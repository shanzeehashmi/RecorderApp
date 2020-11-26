package com.zee.recorderapp.db;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@androidx.room.Database(entities = {Recording.class},version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract dataaccessobject dataaccessobject() ;

    private  static volatile Database database_INSTANCE ;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static Database getDatabase(final Context context) {
        if (database_INSTANCE == null) {
            synchronized (Database.class) {
                if (database_INSTANCE == null) {
                    database_INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            Database.class, "recordings_database")
                            .build();
                }
            }
        }
        return database_INSTANCE;
    }


}
