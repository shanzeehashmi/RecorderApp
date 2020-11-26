package com.zee.recorderapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface dataaccessobject {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRecording(Recording recording);

    @Query("SELECT * FROM recording_table ")
    LiveData<List<Recording>> getRecordings() ;



}
