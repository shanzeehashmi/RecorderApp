package com.zee.recorderapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zee.recorderapp.MyApplication;
import com.zee.recorderapp.db.Recording;
import com.zee.recorderapp.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private Repository repository ;
    private LiveData<List<Recording>> data = new MutableLiveData<>();
    private LiveData<Boolean> processing = new MutableLiveData<>() ;


    public HistoryViewModel()
   {
       super(MyApplication.getInstance());

       repository = new Repository(MyApplication.getInstance()) ;
       data = repository.getAll_recordings() ;
       processing = repository.getProcessing() ;

   }


    public LiveData<List<Recording>> getData() {
        return data;
    }

    public LiveData<Boolean> getProcessing() {
        return processing;
    }

    public Repository getRepository() {
        return repository;
    }
}
