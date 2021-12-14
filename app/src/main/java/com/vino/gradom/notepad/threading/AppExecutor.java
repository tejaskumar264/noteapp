package com.vino.gradom.notepad.threading;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutor {
    private static AppExecutor instance;
    private final Executor mainThread;
    private final Executor subThread;

    public AppExecutor(Executor mainThread, Executor subThread) {
        this.mainThread = mainThread;
        this.subThread = subThread;
    }

    public static AppExecutor getInstance(){
        if (instance == null){
            instance = new AppExecutor(new MainThreadHandler(), Executors.newSingleThreadExecutor());
        }
        return instance;
    }

    public Executor getMainThread() {
        return mainThread;
    }

    public Executor getSubThread() {
        return subThread;
    }
}
