package com.thesejongproject.src;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ankushgrover.hourglass.Hourglass;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.thesejongproject.R;
import com.thesejongproject.service.TimerService;
import com.thesejongproject.smart.AlertNeutral;
import com.thesejongproject.smart.Constants;
import com.thesejongproject.smart.SmartApplication;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ebiztrait on 9/1/17.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseActivityHandler, Constants {

    private Toolbar toolbar;
    private LayoutInflater layoutInflater;
    private FrameLayout lytChildViewContainer;

    private Timer adTimer;
    private Dialog loadingDialog;

    public Hourglass getHourglass() {
        return TimerService.getServiceObject().getHourglass();
    }

    //private Hourglass hourglass;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        lytChildViewContainer = (FrameLayout) findViewById(R.id.lytChildViewContainer);
        layoutInflater.inflate(getLayoutID(), lytChildViewContainer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);

            toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.topbar_bg));
        }

        initComponents();

        prepareViews();

        setActionListeners();

        if (toolbar != null) {
            manageAppBar(getSupportActionBar(), toolbar);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // stopService(lintent);
    }

    public void showLoadingDialog(final Context context) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        loadingDialog = new Dialog(context);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCancelable(false);
        Window window = loadingDialog.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        loadingDialog.show();
    }

    public void hideLoadingDialog() {
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        }catch (Exception e){

        }

    }

    public void displayAd(final Context context, final boolean isFinish) {
        if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getBoolean(SP_HIT_TIMER, false)) {
            showLoadingDialog(context);
            final InterstitialAd mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_unit_id));
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
            mInterstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    hideLoadingDialog();
                    mInterstitialAd.show();
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HIT_TIMER, false);
                }

                @Override
                public void onAdClosed() {
                    hideLoadingDialog();
                    TimerService.getServiceObject().getHourglass().startTimer();
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HIT_TIMER, false);
                    Intent exerciseIntent = new Intent(context, ExerciseActivity.class);
                    exerciseIntent.putExtra("ISFROMRESULT",true);
                    startActivity(exerciseIntent);
                    if (isFinish) {
                        finish();
                    }
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    hideLoadingDialog();
                    TimerService.getServiceObject().getHourglass().startTimer();
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HIT_TIMER, false);
                    Intent exerciseIntent = new Intent(context, ExerciseActivity.class);
                    startActivity(exerciseIntent);
                    if (isFinish) {
                        finish();
                    }
                }

                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdOpened() {
                }

            });
        } else {
            Intent exerciseIntent = new Intent(context, ExerciseActivity.class);
            startActivity(exerciseIntent);
            if (isFinish) {
                finish();
            }
        }
    }

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

    public boolean emailValidator(final String mailAddress) {
        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mailAddress);
        return matcher.matches();
    }

    /**
     * This method will show short length Toast message with given string.
     *
     * @param msg = String msg to be shown in Toast message.
     */
    public void ting(String msg) {
        Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method will show long length Toast message with given string.
     *
     * @param msg = String msg to be shown in Toast message.
     */
    public void tong(String msg) {
        Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    public void clearActivityStack(Activity currentActivity, Intent intent) {
        Intent mainIntent = Intent.makeRestartActivityTask(intent.getComponent());
        ActivityCompat.startActivity(currentActivity, mainIntent, null);
    }
}
