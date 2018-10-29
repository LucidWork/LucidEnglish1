package com.thesejongproject.src;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.thesejongproject.R;
import com.thesejongproject.customviews.SmartEditText;
import com.thesejongproject.customviews.SmartTextView;
import com.thesejongproject.smart.AlertNeutral;
import com.thesejongproject.smart.Constants;
import com.thesejongproject.weservice.SmartWebManager;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by ebiztrait on 31/1/17.
 */

public class ForgotPasswordActivity extends BaseActivity {

    private SmartEditText edtEmail;
    private SmartTextView btnSend;

    @Override
    public int getLayoutID() {
        return R.layout.tsp_forgot_password_screen;
    }

    @Override
    public void initComponents() {

        edtEmail = (SmartEditText) findViewById(R.id.edtEmail);
        btnSend = (SmartTextView) findViewById(R.id.btnSend);
    }

    @Override
    public void prepareViews() {

    }

    @Override
    public void setActionListeners() {

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isValidated = true;

                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    isValidated = false;
                    edtEmail.setError(getString(R.string.validation_value_required));
                } else if (!emailValidator(edtEmail.getText().toString())) {
                    isValidated = false;
                    ting(getString(R.string.validation_invalid_email));
                }

                if (isValidated) {

                    forgotPassword();
                }
            }
        });
    }

    private void forgotPassword() {
        try {
            showLoadingDialog(ForgotPasswordActivity.this);

            JSONObject params = new JSONObject();
            params.put("Email", edtEmail.getText().toString());

            HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ForgotPasswordActivity.this);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url) + getString(R.string.forgot_password_url));
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_FORGOT_PASSWORD);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                @Override
                public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                    hideLoadingDialog();
                    if (responseCode == 200) {
                        try {
                            JSONObject result = response.getJSONObject("Results");
                            getOKDialog(ForgotPasswordActivity.this, result.getString("Message"), getString(R.string.ok), true, new AlertNeutral() {
                                @Override
                                public void NeutralMathod(DialogInterface dialog, int id) {

                                    supportFinishAfterTransition();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            getOKDialog(ForgotPasswordActivity.this, response.getString("ErrorMessage"), getString(R.string.ok), true, new AlertNeutral() {
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
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });
    }
}
