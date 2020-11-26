package com.zee.recorderapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zee.recorderapp.MainActivity;
import com.zee.recorderapp.MyApplication;
import com.zee.recorderapp.R;
import com.zee.recorderapp.db.Recording;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class PlayerService extends Service  {

    boolean SUFFLED = false ;
    List<Recording> data ;

    private PlayerServiceBinder playerServiceBinder = new PlayerServiceBinder();

    final String TAG = this.getClass().getSimpleName() ;
    private static final int SERVICE_ID = 787;
    private static final String CHANNEL_ID = "player channel";
    private static final String CHANNEL_NAME = "player service";


    MutableLiveData<Integer> duration_remaining  ;
    int total_duration ;

    private MediaPlayer player = null;

    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return playerServiceBinder ;
    }


    public class PlayerServiceBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }


    public LiveData<Integer> remaining_duaration()
    {
        return duration_remaining ;
    }


    public boolean isPlaying()
    {
        if(player!= null)
        {
            return player.isPlaying() ;
        }else
        {
            return false ;
        }
    }

    public void setLooping(boolean looping) {
       if(player!=null)
       {
           player.setLooping(looping);
       }
    }

    public boolean getLooping()
    {
        if(player!=null)
        {
           return player.isLooping();
        }

        return  false ;
    }

    public void setSUFFLED(boolean SUFFLED,List<Recording> data) {
        this.SUFFLED = SUFFLED;
        this.data = data ;
    }

    public boolean isSUFFLED() {
        return SUFFLED;
    }

    public void startPlaying(Recording recording)
    {
        Notification notification = initNotification();
        startForeground(SERVICE_ID, notification);

        Toast.makeText(MyApplication.getInstance(),"Playing: Recording "+recording.getId(),Toast.LENGTH_SHORT).show();

        if(player != null)
        {
            player.isPlaying() ;
            player.stop();
            player.release();
            player = null ;
        }

        player = new MediaPlayer();
        try {
            player.setDataSource(recording.getRecording_path());
            player.prepare();
            player.start();


            if(playingCompleted != null)
            {
                playingCompleted.startedPlaying(recording);
            }


        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if(isSUFFLED())
                {
                    startPlaying(getShuffledTrack(data,recording));
                    return ;
                }

                if(playingCompleted != null)
                {
                    playingCompleted.playingCompleted(recording);
                }

                stopPlaying();

            }
        });

    }

    public void pausePlaying()
    {
        if(player != null)
        {
            player.pause();
        }
    }

    public boolean resumePlaying()
    {
        if(player != null)
        {
            player.start();

            return  true ;

        }
        return false;
    }

    public void  stopPlaying()
    {
        stopForeground(true);
        if(player!=null)
        {
            player.release();
            player = null;
        }

    }

    private Recording getShuffledTrack(List<Recording> data , Recording recording)
    {
        int index = data.indexOf(recording) ;

        if( index == data.size()-1)
        {
            return data.get(index/2) ;
        }

        Random rand = new Random();
        int ran = rand.nextInt(data.size());
        if (ran==index){
            ran= ran+1;
        }


        return data.get(ran) ;


    }


    private Notification initNotification() {

        String notificationTitle ="Recording";
        String notificationText = "Recording is playing";


        Intent intent = new Intent(PlayerService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(PlayerService.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(notificationTitle);
        bigTextStyle.bigText(notificationText);

        String channelID = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                createNotificationChannel(CHANNEL_ID, CHANNEL_NAME)
                : getString(R.string.app_name);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        builder.setStyle(bigTextStyle);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(notificationText);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_LOW) ;
        builder.setContentIntent(pendingIntent) ;

        Bitmap bitmapIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        builder.setLargeIcon(bitmapIcon);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            builder.setPriority(NotificationManager.IMPORTANCE_LOW);
        } else {
            builder.setPriority(Notification.PRIORITY_LOW);
        }

        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelID, String channelName) {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_LOW);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
        return channelID;
    }


    private PlayingCompleted playingCompleted ;

    public void setPlayingCompleted(PlayingCompleted playingCompleted) {
        this.playingCompleted = playingCompleted;
    }

    public interface PlayingCompleted
    {
        void playingCompleted(Recording recording) ;
        void startedPlaying(Recording recording) ;
    }




}
