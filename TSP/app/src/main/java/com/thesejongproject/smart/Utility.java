package com.thesejongproject.smart;

import android.content.Context;
import android.provider.Settings;

public class Utility {

    public static String getUniqueDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
