package com.thesejongproject.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ankushgrover.hourglass.Hourglass;
import com.thesejongproject.R;
import com.thesejongproject.smart.SmartApplication;

import java.util.Timer;

public class TimerService extends Service {
    private static Timer timer = new Timer();
    private static TimerService self = null;
    private final Handler toastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
        }
    };
    private static Context ctx;

    private Hourglass hourglass;
   // private static boolean isDestroy = false;

    public static TimerService getServiceObject() {
        return (TimerService) ctx;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        ctx = this;
        startService();
    }

    private void startService() {
        if (hourglass == null) {
            hourglass = new Hourglass(getResources().getInteger(R.integer.ad_load_time), 1000) {
                @Override
                public void onTimerTick(long timeRemaining) {
                    // Update UI
                    Log.i("timeremaining", ">>>>>>" + timeRemaining);
                    if(timeRemaining == 0){
                       // isDestroy = false;
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("isHitTimer", true);
                    }
                }

                @Override
                public void onTimerFinish() {
                    // Timer finished
                    /*if(!isDestroy){

                    }*/

                   // Log.i("timeremaining", "Timer finished>>>>>>");
                }
            };
        }
    }

    public Hourglass getHourglass() {
        //if(self.is)
        if(hourglass != null){
            return hourglass;
        }else {
            return null;
        }

    }

    public void onDestroy() {
        super.onDestroy();
      //  Log.i("On",">>>>>>>onDestroy service");
        //isDestroy = true;
        hourglass.stopTimer();

    }

}
