package com.zee.recorderapp.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recording_table")
public class Recording {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id  ;


    @NonNull
    @ColumnInfo(name = "recording_name")
    private String recording_name ;


    @ColumnInfo(name = "recording_path")
    private String recording_path ;

    public Recording(@NonNull String recording_name, @NonNull String recording_path)
    {
        this.recording_name = recording_name ;
        this.recording_path = recording_path ;

    }

    public String getRecording_name() {
        return recording_name;
    }

    public String getRecording_path() {
        return recording_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
