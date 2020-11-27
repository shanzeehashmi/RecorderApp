package com.zee.recorderapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaCodec;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zee.recorderapp.MyApplication;
import com.zee.recorderapp.R;
import com.zee.recorderapp.db.Recording;
import com.zee.recorderapp.repository.Repository;
import com.zee.recorderapp.viewmodels.HistoryViewModel;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Record extends Fragment {


    CircleImageView start_recording,stop_recording,pause_recording ;

    private static final String LOG_TAG = "recordzee";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;


    private MediaRecorder recorder = null;

    private MediaPlayer player = null;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    TextView status_text,pause_text ;


    Repository repository ;
    HistoryViewModel historyViewModel ;


    public Record() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        start_recording = view.findViewById(R.id.start_recording) ;
        stop_recording = view.findViewById(R.id.stop_recording) ;
        pause_recording = view.findViewById(R.id.pause_recording) ;

        status_text = view.findViewById(R.id.status_text) ;
        pause_text = view.findViewById(R.id.pause_text) ;


        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class) ;
        repository = historyViewModel.getRepository() ;

        start_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileName = getActivity().getExternalCacheDir().getAbsolutePath();
                fileName += "/aud_rec_"+System.currentTimeMillis()+".3gp";

                status_text.setText("Preparing ..");
               onRecord(true);
            }
        });

        pause_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseRecording() ;
            }
        });

        stop_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(false);
            }
        });


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted )
        {
            Toast.makeText(getActivity(),"Permission required to run the app",Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }


    private void pauseRecording()
    {
        if(recorder != null)
        {
            status_text.setText("Pausing..");

            if(pause_text.getText().toString().equalsIgnoreCase("Resume"))
            {
                resumeRecording();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder.pause();
            }else
            {
                Toast.makeText(getActivity(),"Pause/Resume is only allowed from API level 24 ",Toast.LENGTH_SHORT).show();
            }

            status_text.setText("Recording Paused !");
            pause_text.setText("Resume");
        }
    }

    private void resumeRecording()
    {
        if(recorder != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder.resume();
            }else
            {
                Toast.makeText(getActivity(),"Pause/Resume is only allowed from API level 24 ",Toast.LENGTH_SHORT).show();
            }

            status_text.setText("Recording..");
            pause_text.setText("Pause");
        }
    }


    private void startRecording() {

        if(recordingStarted != null)
        {
            recordingStarted.recordingStarted();
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);



        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.i(LOG_TAG, "prepare() failed");
        }

        recorder.start();

        status_text.setText("Recording..");


    }

    private void stopRecording() {

       if(recorder !=null)
       {
           recorder.stop();
           recorder.reset();
           recorder.release();
           recorder = null;

           status_text.setText("Recording Done ! And Saved To HISTORY ");

           Recording rec =  new Recording("Rec001"+ System.currentTimeMillis(),fileName+"") ;
           repository.InsertRecording(rec);

       }


    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }


    private RecordingStarted recordingStarted ;

    public void setRecordingStarted(RecordingStarted recordingStarted) {
        this.recordingStarted = recordingStarted;
    }

    public interface RecordingStarted
    {
        void recordingStarted() ;
    }


}