package com.thesejongproject.src;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.thesejongproject.R;
import com.thesejongproject.smart.AlertNeutral;
import com.thesejongproject.smart.Constants;
import com.thesejongproject.smart.SmartApplication;
import com.thesejongproject.smart.Utility;
import com.thesejongproject.weservice.SmartWebManager;

import org.json.JSONObject;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity implements Constants {

    public static String TestDeviceId = "";
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private ProgressBar progress;
    private Handler splashHandler = new Handler();
    private Runnable runnable;

    public void getOKDialog(Context context, String msg, String buttonCaption,
                            boolean isCancelable, final AlertNeutral target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.app_name))
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(buttonCaption, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        target.NeutralMathod(dialog, id);
                    }
                });

        AlertDialog alert = builder.create();
        alert.setCancelable(isCancelable);
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        TestDeviceId = /*"159753";*/  Utility.getUniqueDeviceID(SplashActivity.this);


        progress = (ProgressBar) findViewById(R.id.progress);

       /* if (TextUtils.isEmpty(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().
                getString(SP_LOGGED_USER_ID, null))) {

            runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        authenticateUser(TestDeviceId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            splashHandler.postDelayed(runnable, SPLASH_DISPLAY_LENGTH);
        }*/

        try {
            authenticateUser(TestDeviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerUser(String deviceId) {
        Log.e("!_@_@", "-----------go for register--------");
        try {
            progress.setVisibility(View.VISIBLE);

            JSONObject params = new JSONObject();
            params.put("UserId", "");
            params.put("Firstname", "");
            params.put("Lastname", "");
            params.put("Username", "");
            params.put("Email", "");
            params.put("Password", "");
            params.put("DeviceId", deviceId);

            HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, SplashActivity.this);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url) + getString(R.string.registration_url));
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_REGISTRATION);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                @Override
                public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                    progress.setVisibility(View.GONE);

                    if (responseCode == 200) {
                        try {
                            startMenuScreen(response, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            getOKDialog(SplashActivity.this, "Error while fetching data.",
                                    getString(R.string.ok), false,
                                    new AlertNeutral() {
                                        @Override
                                        public void NeutralMathod(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e("!_@_@", "-----------error while register--------");
                    }
                }

                @Override
                public void onResponseError() {
                    try {
                        getOKDialog(SplashActivity.this, "Error while fetching data.", getString(R.string.ok), false,
                                new AlertNeutral() {
                                    @Override
                                    public void NeutralMathod(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startMenuScreen(JSONObject response, boolean isNewUser) {
        try {
            JSONObject result = response.getJSONArray("Results").getJSONObject(0);
            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_USER, result.toString());
            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_USER_ID, result.getString("UserID"));
            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_WORD_HOUSE, result.getJSONArray("WordHouse").toString());
            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_IS_MANUAL_MODE, result.getInt("IsManualMode"));
//            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_IS_MANUAL_MODE, 0);

            if (isNewUser) {
                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HIT_TIMER, false);
                Intent intent = new Intent(SplashActivity.this, TutorialActivity.class);
                intent.putExtra("is_signup", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
              /*  Intent exerciseIntent = new Intent(SplashActivity.this, MenuActivity.class);
                exerciseIntent.putExtra("isLogin", true);
                startActivity(exerciseIntent);
                supportFinishAfterTransition();*/

                Intent exerciseIntent = new Intent(SplashActivity.this, ExerciseActivity.class);
                exerciseIntent.putExtra("isLogin", true);
                startActivity(exerciseIntent);
                supportFinishAfterTransition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void authenticateUser(String deviceId) throws Exception {
        progress.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();
//        params.put("UserID", SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER_ID, null));
//        params.put("DeviceId", deviceId);

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, SplashActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url)
                + getString(R.string.auto_login_new_url) + "?DeviceId=" + deviceId);
//                + getString(R.string.auto_login_url));
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER,
                new SmartWebManager.OnResponseReceivedListener() {

                    @Override
                    public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                        progress.setVisibility(View.GONE);
                        try {
                            if (response.has("Status")) {
                                if (response.getBoolean("Status") == true) {
                                    startMenuScreen(response, false);
                                } else {
                                    registerUser(TestDeviceId); // no id found then do registration
                                }
                            } else {
                                if (responseCode == 200) {
                                    if (response.getBoolean("Status") == true) {
                                        startMenuScreen(response, false);
                                    } else {
                                        registerUser(TestDeviceId); // no id found then do registration
                                    }
                                } else {
                                    try {
                                        getOKDialog(SplashActivity.this, "Error while fetching data.",
                                                getString(R.string.ok), false,
                                                new AlertNeutral() {
                                                    @Override
                                                    public void NeutralMathod(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("!_@_@", "-----issue with auto logi-------");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponseError() {
                        progress.setVisibility(View.GONE);
                        try {
                            getOKDialog(SplashActivity.this, "Error while fetching data.", getString(R.string.ok), false,
                                    new AlertNeutral() {
                                        @Override
                                        public void NeutralMathod(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e("!_@_@", "-----EROOR issue with auto logi-------");
                    }
                });
        SmartWebManager.getInstance(getApplicationContext()).addToGETRequestQueue(requestParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (splashHandler != null) {
            splashHandler.removeCallbacks(runnable);
        }
    }
}
