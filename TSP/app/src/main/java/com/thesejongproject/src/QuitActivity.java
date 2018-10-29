package com.thesejongproject.src;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.thesejongproject.R;
import com.thesejongproject.customviews.SmartTextView;

/**
 * Created by ebiztrait on 31/1/17.
 */

public class QuitActivity extends BaseActivity {

    private SmartTextView txtKoreanWord;
    private SmartTextView txtEnglishWord;

    private LinearLayout btnMenu;
    private LinearLayout btnNext;

    @Override
    public int getLayoutID() {
        return R.layout.quit_result_activity;
    }

    @Override
    public void initComponents() {

        txtEnglishWord = (SmartTextView) findViewById(R.id.txtEnglishWord);
        txtKoreanWord = (SmartTextView) findViewById(R.id.txtKoreanWord);

        btnMenu = (LinearLayout) findViewById(R.id.btnMenu);
        btnNext = (LinearLayout) findViewById(R.id.btnNext);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getHourglass().isPaused()) {
            getHourglass().resumeTimer();
        }
    }

    @Override
    public void prepareViews() {

        txtKoreanWord.setText(getIntent().getStringExtra("IN_KOREAN_WORD"));
        txtEnglishWord.setText(getIntent().getStringExtra("IN_ENG_WORD"));
    }

    @Override
    public void setActionListeners() {

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getHourglass().isRunning()) {
                    getHourglass().pauseTimer();
                }
                Intent exerciseIntent = new Intent(QuitActivity.this, MenuActivity.class);
                startActivity(exerciseIntent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAd(QuitActivity.this, true);
                /*Intent exerciseIntent = new Intent(QuitActivity.this, ExerciseActivity.class);
                startActivity(exerciseIntent);
                supportFinishAfterTransition();*/
            }
        });
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar) {

    }
}
