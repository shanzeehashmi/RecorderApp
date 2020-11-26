package com.zee.recorderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.zee.recorderapp.adapter.HistoryRecyclerAdapter;
import com.zee.recorderapp.adapter.PagerAdapter;
import com.zee.recorderapp.db.Recording;
import com.zee.recorderapp.fragments.History;
import com.zee.recorderapp.fragments.Record;
import com.zee.recorderapp.repository.Repository;
import com.zee.recorderapp.services.PlayerService;
import com.zee.recorderapp.viewmodels.HistoryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements History.RecordingClicked , View.OnClickListener , PlayerService.PlayingCompleted , Record.RecordingStarted {

    ViewPager viewPager ;
    TabLayout tabs ;
    RelativeLayout media_player_layout ;

    private PlayerService playerService ;
    boolean isBound ;

    PlayerServiceConnection playerServiceConnection ;

    CircleImageView shuffle_button,repeat_button, prev_button, play_pause_button, next_button ;

    Repository repository ;
    HistoryViewModel historyViewModel ;

    Recording currently_playing ;

    Record record = new Record() ;
    History history = new History() ;

    boolean SHUFFLE = false ;

    boolean playing = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager) ;
        tabs = findViewById(R.id.tabs) ;
        media_player_layout = findViewById(R.id.media_player_layout) ;
        media_player_layout.setVisibility(View.GONE);

        shuffle_button = findViewById(R.id.shuffle_button) ;
        repeat_button = findViewById(R.id.repeat_button) ;
        prev_button = findViewById(R.id.prev_button) ;
        play_pause_button = findViewById(R.id.play_pause_button) ;
        next_button = findViewById(R.id.next_button) ;



        PagerAdapter sectionsPagerAdapter = new PagerAdapter(this,getSupportFragmentManager()) ;
        sectionsPagerAdapter.addFragment(record,"Record");
        sectionsPagerAdapter.addFragment(history,"History");
        viewPager.setAdapter(sectionsPagerAdapter);

        tabs.setupWithViewPager(viewPager);

        history.setRecordingClicked(this);
        record.setRecordingStarted(this);

        shuffle_button.setOnClickListener(this);
        repeat_button.setOnClickListener(this);
        prev_button.setOnClickListener(this);
        play_pause_button.setOnClickListener(this);
        next_button.setOnClickListener(this);

        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class) ;
        repository = historyViewModel.getRepository() ;

    }



    @Override
    protected void onStart() {
        super.onStart();

        playerServiceConnection = new PlayerServiceConnection() ;

        Intent intent = new Intent(this , PlayerService.class);
        startService(intent);
        bindService(intent , playerServiceConnection,BIND_AUTO_CREATE);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBound){
            unbindService(playerServiceConnection);
            isBound = false;


        }
    }



    @Override
    public void onBackPressed() {

        if(viewPager.getCurrentItem()==0)
        {
            super.onBackPressed();
        }else
        {
            viewPager.setCurrentItem(0);
        }

    }

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.shuffle_button :

                if(SHUFFLE)
                {
                    SHUFFLE = false ;
                    if(isBound)
                    {
                        playerService.setSUFFLED(false,history.getRecordings());
                    }
                    shuffle_button.setAlpha((float)0.3);
                }else
                {
                    SHUFFLE = true ;
                    if(isBound)
                    {
                        playerService.setSUFFLED(true,history.getRecordings());
                    }
                    shuffle_button.setAlpha((float)1);
                }

                break ;
            case R.id.repeat_button :
                if(isBound)
                {
                    if(playerService.getLooping())
                    {
                        playerService.setLooping(false);
                        repeat_button.setAlpha((float)0.3);
                    }
                      else
                    {
                        if(playerService.isPlaying())
                        {
                            playerService.setLooping(true);
                            repeat_button.setAlpha((float)1);
                        }

                    }
                }
                break ;
            case R.id.prev_button :

                if(currently_playing != null)
                {
                    if(isBound)
                    {
                        List<Recording> all_recordings = history.getRecordings() ;

                        Recording recording1 =  getPreviousTrack(all_recordings,currently_playing) ;


                        if(recording1 != null )
                        {
                            if(isBound)
                            {
                                playerService.startPlaying(recording1);
                            }
                        }else
                        {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_play_circle));
                            }
                        }

                    }
                }

                break ;
            case R.id.play_pause_button :
                        if(isBound)
                        {
                            if(playerService.isPlaying())
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_play_circle));
                                }

                                playerService.pausePlaying();

                            }else
                            {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_pause));
                                }
                              playing =   playerService.resumePlaying() ;

                                if(!playing)
                                {
                                    playerService.startPlaying(currently_playing);
                                }

                            }

                        }
                break ;
            case R.id.next_button :

                if(currently_playing != null)
                {
                    if(isBound)
                    {
                        List<Recording> all_recordings = history.getRecordings() ;

                        Recording recording1 =  getNextTrack(all_recordings,currently_playing) ;


                        if(recording1 != null )
                        {
                            if(isBound)
                            {
                                playerService.startPlaying(recording1);
                            }
                        }else
                        {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_play_circle));
                            }
                        }

                    }
                }

                break ;
        }

    }

    @Override
    public void playingCompleted(Recording recording) {

           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
               play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_play_circle));
           }

    }





    private Recording getNextTrack(List<Recording> data , Recording recording)
    {
        int index = data.indexOf(recording) ;

        if(index >= data.size()-1)
        {
            return null ;
        }else
        {
            return data.get(index+1) ;
        }

    }

    private Recording getPreviousTrack(List<Recording> data , Recording recording)
    {
        int index = data.indexOf(recording) ;

        if(index == 0)
        {
            return null ;
        }else
        {
            return data.get(index-1) ;
        }

    }


    @Override
    public void startedPlaying(Recording recording) {

        currently_playing = recording ;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_pause));
        }
    }

    @Override
    public void recordingStarted() {
        if(isBound)
        {
            if(playerService.isPlaying())
            {
                playerService.pausePlaying();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_play_circle));
                }
            }
        }
    }


    private class PlayerServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            PlayerService.PlayerServiceBinder binder = (PlayerService.PlayerServiceBinder) service;
            playerService = binder.getService();
            playerService.setPlayingCompleted(MainActivity.this);
            isBound = true ;

            checkToShowMediaPlayer() ;

        }


    }

    void checkToShowMediaPlayer()
    {
        if(isBound)
        {
            if(playerService.isPlaying())
            {
                media_player_layout.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_pause));
                }
            }else
            {
                media_player_layout.setVisibility(View.GONE);
            }
        }

    }



    @Override
    public void clicked(Recording recording) {
        if(isBound)
        {
            if(playerService.isPlaying())
            {
                playerService.stopPlaying();
                playerService.startPlaying(recording);
            }
            else
            {
                playerService.startPlaying(recording);

            }

            checkToShowMediaPlayer() ;

        }
    }


}