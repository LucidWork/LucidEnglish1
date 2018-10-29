package com.thesejongproject.src;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.thesejongproject.R;
import com.thesejongproject.smart.Constants;
import com.thesejongproject.smart.SmartApplication;
import com.thesejongproject.weservice.SmartWebManager;

import org.json.JSONObject;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity implements Constants {

    private ProgressBar progress;

    private Handler splashHandler = new Handler();
    private Runnable runnable;

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);



        progress = (ProgressBar) findViewById(R.id.progress);

        if (TextUtils.isEmpty(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER_ID, null))) {

            runnable = new Runnable() {
                @Override
                public void run() {

                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(loginIntent);

                    supportFinishAfterTransition();
                }
            };
            splashHandler.postDelayed(runnable, SPLASH_DISPLAY_LENGTH);
        } else {
            try {
                authenticateUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void authenticateUser() throws Exception {
        progress.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();
        params.put("UserID", SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER_ID, null));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, SplashActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url) + getString(R.string.auto_login_url));
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                progress.setVisibility(View.GONE);
                if (responseCode == 200) {
                    try {
                        JSONObject result = response.getJSONArray("Results").getJSONObject(0);
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_USER, result.toString());
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_USER_ID, result.getString("UserID"));
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_WORD_HOUSE, result.getJSONArray("WordHouse").toString());
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_IS_MANUAL_MODE, result.getInt("IsManualMode"));

                        /*Intent exerciseIntent = new Intent(SplashActivity.this, ExerciseActivity.class);
                        startActivity(exerciseIntent);

                        supportFinishAfterTransition();*/

                       /* if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getBoolean(SP_HIT_TIMER, false)) {
                            final InterstitialAd mInterstitialAd = new InterstitialAd(SplashActivity.this);
                            mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_unit_id));
                            AdRequest adRequest = new AdRequest.Builder().build();
                            mInterstitialAd.loadAd(adRequest);
                            mInterstitialAd.setAdListener(new AdListener() {

                                @Override
                                public void onAdLoaded() {
                                    mInterstitialAd.show();
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HIT_TIMER, false);
                                }

                                @Override
                                public void onAdClosed() {
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HIT_TIMER, false);
                                    Intent exerciseIntent = new Intent(SplashActivity.this, ExerciseActivity.class);
                                    exerciseIntent.putExtra("isLogin",true);
                                    startActivity(exerciseIntent);
                                    supportFinishAfterTransition();
                                }

                                @Override
                                public void onAdFailedToLoad(int errorCode) {
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HIT_TIMER, false);
                                    Intent exerciseIntent = new Intent(SplashActivity.this, ExerciseActivity.class);
                                    exerciseIntent.putExtra("isLogin",true);
                                    startActivity(exerciseIntent);
                                    supportFinishAfterTransition();

                                }

                                @Override
                                public void onAdLeftApplication() {
                                }

                                @Override
                                public void onAdOpened() {

                                }
                            });
                        }else {*/
                            //SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HIT_TIMER, false);
                            Intent exerciseIntent = new Intent(SplashActivity.this, ExerciseActivity.class);
                            exerciseIntent.putExtra("isLogin",true);
                            startActivity(exerciseIntent);
                            supportFinishAfterTransition();
                       // }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(loginIntent);

                    supportFinishAfterTransition();
                }
            }

            @Override
            public void onResponseError() {
                progress.setVisibility(View.GONE);

                Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(loginIntent);

                supportFinishAfterTransition();
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (splashHandler != null) {
            splashHandler.removeCallbacks(runnable);
        }
    }
}
