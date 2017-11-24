package com.blog.ljtatum.androidx.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.app.framework.utilities.FrameworkUtils;
import com.blog.ljtatum.androidx.gui.iPhoneNotch;

/**
 * Created by LJTat on 11/4/2017.
 */

public class iPhoneNotchService extends Service {

    private iPhoneNotch mPhoneNotchView;
    private static boolean isRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            // set flag to true
            isRunning = true;
            // initialize iphone notch view
            mPhoneNotchView = new iPhoneNotch(this);
            // start service
            return START_STICKY;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (!FrameworkUtils.checkIfNull(mPhoneNotchView) && isRunning) {
            isRunning = false;
            mPhoneNotchView.destroy();
            mPhoneNotchView = null;
            // invoke garbage collector
            System.gc();
        }
        stopForeground(true);
        stopSelf();
    }
}
