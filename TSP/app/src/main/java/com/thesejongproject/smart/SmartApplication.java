package com.thesejongproject.smart;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.crash.FirebaseCrash;
import com.thesejongproject.R;
import com.thesejongproject.service.TimerService;

import java.io.IOException;

public class SmartApplication extends Application {

    public static SmartApplication REF_SMART_APPLICATION;

    private SharedPreferences sharedPreferences;

    private SmartDataHelper dataHelper;

    public Typeface FONT;

    public Typeface BOLDFONT;
    public static Intent lintent;

    @Override
    public void onCreate() {
        super.onCreate();

        REF_SMART_APPLICATION = this;

        FirebaseCrash.report(new Exception());
        lintent = new Intent(this, TimerService.class);
        startService(lintent);
// Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        try {
            dataHelper = new SmartDataHelper(getApplicationContext(), getString(R.string.app_name), 1, getString(R.string.app_name) + ".sql");
        } catch (IOException e) {
            e.printStackTrace();
        }

        SmartApplication.REF_SMART_APPLICATION.FONT = Typeface.createFromAsset(getAssets(), getString(R.string.font_normal));

        SmartApplication.REF_SMART_APPLICATION.BOLDFONT = Typeface.createFromAsset(getAssets(), getString(R.string.font_bold));
    }

    /**
     * This method will return instance of <b>SharedPreferences</b> generated by
     * SmartFramework. Framework will use SharedPreference name as given in
     * <b>ApplicationConfiguration</b> for generation of SharedPreference.
     * <b>Note</b> : SharedPreference Mode will be private whenever generated by
     * SmartFramework.
     *
     * @return sharedPreferences = Instance of SharedPreferences created by
     * SmartFramework.
     */
    public SharedPreferences readSharedPreferences() {
        return sharedPreferences;
    }

    /**
     * This method will write to <b>SharedPreferences</b>.
     *
     * @param key   = String <b>key</b> to store in <b>SharedPreferences</b>.
     * @param value = String <b>value</b> to store in <b>SharedPreferences</b>.
     */
    public void writeSharedPreferences(String key, String value) {
        SharedPreferences.Editor editor = readSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * This method will write to <b>SharedPreferences</b>.
     *
     * @param key   = String <b>key</b> to store in <b>SharedPreferences</b>.
     * @param value = boolean <b>value</b> to store in <b>SharedPreferences</b>.
     */
    public void writeSharedPreferences(String key, boolean value) {
        SharedPreferences.Editor editor = readSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * This method will write to <b>SharedPreferences</b>.
     *
     * @param key   = String <b>key</b> to store in <b>SharedPreferences</b>.
     * @param value = float <b>value</b> to store in <b>SharedPreferences</b>.
     */
    public void writeSharedPreferences(String key, float value) {
        SharedPreferences.Editor editor = readSharedPreferences().edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * This method will write to <b>SharedPreferences</b>.
     *
     * @param key   = String <b>key</b> to store in <b>SharedPreferences</b>.
     * @param value = int <b>value</b> to store in <b>SharedPreferences</b>.
     */
    public void writeSharedPreferences(String key, int value) {
        SharedPreferences.Editor editor = readSharedPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * This method will write to <b>SharedPreferences</b>.
     *
     * @param key   = String <b>key</b> to store in <b>SharedPreferences</b>.
     * @param value = long <b>value</b> to store in <b>SharedPreferences</b>.
     */
    public void writeSharedPreferences(String key, long value) {
        SharedPreferences.Editor editor = readSharedPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * This method will return instance of <b>SmartDataHelper</b> which is
     * currently being used by the SmartFramework.<br>
     * This method will return <b>null</b>, if <b>isDBEnabled</b> flag is false
     * in <b>ApplicationConfiguration</b>.
     *
     * @return dataHelper = Instance of <b>SmartDataHelper</b>.
     */
    public SmartDataHelper getDataHelper() {
        return dataHelper;
    }
}
