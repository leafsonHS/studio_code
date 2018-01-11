package com.leafson.lifecycle.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;

public class JChatDemoApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("JpushDemoApplication", "init");
    }


}
