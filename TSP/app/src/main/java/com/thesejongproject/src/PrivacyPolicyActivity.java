package com.thesejongproject.src;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.thesejongproject.R;
import com.thesejongproject.smart.AlertNeutral;
import com.thesejongproject.smart.Constants;
import com.thesejongproject.smart.SmartApplication;
import com.thesejongproject.smart.Utility;
import com.thesejongproject.weservice.SmartWebManager;

import org.json.JSONObject;

import java.util.HashMap;

public class PrivacyPolicyActivity extends AppCompatActivity implements Constants {

    private String TestDeviceId = "";
    private ProgressBar progress;

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
      /*  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_privacy_policy);

        TestDeviceId = Utility.getUniqueDeviceID(PrivacyPolicyActivity.this);

        progress = findViewById(R.id.progress);
        LinearLayout btnAgree = findViewById(R.id.btnAgree);
        final CheckBox checkBox = findViewById(R.id.checkBox);
        WebView wvPrivacyPolicy = findViewById(R.id.wvPrivacyPolicy);

        LinearLayout llAccept = findViewById(R.id.llAccept);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setVisibility(View.VISIBLE);

        boolean showBack = getIntent().getBooleanExtra("showBack", false);
        if (!showBack) {
            llAccept.setVisibility(View.VISIBLE);
        }


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
            getSupportActionBar().setHomeButtonEnabled(false);

            toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.topbar_bg));

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    supportFinishAfterTransition();
                }
            });
        }

        wvPrivacyPolicy.loadUrl("file:///android_asset/privacy_policy.html");

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    registerUser(TestDeviceId);
                } else {
                    Toast.makeText(PrivacyPolicyActivity.this,
                            "Please accept privacy and policy.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(String deviceId) {
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
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, PrivacyPolicyActivity.this);
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
                            getOKDialog(PrivacyPolicyActivity.this, "Error while fetching data.",
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
                        getOKDialog(PrivacyPolicyActivity.this, "Error while fetching data.",
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
                Intent intent = new Intent(PrivacyPolicyActivity.this, TutorialActivity.class);
                intent.putExtra("is_signup", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Intent exerciseIntent = new Intent(PrivacyPolicyActivity.this, ExerciseActivity.class);
                exerciseIntent.putExtra("isLogin", true);
                startActivity(exerciseIntent);
                supportFinishAfterTransition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
