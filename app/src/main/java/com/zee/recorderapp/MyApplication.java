package com.zee.recorderapp;

public class MyApplication extends android.app.Application {

    private static MyApplication instance ;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this ;

    }

    public static MyApplication getInstance()
    {
        return instance ;
    }

}
