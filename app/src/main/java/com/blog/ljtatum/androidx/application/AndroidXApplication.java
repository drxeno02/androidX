package com.blog.ljtatum.androidx.application;

import android.app.Application;

import com.blog.ljtatum.androidx.BuildConfig;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;

/**
 * Created by leonard on 11/3/2017.
 */

public class AndroidXApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // instantiate crashlytics
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build();
        Fabric.with(this, crashlyticsKit);
    }

}
