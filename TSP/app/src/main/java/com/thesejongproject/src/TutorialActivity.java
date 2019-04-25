package com.thesejongproject.src;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.thesejongproject.R;
import com.thesejongproject.customviews.CirclePageIndicator;
import com.thesejongproject.customviews.SmartTextView;
import com.thesejongproject.smart.Constants;
import com.thesejongproject.smart.SmartApplication;

public class TutorialActivity extends AppCompatActivity implements Constants {

    private ViewPager images_slider;
    private CirclePageIndicator indicator;
    private ImagesSliderAdapter imagesSliderAdapter;
    private SmartTextView btnSkip;

    private int images_array[] = new int[]{R.drawable.tutorial_1, R.drawable.tutorial_2, R.drawable.tutorial_3,
            R.drawable.tutorial_4, R.drawable.tutorial_5, R.drawable.tutorial_6, R.drawable.tutorial_7, R.drawable.tutorial_8,
            R.drawable.tutorial_9, R.drawable.tutorial_10, R.drawable.tutorial_11};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial);

        images_slider = (ViewPager) findViewById(R.id.images_slider);
        indicator = (CirclePageIndicator) findViewById(R.id.pageIndicator);
        btnSkip = (SmartTextView) findViewById(R.id.btnSkip);
        imagesSliderAdapter = new ImagesSliderAdapter(this);
        images_slider.setAdapter(imagesSliderAdapter);
        indicator.setPageColor(ContextCompat.getColor(this, R.color.transparent));
        indicator.setStrokeColor(ContextCompat.getColor(this, R.color.white));
        indicator.setStrokeWidth(convertSizeToDeviceDependent(this, 1));
        indicator.setRadius(convertSizeToDeviceDependent(this, 4));
        indicator.setFillColor(ContextCompat.getColor(this, R.color.white));
        indicator.setViewPager(images_slider);
        indicator.setSnap(true);

       /* if (getIntent().getBooleanExtra("is_signup", true)) {
            btnSkip.setVisibility(View.VISIBLE);
        } else {
            btnSkip.setVisibility(View.GONE);
        }
*/
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getBooleanExtra("is_signup", true)) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HIT_TIMER, false);
                    Intent intent = new Intent(TutorialActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    supportFinishAfterTransition();
                }
            }
        });
    }

    private int convertSizeToDeviceDependent(Context context, int value) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return ((dm.densityDpi * value) / 160);
    }

    private class ImagesSliderAdapter extends PagerAdapter {

        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public ImagesSliderAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images_array.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.tutorial_image_item, container, false);

            ImageView imgTutorial = (ImageView) itemView.findViewById(R.id.imgTutorial);

            imgTutorial.setBackgroundResource(images_array[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
