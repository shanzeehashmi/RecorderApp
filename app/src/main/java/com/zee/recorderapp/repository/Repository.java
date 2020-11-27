package com.zee.recorderapp.repository;


import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zee.recorderapp.db.Database;
import com.zee.recorderapp.db.Recording;
import com.zee.recorderapp.db.dataaccessobject;

import java.util.List;

public class Repository {

    private dataaccessobject  dao;
    private LiveData<List<Recording>> all_recordings  ;
    private MutableLiveData<Boolean> processing = new MutableLiveData<>();

    public Repository(Application application)
    {
        Database database = Database.getDatabase(application) ;
        dao = database.dataaccessobject() ;

        all_recordings = dao.getRecordings() ;


    }

    public LiveData<List<Recording>> getAll_recordings() {
        return all_recordings;
    }

    public LiveData<Boolean> getProcessing()
    {
        return  processing ;
    }

    public void InsertRecording(final Recording recording)
    {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                processing.setValue(true);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                processing.setValue(false);
            }

            @Override
            protected Void doInBackground(Void... voids) {

                Database.databaseWriteExecutor.execute(()->
                {
                    dao.insertRecording(recording);
                });
                return null;
            }
        }.execute();


    }



}
