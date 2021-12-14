package com.vino.gradom.notepad.threading;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

public class MainThreadHandler implements Executor {
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable command) {
        mainHandler.post(command);
    }
}
