package com.thesejongproject.src;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.thesejongproject.R;
import com.thesejongproject.caching.SmartCaching;
import com.thesejongproject.customviews.AutoSpanGridLayoutManager;
import com.thesejongproject.customviews.AutoSpannable;
import com.thesejongproject.customviews.RelativePopupWindow;
import com.thesejongproject.customviews.SmartTextView;
import com.thesejongproject.customviews.SuggestionsPopup;
import com.thesejongproject.customviews.WordHousePopup;
import com.thesejongproject.customviews.WordHouseSuggestionsPopup;
import com.thesejongproject.smart.Constants;
import com.thesejongproject.smart.SmartApplication;
import com.thesejongproject.weservice.SmartWebManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import helper.ItemTouchHelperAdapter;
import helper.ItemTouchHelperViewHolder;
import helper.OnStartDragListener;
import helper.SimpleItemTouchHelperCallback;
import helper.SpannableEnglishWord;
import helper.SpannableKoreanWord;

/**
 * Created by ebiztrait on 27/1/17.
 */
public class ExerciseActivity extends BaseActivity implements OnStartDragListener {

    private SmartTextView txtMessage;

    private LinearLayout lnrErrorMessage;
    private SmartTextView txtErrorMessage;

    private RecyclerView rvKoreanSentence;
    private AutoSpanGridLayoutManager koreanGridLayoutManager;
    private RecyclerViewKoreanSentenceAdapter recyclerViewKoreanSentenceAdapter;

    private RecyclerView rvEnglishSentence;
    private AutoSpanGridLayoutManager englishGridLayoutManager;
    private RecyclerViewEnglishSentenceAdapter recyclerViewEnglishSentenceAdapter;

    private LinearLayout btnQuit;
    private LinearLayout btnWordHouse;
    private LinearLayout btnSubmit;

    private ItemTouchHelper mItemTouchHelper;

    private ArrayList<SpannableKoreanWord> nodesKorean = new ArrayList<>();
    private ArrayList<SpannableEnglishWord> nodesEnglish = new ArrayList<>();

    private ArrayList<String> arraySuggestions = new ArrayList<>();
    private ArrayList<String> arrayWordHouse = new ArrayList<>();

    private SuggestionsPopup suggestionsPopup;
    private WordHouseSuggestionsPopup wordHouseSuggestionsPopup;
    private WordHousePopup wordHousePopup;
    private PopupWindow subPopupWindowWordHouse;

    private SmartCaching smartCaching;

    private JSONObject additionalData = null;


    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    @Override
    public int getLayoutID() {
        return R.layout.exercise_activity;
    }

    @Override
    public void initComponents() {
        if (getIntent().getBooleanExtra("isLogin", false)) {
            getHourglass().startTimer();
        }

        smartCaching = new SmartCaching(this);

        txtMessage = (SmartTextView) findViewById(R.id.txtMessage);

        lnrErrorMessage = (LinearLayout) findViewById(R.id.lnrErrorMessage);
        txtErrorMessage = (SmartTextView) findViewById(R.id.txtErrorMessage);

        rvKoreanSentence = (RecyclerView) findViewById(R.id.rvKoreanSentence);
        rvKoreanSentence.setHasFixedSize(true);
        koreanGridLayoutManager = new AutoSpanGridLayoutManager(this, 4, 0);
        rvKoreanSentence.setLayoutManager(koreanGridLayoutManager);

        rvEnglishSentence = (RecyclerView) findViewById(R.id.rvEnglishSentence);
        rvEnglishSentence.setHasFixedSize(true);
        englishGridLayoutManager = new AutoSpanGridLayoutManager(this, 4, 0);
        rvEnglishSentence.setLayoutManager(englishGridLayoutManager);

        btnQuit = (LinearLayout) findViewById(R.id.btnQuit);
        btnWordHouse = (LinearLayout) findViewById(R.id.btnWordHouse);
        btnSubmit = (LinearLayout) findViewById(R.id.btnSubmit);


        //adHandler.postDelayed(adRunnable, 10000);

        /*mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_unit_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);*/
    }

    @Override
    public void prepareViews() {
        try {
            getExercise();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if(!getIntent().getBooleanExtra("isLogin",true)){
        if (getHourglass().isPaused()) {
            getHourglass().resumeTimer();
        }

        // }
        //}
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause call", ">>>>>>>>>>>>");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (getIntent().getBooleanExtra("isLogin", false)) {
            stopService(SmartApplication.lintent);
            getHourglass().stopTimer();
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public void setActionListeners() {

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseActivity.this);
                builder.setTitle(getString(R.string.app_name)).setMessage(getString(R.string.are_you_sure_quit))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                try {
                                    // getHourglass().stopTimer();
                                    QuitExercise();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.setCancelable(false);
                alert.show();
            }
        });

        btnWordHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    showWordHousePopup(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (recyclerViewEnglishSentenceAdapter != null) {

                    JSONArray AnswerJson = new JSONArray();
                    StringBuilder sbAnswer = new StringBuilder();
                    for (int i = 0; i < recyclerViewEnglishSentenceAdapter.getItemCount(); i++) {
                        View itemView = rvEnglishSentence.getLayoutManager().findViewByPosition(i);

                        SmartTextView txtWord = (SmartTextView) itemView.findViewById(R.id.txtWord);
                        if (i > 0) {
                            sbAnswer.append("  ");
                            sbAnswer.append(txtWord.getText().toString().trim());
                        } else {
                            sbAnswer.append(txtWord.getText().toString().trim());
                        }

                        SpannableEnglishWord node = (SpannableEnglishWord) txtWord.getTag();
                        ContentValues wordData = node.getValue();
                        try {
                            JSONObject json = new JSONObject();
                            json.put("EWord", txtWord.getText().toString());
                            if (wordData.containsKey("ID")) {
                                json.put("ID", wordData.getAsInteger("ID"));
                            } else {
                                json.put("ID", 0);
                            }
                            if (wordData.containsKey("TName")) {
                                json.put("TName", wordData.getAsString("TName"));
                            } else {
                                json.put("TName", "");
                            }
                            AnswerJson.put(json);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        if (additionalData.getBoolean("IsQuestion")) {
                            sbAnswer.append("  ");
                            sbAnswer.append("?");

                            JSONObject json = new JSONObject();
                            json.put("EWord", "?");
                            json.put("ID", 0);
                            json.put("TName", "");
                            AnswerJson.put(json);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.v("@@@AnswerJson", AnswerJson.toString());

                    try {

                        SubmitAnswer(sbAnswer.toString(), AnswerJson);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private void getExercise() throws Exception {
        showLoadingDialog(ExerciseActivity.this);


        JSONObject params = new JSONObject();
        params.put("UserID", SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER_ID, "0"));
        params.put("IsManualMode", SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getInt(SP_IS_MANUAL_MODE, 0));
        //params.put("IsManualMode", "0");

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ExerciseActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url) + getString(R.string.get_exercise_url));
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_GET_EXERCISE);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                hideLoadingDialog();


                if (responseCode == 200) {

                    try {
                        additionalData = response.getJSONObject("AdditinalData");
                        txtMessage.setText(additionalData.getString("Messsage"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        String[] unNormalizedFields = new String[]{"Alternative"};
                        HashMap<String, ArrayList<ContentValues>> resultWK = smartCaching.parseResponse(
                                response.getJSONArray("Results"), TABLE_EXERCISE, unNormalizedFields);
                        ArrayList<ContentValues> arrayKoreanWords = resultWK.get(TABLE_EXERCISE);

                        for (ContentValues s : arrayKoreanWords) {
                            SpannableKoreanWord autoMeasurableText = new SpannableKoreanWord(ExerciseActivity.this, s);
                            nodesKorean.add(autoMeasurableText);
                        }

                        recyclerViewKoreanSentenceAdapter = new RecyclerViewKoreanSentenceAdapter();
                        rvKoreanSentence.setAdapter(recyclerViewKoreanSentenceAdapter);

                        recyclerViewEnglishSentenceAdapter = new RecyclerViewEnglishSentenceAdapter(ExerciseActivity.this);
                        rvEnglishSentence.setAdapter(recyclerViewEnglishSentenceAdapter);

                        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(recyclerViewEnglishSentenceAdapter);
                        mItemTouchHelper = new ItemTouchHelper(callback);
                        mItemTouchHelper.attachToRecyclerView(rvEnglishSentence);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        lnrErrorMessage.setVisibility(View.VISIBLE);
                        txtErrorMessage.setText(response.getString("ErrorMessage"));
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
    }

    private void SubmitAnswer(String answer, JSONArray answerJson) throws Exception {
        showLoadingDialog(ExerciseActivity.this);

        JSONObject params = new JSONObject();
        params.put("UserID", SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER_ID, "0"));
        params.put("ExerciseID", additionalData.getJSONArray("ExersiceID"));
        params.put("AnswerString", answer);
        params.put("AnswerJSON", answerJson);

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ExerciseActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url) + getString(R.string.get_answer_url));
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_GET_EXERCISE);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                hideLoadingDialog();
                if (responseCode == 200) {

                    try {
                        JSONObject results = response.getJSONObject("Results");
                        if (results.getBoolean("IsCorrect")) {
                            //getHourglass().pauseTimer();
                            JSONObject additionalData = response.getJSONObject("AdditinalData");

                            Intent resultIntent = new Intent(ExerciseActivity.this, ResultActivity.class);
                            resultIntent.putExtra("IN_STAGE_COMPLETED", additionalData.getBoolean("StageIsComplate"));
                            resultIntent.putExtra("IN_STAGE", additionalData.getInt("Stage"));
                            resultIntent.putExtra("IN_KOREAN_WORD", results.getString("KWords"));
                            resultIntent.putExtra("IN_ENG_WORD", results.getString("EWords"));
                            startActivity(resultIntent);
                            supportFinishAfterTransition();
                        } else {

                            lnrErrorMessage.setVisibility(View.VISIBLE);
                            txtErrorMessage.setText(results.getString("Message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseError() {

            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams);
    }

    private void QuitExercise() throws Exception {
        showLoadingDialog(ExerciseActivity.this);

        JSONObject params = new JSONObject();
        params.put("UserID", SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER_ID, "0"));
        params.put("ExerciseID", additionalData.getJSONArray("ExersiceID"));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ExerciseActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url) + getString(R.string.quit_exercise_url));
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_QUIT_EXERCISE);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                hideLoadingDialog();
                if (responseCode == 200) {

                    try {
                        JSONObject results = response.getJSONObject("Results");
                        /*if(getHourglass().isRunning()){
                            getHourglass().pauseTimer();
                        }*/
                        Intent resultIntent = new Intent(ExerciseActivity.this, QuitActivity.class);
                        resultIntent.putExtra("IN_KOREAN_WORD", results.getString("KWords"));
                        resultIntent.putExtra("IN_ENG_WORD", results.getString("EWords"));
                        startActivity(resultIntent);
                        finish();
                        // supportFinishAfterTransition();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseError() {

            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams);
    }

    private void showKoreanSuggestionPopup(View v, int position) {
        suggestionsPopup = new SuggestionsPopup(this);

        RecyclerView rvSuggestions = (RecyclerView) suggestionsPopup.getContentView().findViewById(R.id.rvSuggestions);
        rvSuggestions.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ExerciseActivity.this);
        rvSuggestions.setLayoutManager(linearLayoutManager);

        RecyclerViewKoreanSuggestionsAdapter koreanSuggestionsAdapter = new RecyclerViewKoreanSuggestionsAdapter(position);
        rvSuggestions.setAdapter(koreanSuggestionsAdapter);

        suggestionsPopup.showOnAnchor(v, RelativePopupWindow.VerticalPosition.BELOW,
                RelativePopupWindow.HorizontalPosition.CENTER, false);
    }

    private void showEnglishSuggestionPopup(View v, int position, ContentValues row) {
        suggestionsPopup = new SuggestionsPopup(this);

        RecyclerView rvSuggestions = (RecyclerView) suggestionsPopup.getContentView().findViewById(R.id.rvSuggestions);
        rvSuggestions.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ExerciseActivity.this);
        rvSuggestions.setLayoutManager(linearLayoutManager);

        RecyclerViewEnglishSuggestionsAdapter englishSuggestionsAdapter = new RecyclerViewEnglishSuggestionsAdapter(row);
        rvSuggestions.setAdapter(englishSuggestionsAdapter);

        suggestionsPopup.showOnAnchor(v, RelativePopupWindow.VerticalPosition.BELOW,
                RelativePopupWindow.HorizontalPosition.CENTER, false);
    }

    private void showWordHouseSuggestionPopup(View v, final String EnglishWord) {
        wordHouseSuggestionsPopup = new WordHouseSuggestionsPopup(this);

        SmartTextView btnRemove = (SmartTextView) wordHouseSuggestionsPopup.getContentView().findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wordHouseSuggestionsPopup.dismiss();

                for (int i = 0; i < nodesEnglish.size(); i++) {
                    if (nodesEnglish.get(i).getValue().getAsString("EnglishWord").equalsIgnoreCase(EnglishWord)) {
                        nodesEnglish.remove(i);
                        break;
                    }
                }

                recyclerViewEnglishSentenceAdapter.notifyDataSetChanged();
            }
        });

        wordHouseSuggestionsPopup.showOnAnchor(v, RelativePopupWindow.VerticalPosition.BELOW,
                RelativePopupWindow.HorizontalPosition.CENTER, false);
    }

    public void showWordHousePopup(View v) throws Exception {
        wordHousePopup = new WordHousePopup(this);

        RecyclerView rvWordHouse = (RecyclerView) wordHousePopup.getContentView().findViewById(R.id.rvWordHouse);
        rvWordHouse.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(ExerciseActivity.this, 4);
        rvWordHouse.setLayoutManager(linearLayoutManager);

        arrayWordHouse.clear();
        JSONArray word_house = new JSONArray(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_WORD_HOUSE, null));
        for (int i = 0; i < word_house.length(); i++) {
            arrayWordHouse.add(word_house.getString(i));
        }

        RecyclerViewWordHouseAdapter wordHouseAdapter = new RecyclerViewWordHouseAdapter();
        rvWordHouse.setAdapter(wordHouseAdapter);

        wordHousePopup.showOnAnchor(v, RelativePopupWindow.VerticalPosition.ALIGN_BOTTOM,
                RelativePopupWindow.HorizontalPosition.CENTER, 0, 0, true);
    }

    public void showWordHouseSubPopup(final View v, String[] keywords) {
        final Rect location = locateView(v);

        final View popupView = LayoutInflater.from(ExerciseActivity.this).inflate(R.layout.wordhouse_sub_menu, null);
        subPopupWindowWordHouse = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subPopupWindowWordHouse.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        subPopupWindowWordHouse.setOutsideTouchable(true);

        LinearLayout lnrWordHouseSubOptions = (LinearLayout) popupView.findViewById(R.id.lnrWordHouseSubOptions);
        for (int i = 0; i < keywords.length; i++) {
            final TextView textView = new TextView(ExerciseActivity.this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            textView.setPadding(16, 0, 16, 0);
            textView.setTextSize(16);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            textView.setText(keywords[i]);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    subPopupWindowWordHouse.dismiss();
                    wordHousePopup.dismiss();

                    ContentValues word = new ContentValues();
                    word.put("EnglishWord", textView.getText().toString());
                    word.put("from_word_house", "1");

                    SpannableEnglishWord autoMeasurableText = new SpannableEnglishWord(ExerciseActivity.this, word);
                    nodesEnglish.add(autoMeasurableText);

                    recyclerViewEnglishSentenceAdapter.notifyDataSetChanged();
                }
            });
            lnrWordHouseSubOptions.addView(textView);
        }
        subPopupWindowWordHouse.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, location.left, location.top - 90);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e("on stop call", ">>>>>>>>>>>>");
       /* if (adHandler != null) {
            adHandler.removeCallbacks(adRunnable);
        }
        if (adTimer != null) {
            adTimer.cancel();
        }*/
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar) {

    }

    private class RecyclerViewKoreanSentenceAdapter extends RecyclerView.Adapter<RecyclerViewKoreanSentenceAdapter.ViewHolder> implements AutoSpanGridLayoutManager.AutoSpanAdapter {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentence_list_item,
                    parent, false);
            return new ViewHolder(parentView);
        }

        @Override
        public List<? extends AutoSpannable> getItems() {
            return nodesKorean;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final SpannableKoreanWord node = nodesKorean.get(position);

            holder.txtWord.setText(node.getValue().getAsString("KorianWord"));

            holder.txtWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        JSONArray jArraySuggestions = new JSONArray(node.getValue().getAsString("Alternative"));

                        if (jArraySuggestions.length() > 0) {
                            arraySuggestions = new ArrayList<>();
                            for (int i = 0; i < jArraySuggestions.length(); i++) {
                                if (!TextUtils.isEmpty(jArraySuggestions.getString(i))) {
                                    arraySuggestions.add(jArraySuggestions.getString(i));
                                }
                            }

                            showKoreanSuggestionPopup(view, position);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return nodesKorean.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private SmartTextView txtWord;

            public ViewHolder(View itemView) {
                super(itemView);

                txtWord = (SmartTextView) itemView.findViewById(R.id.txtWord);
            }
        }
    }

    private class RecyclerViewEnglishSentenceAdapter extends RecyclerView.Adapter<RecyclerViewEnglishSentenceAdapter.ViewHolder>
            implements ItemTouchHelperAdapter, AutoSpanGridLayoutManager.AutoSpanAdapter {

        private final OnStartDragListener mDragStartListener;

        public RecyclerViewEnglishSentenceAdapter(OnStartDragListener dragStartListener) {
            mDragStartListener = dragStartListener;
        }

        @Override
        public List<? extends AutoSpannable> getItems() {
            return nodesEnglish;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentence_list_item,
                    parent, false);
            return new ViewHolder(parentView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final SpannableEnglishWord node = nodesEnglish.get(position);

            String s = node.getValue().getAsString("EnglishWord");
            holder.txtWord.setText(s);
            holder.txtWord.setTag(node);
            holder.txtWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (node.getValue().getAsString("from_word_house").equalsIgnoreCase("0")) {
                        try {
                            JSONArray jArraySuggestions = new JSONArray(node.getValue().getAsString("Alternative"));
                            if (jArraySuggestions != null && jArraySuggestions.length() > 0) {
                                arraySuggestions = new ArrayList<>();
                                for (int i = 0; i < jArraySuggestions.length(); i++) {
                                    if (!TextUtils.isEmpty(jArraySuggestions.getString(i))) {
                                        arraySuggestions.add(jArraySuggestions.getString(i));
                                    }
                                }
                                arraySuggestions.add("Delete");

                                showEnglishSuggestionPopup(view, position, node.getValue());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (node.getValue().getAsString("from_word_house").equalsIgnoreCase("1")) {

                        showWordHouseSuggestionPopup(view, node.getValue().getAsString("EnglishWord"));
                    }
                }
            });

            holder.txtWord.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mDragStartListener.onStartDrag(holder);
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return nodesEnglish.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(nodesEnglish, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(nodesEnglish, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(int position) {

        }

        public class ViewHolder extends RecyclerView.ViewHolder implements
                ItemTouchHelperViewHolder {

            private SmartTextView txtWord;

            public ViewHolder(View itemView) {
                super(itemView);

                txtWord = (SmartTextView) itemView.findViewById(R.id.txtWord);
            }

            @Override
            public void onItemSelected() {
                itemView.setBackgroundColor(Color.LTGRAY);
            }

            @Override
            public void onItemClear() {
                itemView.setBackgroundColor(0);
            }
        }
    }

    private class RecyclerViewKoreanSuggestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private int Word_Position;

        public RecyclerViewKoreanSuggestionsAdapter(int position) {
            this.Word_Position = position;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestions_list_item,
                    parent, false);
            return new ViewHolder(parentView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

            ViewHolder holder = (ViewHolder) viewHolder;

            holder.txtSuggestion.setText(arraySuggestions.get(position));
            holder.txtSuggestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    suggestionsPopup.dismiss();

                    ContentValues englishWord = new ContentValues();
                    englishWord.putAll(nodesKorean.get(Word_Position).getValue());
                    englishWord.put("EnglishWord", arraySuggestions.get(position));
                    englishWord.put("from_word_house", "0");

                    SpannableEnglishWord autoMeasurableText = new SpannableEnglishWord(ExerciseActivity.this, englishWord);
                    nodesEnglish.add(autoMeasurableText);

                    recyclerViewEnglishSentenceAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return arraySuggestions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private SmartTextView txtSuggestion;

            public ViewHolder(View itemView) {
                super(itemView);

                txtSuggestion = (SmartTextView) itemView.findViewById(R.id.txtSuggestion);
            }
        }
    }

    private class RecyclerViewEnglishSuggestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private int ITEM_VIEW = 0;
        private int DELETE_VIEW = 1;

        private ContentValues englishWord;

        public RecyclerViewEnglishSuggestionsAdapter(ContentValues row) {
            this.englishWord = row;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_VIEW) {
                View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestions_list_item,
                        parent, false);
                return new ViewHolder(parentView);
            } else {
                View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestions_delete_item,
                        parent, false);
                return new ViewHolderDelete(parentView);
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

            if (viewHolder instanceof ViewHolder) {

                ViewHolder holder = (ViewHolder) viewHolder;

                holder.txtSuggestion.setText(arraySuggestions.get(position));
                holder.txtSuggestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        suggestionsPopup.dismiss();

                        englishWord.put("EnglishWord", arraySuggestions.get(position));
                        englishWord.put("from_word_house", "0");

                        recyclerViewEnglishSentenceAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                ViewHolderDelete holder = (ViewHolderDelete) viewHolder;

                holder.txtSuggestion.setText(arraySuggestions.get(position));
                holder.txtSuggestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        suggestionsPopup.dismiss();

                        for (int i = 0; i < nodesEnglish.size(); i++) {
                            if (nodesEnglish.get(i).getValue().getAsString("EnglishWord")
                                    .equalsIgnoreCase(englishWord.getAsString("EnglishWord"))) {
                                nodesEnglish.remove(i);
                                break;
                            }
                        }

                        recyclerViewEnglishSentenceAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {

            if (arraySuggestions.get(position).equalsIgnoreCase("Delete")) {

                return DELETE_VIEW;
            } else {

                return ITEM_VIEW;
            }
        }

        @Override
        public int getItemCount() {
            return arraySuggestions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private SmartTextView txtSuggestion;

            public ViewHolder(View itemView) {
                super(itemView);

                txtSuggestion = (SmartTextView) itemView.findViewById(R.id.txtSuggestion);
            }
        }

        public class ViewHolderDelete extends RecyclerView.ViewHolder {

            private SmartTextView txtSuggestion;

            public ViewHolderDelete(View itemView) {
                super(itemView);

                txtSuggestion = (SmartTextView) itemView.findViewById(R.id.txtSuggestion);
            }
        }
    }

    private class RecyclerViewWordHouseAdapter extends RecyclerView.Adapter<RecyclerViewWordHouseAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_house_list_item,
                    parent, false);
            return new ViewHolder(parentView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.txtWordHouse.setText(arrayWordHouse.get(position));
            holder.txtWordHouse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] keywords = arrayWordHouse.get(position).split("/");
                    if (keywords.length > 1) {

                        showWordHouseSubPopup(view, keywords);
                    } else {
                        wordHousePopup.dismiss();

                        ContentValues word = new ContentValues();
                        word.put("EnglishWord", arrayWordHouse.get(position));
                        word.put("from_word_house", "1");

                        SpannableEnglishWord autoMeasurableText = new SpannableEnglishWord(ExerciseActivity.this, word);
                        nodesEnglish.add(autoMeasurableText);

                        recyclerViewEnglishSentenceAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayWordHouse.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private SmartTextView txtWordHouse;

            public ViewHolder(View itemView) {
                super(itemView);

                txtWordHouse = (SmartTextView) itemView.findViewById(R.id.txtWordHouse);
            }
        }
    }


}
