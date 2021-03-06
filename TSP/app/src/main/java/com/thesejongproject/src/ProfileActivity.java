package com.thesejongproject.src;

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

public class ProfileActivity extends BaseActivity {

    private SmartTextView btnEdit;

    private SmartEditText edtFirstName;
    private SmartEditText edtLastName;
    private SmartEditText edtUsername;
    private SmartEditText edtEmail;
    private SmartEditText edtPassword;
    private SmartEditText edtConfirmPassword;

    private SmartTextView btnSave;

    @Override
    public int getLayoutID() {
        return R.layout.user_profile_screen;
    }

    @Override
    public void initComponents() {

        btnEdit = (SmartTextView) findViewById(R.id.btnEdit);

        edtFirstName = (SmartEditText) findViewById(R.id.edtFirstName);
        edtLastName = (SmartEditText) findViewById(R.id.edtLastName);
        edtUsername = (SmartEditText) findViewById(R.id.edtUsername);
        edtEmail = (SmartEditText) findViewById(R.id.edtEmail);
        edtPassword = (SmartEditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (SmartEditText) findViewById(R.id.edtConfirmPassword);

        btnSave = (SmartTextView) findViewById(R.id.btnSave);
    }

    @Override
    public void prepareViews() {

        try {
            JSONObject user = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER, null));
            edtFirstName.setText(user.getString("Firstname"));
            edtLastName.setText(user.getString("Lastname"));
            edtUsername.setText(user.getString("Username"));
            edtEmail.setText(user.getString("Email"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setActionListeners() {

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enableDisableView(true);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
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
                    tong(getString(R.string.validation_invalid_email));
                }

                if (!TextUtils.isEmpty(edtPassword.getText().toString()) && TextUtils.isEmpty(edtConfirmPassword.getText().toString())) {
                    isValidated = false;
                    edtConfirmPassword.setError(getString(R.string.validation_value_required));
                } else if (!TextUtils.isEmpty(edtPassword.getText().toString()) && !TextUtils.isEmpty(edtConfirmPassword.getText().toString())) {

                    if (!edtConfirmPassword.getText().toString().equalsIgnoreCase(edtPassword.getText().toString())) {
                        isValidated = false;
                        tong(getString(R.string.validation_password_not_matches));
                    }
                }

                if (isValidated) {

                    try {
                        registerNewUser();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void registerNewUser() throws Exception {
        showLoadingDialog(ProfileActivity.this);

        JSONObject params = new JSONObject();
        params.put("UserID", SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER_ID, null));
        params.put("Firstname", edtFirstName.getText().toString());
        params.put("Lastname", edtLastName.getText().toString());
        params.put("Username", edtUsername.getText().toString());
        params.put("Email", edtEmail.getText().toString());
        params.put("Password", edtPassword.getText().toString());

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url) + getString(R.string.registration_url));
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ProfileActivity.this);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    enableDisableView(false);
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
    }

    private void enableDisableView(boolean flag) {
        edtFirstName.setFocusableInTouchMode(flag);
        edtLastName.setFocusableInTouchMode(flag);
        edtEmail.setFocusableInTouchMode(flag);
        edtPassword.setFocusableInTouchMode(flag);
        edtConfirmPassword.setFocusableInTouchMode(flag);

        if (flag) {
            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
        } else {
            btnEdit.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
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
