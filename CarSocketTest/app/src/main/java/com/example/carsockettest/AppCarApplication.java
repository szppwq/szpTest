package com.example.carsockettest;

import android.app.Application;
import android.content.Context;


public class AppCarApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//        CrashReport.initCrashReport(getApplicationContext(), "4433c4e038", true);

    }
    public static Context getContext(){
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}