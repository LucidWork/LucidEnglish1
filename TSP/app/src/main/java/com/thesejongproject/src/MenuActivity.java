package com.thesejongproject.src;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.thesejongproject.R;
import com.thesejongproject.customviews.SmartTextView;
import com.thesejongproject.smart.SmartApplication;

/**
 * Created by ebiztrait on 7/3/17.
 */

public class MenuActivity extends BaseActivity {

    private LinearLayout btnProfile;
    private LinearLayout btnHistory;
    private LinearLayout btnManualMode;
    private LinearLayout btnAutoMode;
    private LinearLayout btnQuickTutorial;
    private SmartTextView btnLogout;

    @Override
    public int getLayoutID() {
        return R.layout.tsp_menu_layout;
    }

    @Override
    public void initComponents() {

        btnProfile = (LinearLayout) findViewById(R.id.btnProfile);
        btnHistory = (LinearLayout) findViewById(R.id.btnHistory);
        btnManualMode = (LinearLayout) findViewById(R.id.btnManualMode);
        btnAutoMode = (LinearLayout) findViewById(R.id.btnAutoMode);
        btnQuickTutorial = (LinearLayout) findViewById(R.id.btnQuickTutorial);
        btnLogout = (SmartTextView) findViewById(R.id.btnLogout);
    }

    @Override
    public void prepareViews() {

        if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getInt(SP_IS_MANUAL_MODE, 0) > 0) {

            btnAutoMode.setVisibility(View.GONE);
        }
    }

    @Override
    public void setActionListeners() {

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MenuActivity.this, ExercisesHistoryActivity.class);
                startActivity(intent);
            }
        });

        btnManualMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ManualModeActivity.class);
                startActivity(intent);
            }
        });

        btnAutoMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_IS_MANUAL_MODE, 0);

                Intent intent = new Intent(MenuActivity.this, ExerciseActivity.class);
                startActivity(intent);

                //displayAd(MenuActivity.this, false);
            }
        });

        btnQuickTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, TutorialActivity.class);
                intent.putExtra("is_signup", false);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                builder.setTitle(getString(R.string.logout)).setMessage(getString(R.string.logout_message))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_USER_ID, null);

                                Intent loginIntent = new Intent(MenuActivity.this, LoginActivity.class);
                                clearActivityStack(MenuActivity.this, loginIntent);
                                stopService(SmartApplication.lintent);
                                getHourglass().stopTimer();
                            }
                        });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.setCancelable(false);
                alert.show();
            }
        });
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                supportFinishAfterTransition();
            }
        });
    }
}
