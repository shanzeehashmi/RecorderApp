package com.zee.recorderapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zee.recorderapp.R;
import com.zee.recorderapp.db.Recording;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.ZeeViewHolder> {

    List<Recording> recordings ;
    Context context ;

    public HistoryRecyclerAdapter(Context context, List<Recording> recordings)
    {
        this.recordings = recordings ;
        this.context = context ;

    }



    @NonNull
    @Override
    public ZeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_history_recording, parent, false);

        return new ZeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ZeeViewHolder holder, final int position) {

        Recording recording = recordings.get(position) ;

        holder.recording_name.setText("Recording "+ recording.getId());

        holder.recording_layout.setOnClickListener(v -> {
            if(recordingClicked != null)
            {
                recordingClicked.clicked(recording);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    public static class ZeeViewHolder extends RecyclerView.ViewHolder {

        CircleImageView play_pause_button ;
        TextView recording_name ;
        LinearLayout recording_layout ;

        public ZeeViewHolder(@NonNull View itemView) {
            super(itemView);

            play_pause_button = itemView.findViewById(R.id.play_pause_button) ;
            recording_name = itemView.findViewById(R.id.recording_name) ;
            recording_layout = itemView.findViewById(R.id.recording_layout) ;

        }
    }


    private RecordingClicked recordingClicked ;

    public void setRecordingClicked(RecordingClicked recordingClicked) {
        this.recordingClicked = recordingClicked;
    }

    public interface RecordingClicked
    {
        void clicked(Recording recording) ;
    }


}
