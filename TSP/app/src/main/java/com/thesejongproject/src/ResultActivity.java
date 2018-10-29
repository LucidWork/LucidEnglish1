package com.thesejongproject.src;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
/*import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;*/
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.thesejongproject.R;
import com.thesejongproject.customviews.SmartTextView;
import com.thesejongproject.smart.SmartApplication;

/**
 * Created by ebiztrait on 31/1/17.
 */

public class ResultActivity extends BaseActivity {

    private ImageView gifImageView;
    private LinearLayout lnrCongoLayout;
    private SmartTextView txtCongoMessage;

    private SmartTextView txtKoreanWord;
    private SmartTextView txtEnglishWord;

    private LinearLayout btnMenu;
    private LinearLayout btnNext;

    @Override
    public int getLayoutID() {
        return R.layout.result_success_activity;
    }

    @Override
    public void initComponents() {


        gifImageView = (ImageView) findViewById(R.id.gifImageView);
        lnrCongoLayout = (LinearLayout) findViewById(R.id.lnrCongoLayout);
        txtCongoMessage = (SmartTextView) findViewById(R.id.txtCongoMessage);

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
    public void onBackPressed() {
        super.onBackPressed();
        stopService(SmartApplication.lintent);
        getHourglass().stopTimer();
        supportFinishAfterTransition();
    }

    @Override
    public void prepareViews() {

        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);
        Glide.with(this).load(R.drawable.animated_success_image).into(imageViewTarget);

        if (getIntent().getBooleanExtra("IN_STAGE_COMPLETED", true)) {
            lnrCongoLayout.setVisibility(View.VISIBLE);
            txtCongoMessage.setText("You have successfully completed Stage " + getIntent().getIntExtra("IN_STAGE", 1)
                    + ". \n\n Now you can move ahead to next Stage " + (getIntent().getIntExtra("IN_STAGE", 1) + 1));
        }

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
                Intent exerciseIntent = new Intent(ResultActivity.this, MenuActivity.class);
                startActivity(exerciseIntent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Intent exerciseIntent = new Intent(ResultActivity.this, ExerciseActivity.class);
                startActivity(exerciseIntent);

                supportFinishAfterTransition();*/
                displayAd(ResultActivity.this, true);
            }
        });
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar) {

    }
}
