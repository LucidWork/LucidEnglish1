package com.thesejongproject.src;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.thesejongproject.R;
import com.thesejongproject.customviews.SmartEditText;
import com.thesejongproject.customviews.SmartTextView;
import com.thesejongproject.smart.Constants;
import com.thesejongproject.smart.SmartApplication;
import com.thesejongproject.weservice.SmartWebManager;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by ebiztrait on 31/1/17.
 */

public class SignUpActivity extends BaseActivity {

    private SmartEditText edtFirstName;
    private SmartEditText edtLastName;
    private SmartEditText edtUsername;
    private SmartEditText edtEmail;
    private SmartEditText edtPassword;
    private SmartEditText edtConfirmPassword;
    private SmartTextView btnSignUp;

    @Override
    public int getLayoutID() {
        return R.layout.tsp_signup_screen;
    }

    @Override
    public void initComponents() {

        edtFirstName = (SmartEditText) findViewById(R.id.edtFirstName);
        edtLastName = (SmartEditText) findViewById(R.id.edtLastName);
        edtUsername = (SmartEditText) findViewById(R.id.edtUsername);
        edtEmail = (SmartEditText) findViewById(R.id.edtEmail);
        edtPassword = (SmartEditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (SmartEditText) findViewById(R.id.edtConfirmPassword);
        btnSignUp = (SmartTextView) findViewById(R.id.btnSignUp);
    }

    @Override
    public void prepareViews() {

    }

    @Override
    public void setActionListeners() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValidated = true;

                if (TextUtils.isEmpty(edtFirstName.getText().toString())) {
                    isValidated = false;
                    edtFirstName.setError(getString(R.string.validation_value_required));
                } else if (TextUtils.isEmpty(edtLastName.getText().toString())) {
                    isValidated = false;
                    edtLastName.setError(getString(R.string.validation_value_required));
                } else if (TextUtils.isEmpty(edtUsername.getText().toString())) {
                    isValidated = false;
                    edtUsername.setError(getString(R.string.validation_value_required));
                } else if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    isValidated = false;
                    edtEmail.setError(getString(R.string.validation_value_required));
                } else if (!emailValidator(edtEmail.getText().toString())) {
                    isValidated = false;
                    ting(getString(R.string.validation_invalid_email));
                } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    isValidated = false;
                    edtPassword.setError(getString(R.string.validation_value_required));
                } else if (TextUtils.isEmpty(edtConfirmPassword.getText().toString())) {
                    isValidated = false;
                    edtConfirmPassword.setError(getString(R.string.validation_value_required));
                } else if (!edtConfirmPassword.getText().toString().equalsIgnoreCase(edtPassword.getText().toString())) {
                    isValidated = false;
                    ting(getString(R.string.validation_password_not_matches));
                }

                if (isValidated) {

                    registerNewUser();
                }
            }
        });
    }

    private void registerNewUser() {
        try {
            showLoadingDialog(SignUpActivity.this);

            JSONObject params = new JSONObject();
            params.put("UserID", "0");
            params.put("Firstname", edtFirstName.getText().toString());
            params.put("Lastname", edtLastName.getText().toString());
            params.put("Username", edtUsername.getText().toString());
            params.put("Email", edtEmail.getText().toString());
            params.put("Password", edtPassword.getText().toString());

            HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url) + getString(R.string.registration_url));
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, SignUpActivity.this);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_REGISTRATION);
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

                            Intent intent = new Intent(SignUpActivity.this, TutorialActivity.class);
                            intent.putExtra("is_signup",true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            //supportFinishAfterTransition();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {

                        try {
                            tong(response.getString("ErrorMessage"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onResponseError() {

                    hideLoadingDialog();
                }
            });
            SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams);
        } catch (Exception e) {
            e.printStackTrace();

            hideLoadingDialog();
        }
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                supportFinishAfterTransition();
            }
        });
    }
}
