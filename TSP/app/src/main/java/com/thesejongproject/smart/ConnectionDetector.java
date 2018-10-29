package com.thesejongproject.smart;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by chirag on 13/1/17.
 */

public class ConnectionDetector {

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
