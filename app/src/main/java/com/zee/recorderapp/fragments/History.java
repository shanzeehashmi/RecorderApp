package com.zee.recorderapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zee.recorderapp.MainActivity;
import com.zee.recorderapp.MyApplication;
import com.zee.recorderapp.R;
import com.zee.recorderapp.adapter.HistoryRecyclerAdapter;
import com.zee.recorderapp.db.Recording;
import com.zee.recorderapp.repository.Repository;
import com.zee.recorderapp.viewmodels.HistoryViewModel;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class History extends Fragment implements HistoryRecyclerAdapter.RecordingClicked {

    List<Recording> recordings = new ArrayList<>() ;
    HistoryViewModel historyViewModel ;
    ProgressBar progress_circular ;
    Repository repository ;



    public History() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class) ;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progress_circular = view.findViewById(R.id.progress_circular) ;
        progress_circular.setVisibility(View.VISIBLE);


        RecyclerView recyclerView = view.findViewById(R.id.history_recycler_view);
        HistoryRecyclerAdapter adapter = new HistoryRecyclerAdapter(getActivity(),recordings);
        adapter.setRecordingClicked(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        historyViewModel.getProcessing().observe(this,processing->{

            Log.i("Historyzeeeeee","processing value changed") ;

            if(processing)
            {
                progress_circular.setVisibility(View.VISIBLE);
            }
            else
            {
                progress_circular.setVisibility(View.GONE);
            }

        });

    historyViewModel.getData().observe(this,data->{

        Log.i("value_changed","yesssssssss " +data.size()) ;

        int size = recordings.size() ;
        recordings.clear();
        recordings.addAll(data) ;

        progress_circular.setVisibility(View.GONE);

        if(size>0)
        {
            adapter.notifyItemChanged(size-1);
        }
        else
        {
            adapter.notifyDataSetChanged();
        }

    });



    }

    @Override
    public void clicked(Recording recording) {
        if(recordingClicked!=null)
        {
            recordingClicked.clicked(recording);
        }
    }

    private RecordingClicked recordingClicked ;
    public interface RecordingClicked{
        void clicked(Recording recording) ;
    }

    public void setRecordingClicked(RecordingClicked recordingClicked) {
        this.recordingClicked = recordingClicked;
    }

    public List<Recording> getRecordings()
    {
        return recordings;
    }

}