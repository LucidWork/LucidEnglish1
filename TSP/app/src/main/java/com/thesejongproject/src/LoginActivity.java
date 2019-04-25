package com.thesejongproject.src;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.thesejongproject.R;
import com.thesejongproject.customviews.SmartEditText;
import com.thesejongproject.customviews.SmartTextView;
import com.thesejongproject.smart.AlertNeutral;
import com.thesejongproject.smart.Constants;
import com.thesejongproject.smart.SmartApplication;
import com.thesejongproject.weservice.SmartWebManager;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by ebiztrait on 31/1/17.
 */

public class LoginActivity extends BaseActivity {

    private SmartEditText edtEmail;
    private SmartEditText edtPassword;
    private SmartTextView btnLogin;
    private SmartTextView btnForgotPassword;
    private SmartTextView btnSignUp;

    @Override
    public int getLayoutID() {
        return R.layout.tsp_login_screen;
    }

    @Override
    public void initComponents() {

        edtEmail = (SmartEditText) findViewById(R.id.edtEmail);
        edtPassword = (SmartEditText) findViewById(R.id.edtPassword);
        btnLogin = (SmartTextView) findViewById(R.id.btnLogin);
        btnForgotPassword = (SmartTextView) findViewById(R.id.btnForgotPassword);
        btnSignUp = (SmartTextView) findViewById(R.id.btnSignUp);
    }

    @Override
    public void prepareViews() {

    }

    @Override
    public void setActionListeners() {

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isValidated = true;

                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    isValidated = false;
                    edtEmail.setError(getString(R.string.validation_value_required));
                } else if (!emailValidator(edtEmail.getText().toString())) {
                    isValidated = false;
                    ting(getString(R.string.validation_invalid_email));
                } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    isValidated = false;
                    edtPassword.setError(getString(R.string.validation_value_required));
                }

                if (isValidated) {

                    authenticateUser();
                }
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent forgotPasswordIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });
    }

    private void authenticateUser() {
        try {
            showLoadingDialog(LoginActivity.this);

            JSONObject params = new JSONObject();
            params.put("Email", edtEmail.getText().toString());
            params.put("Password", edtPassword.getText().toString());

            HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, LoginActivity.this);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url) + getString(R.string.login_url));
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                @Override
                public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                    hideLoadingDialog();
                    if (responseCode == 200) {
                        try {
                            JSONObject result = response.getJSONArray("Results").getJSONObject(0);
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_USER, result.toString());
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_USER_ID, result.getString("UserID"));
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_WORD_HOUSE, result.getJSONArray("WordHouse").toString());
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_IS_MANUAL_MODE, result.getInt("IsManualMode"));
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HIT_TIMER, false);
                            Intent exerciseIntent = new Intent(LoginActivity.this, MenuActivity.class);
                            exerciseIntent.putExtra("isLogin",true);
                            startActivity(exerciseIntent);
                            supportFinishAfterTransition();
                            /*displayAd(LoginActivity.this, true);*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            getOKDialog(LoginActivity.this, response.getString("ErrorMessage"),
                                    getString(R.string.ok), true, new AlertNeutral() {
                                @Override
                                public void NeutralMathod(DialogInterface dialog, int id) {

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onResponseError() {

                }
            });
            SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar) {

    }
}
