package com.thesejongproject.src;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.thesejongproject.R;
import com.thesejongproject.customviews.SmartTextView;
import com.thesejongproject.smart.Constants;
import com.thesejongproject.smart.SmartApplication;
import com.thesejongproject.weservice.SmartWebManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by ebiztrait on 2/5/17.
 */

public class ManualModeActivity extends BaseActivity {

    private RecyclerView rvManualMode;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewManualModeAdapter recyclerViewManualModeAdapter;

    private LinearLayout btnCancel;
    private LinearLayout btnSubmit;

    private JSONArray manualModeData = new JSONArray();

    @Override
    public int getLayoutID() {
        return R.layout.manual_mode_activity;
    }

    @Override
    public void initComponents() {

        rvManualMode = (RecyclerView) findViewById(R.id.rvManualMode);
        rvManualMode.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        rvManualMode.setLayoutManager(linearLayoutManager);

        btnCancel = (LinearLayout) findViewById(R.id.btnCancel);
        btnSubmit = (LinearLayout) findViewById(R.id.btnSubmit);
    }

    @Override
    public void prepareViews() {

        try {
            getManualMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setActionListeners() {

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                supportFinishAfterTransition();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONArray SelectedTemplateVerb = new JSONArray();

                    boolean isAnyTemplateSelected = false;
                    for (int i = 0; i < manualModeData.length(); i++) {
                        JSONObject templates = manualModeData.getJSONObject(i);
                        if (templates.getInt("IsSelected") > 0) {
                            isAnyTemplateSelected = true;
                        }
                    }

                    if (isAnyTemplateSelected) {

                        for (int i = 0; i < manualModeData.length(); i++) {
                            JSONObject SelectedTemplate = new JSONObject();

                            JSONObject Template = manualModeData.getJSONObject(i);
                            if (Template.getInt("IsSelected") > 0) {

                                SelectedTemplate.put("TemplateVerbLevelId", Template.getInt("TemplateVerbLevelID"));

                                JSONArray VocabularyLevel = Template.getJSONArray("VocabularyLevel");
                                if (VocabularyLevel != null && VocabularyLevel.length() > 0) {
                                    JSONArray selectedVocabLevels = new JSONArray();
                                    for (int j = 0; j < VocabularyLevel.length(); j++) {
                                        JSONObject VocabLevels = VocabularyLevel.getJSONObject(j);
                                        if (VocabLevels.getInt("IsSelected") > 0) {
                                            JSONArray VocabularyLevelDetails = VocabLevels.getJSONArray("VocabularyLevelDetails");
                                            for (int k = 0; k < VocabularyLevelDetails.length(); k++) {
                                                JSONObject VocabLevelDetail = VocabularyLevelDetails.getJSONObject(k);
                                                if (VocabLevelDetail.getInt("IsSelected") > 0) {
                                                    selectedVocabLevels.put(VocabLevelDetail.getInt("VocabularyLevelID"));
                                                }
                                            }
                                        }
                                    }
                                    SelectedTemplate.put("VocabularyLevels", selectedVocabLevels);
                                } else {
                                    SelectedTemplate.put("VocabularyLevels", new JSONArray());
                                }

                                JSONArray Tenses = Template.getJSONArray("Tenses/Variations");
                                if (Tenses != null && Tenses.length() > 0) {
                                    JSONArray selectedTenses = new JSONArray();
                                    for (int j = 0; j < Tenses.length(); j++) {
                                        JSONObject selectedTense = new JSONObject();
                                        JSONObject Tense = Tenses.getJSONObject(j);
                                        if (Tense.getInt("IsSelected") > 0) {
                                            selectedTense.put("TenseId", Tense.getInt("TenseID"));
                                            JSONArray selectedSentences = new JSONArray();
                                            JSONArray Sentences = Tense.getJSONArray("Sentence");
                                            for (int k = 0; k < Sentences.length(); k++) {
                                                JSONObject Sentence = Sentences.getJSONObject(k);
                                                if (Sentence.getInt("IsSelected") > 0) {
                                                    selectedSentences.put(Sentence.getInt("SentenceTypeID"));
                                                }
                                            }
                                            selectedTense.put("SentenceTypes", selectedSentences);
                                            selectedTenses.put(selectedTense);
                                        }
                                    }
                                    SelectedTemplate.put("Tenses", selectedTenses);
                                }
                                SelectedTemplateVerb.put(SelectedTemplate);
                            }
                        }

                        saveManualMode(SelectedTemplateVerb);
                    } else {

                        Toast.makeText(ManualModeActivity.this, "Please select at least one Template!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getManualMode() throws Exception {
        showLoadingDialog(ManualModeActivity.this);

        JSONObject params = new JSONObject();
        params.put("UserID", SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER_ID, "0"));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ManualModeActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url) + getString(R.string.manual_mode_url));
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_GET_MANUAL_MODE);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                hideLoadingDialog();
                if (responseCode == 200) {
                    try {
                        manualModeData = response.getJSONArray("Results");

                        recyclerViewManualModeAdapter = new RecyclerViewManualModeAdapter();
                        rvManualMode.setAdapter(recyclerViewManualModeAdapter);
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

    private void saveManualMode(JSONArray SelectedTemplateVerb) throws Exception {
        showLoadingDialog(ManualModeActivity.this);

        JSONObject params = new JSONObject();
        params.put("UserID", SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER_ID, "0"));
        params.put("SelectedTemplateVerb", SelectedTemplateVerb);

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ManualModeActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url) + getString(R.string.save_manual_mode_url));
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_SAVE_MANUAL_MODE);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                hideLoadingDialog();
                if (responseCode == 200) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_IS_MANUAL_MODE, 1);

                    Intent intent = new Intent(ManualModeActivity.this, ExerciseActivity.class);
                    startActivity(intent);

                    supportFinishAfterTransition();
                    //displayAd(ManualModeActivity.this, true);
                }
            }

            @Override
            public void onResponseError() {

            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams);
    }

    private class RecyclerViewManualModeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.manual_mode_list_group_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            try {
                final ViewHolder holder = (ViewHolder) viewHolder;

                final JSONObject row = manualModeData.getJSONObject(position);
                holder.txtMenuItemCaption.setText(row.getString("TemplateVerb").trim());
                holder.txtMenuItemCaption.setId(R.id.collapsed);
                holder.imgMainArrow.setBackgroundResource(R.drawable.ic_arrow_right_black);

                if (row.getInt("IsSelected") > 0) {

                    holder.chkMenuSelect.setChecked(true);

                    if (isEverythingSelected(position)) {
                        holder.imgSelected.setVisibility(View.GONE);
                    } else {
                        holder.imgSelected.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.chkMenuSelect.setChecked(false);
                }

                holder.chkMenuSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            JSONObject Template = manualModeData.getJSONObject(position);
                            Template.put("IsSelected", holder.chkMenuSelect.isChecked() ? 1 : 0);

                            JSONArray Tenses = Template.getJSONArray("Tenses/Variations");
                            for (int i = 0; i < Tenses.length(); i++) {
                                JSONObject tenseType = Tenses.getJSONObject(i);
                                tenseType.put("IsSelected", holder.chkMenuSelect.isChecked() ? 1 : 0);

                                JSONArray Sentence = tenseType.getJSONArray("Sentence");
                                for (int j = 0; j < Sentence.length(); j++) {
                                    JSONObject sentence = Sentence.getJSONObject(j);
                                    sentence.put("IsSelected", holder.chkMenuSelect.isChecked() ? 1 : 0);
                                }
                            }
                            holder.imgSelected.setVisibility(View.GONE);
                            holder.chkTenses.setChecked(holder.chkMenuSelect.isChecked());
                            holder.chkTenses.callOnClick();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                holder.txtMenuItemCaption.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (holder.txtMenuItemCaption.getId() == R.id.collapsed) {
                            holder.txtMenuItemCaption.setId(R.id.expanded);
                            holder.txtMenuItemCaption.setTypeface(null, Typeface.BOLD);
                            holder.imgMainArrow.setBackgroundResource(R.drawable.ic_arrow_down_black);

                            try {
                                JSONArray VocabularyLevel = manualModeData.getJSONObject(position).getJSONArray("VocabularyLevel");
                                if (VocabularyLevel != null && VocabularyLevel.length() > 0) {
                                    holder.lnrVocalView.setVisibility(View.VISIBLE);
                                } else {
                                    holder.lnrVocalView.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            holder.txtVocabLevels.setId(R.id.collapsed);
                            holder.imgVocabArrow.setBackgroundResource(R.drawable.ic_arrow_right_black);
                            holder.chkVocabLevels.setChecked(true);
                            holder.chkVocabLevels.setClickable(false);
                            if (isAnyVerbSelected(position)) {
                                holder.imgVerbSelected.setVisibility(View.VISIBLE);
                            } else {
                                holder.imgVerbSelected.setVisibility(View.GONE);
                            }

                            if (isAllVerbSelected(position)) {
                                holder.imgVerbSelected.setVisibility(View.GONE);
                            }

                            holder.lnrTenseView.setVisibility(View.VISIBLE);
                            holder.txtTenses.setId(R.id.collapsed);
                            holder.imgTenseArrow.setBackgroundResource(R.drawable.ic_arrow_right_black);

                            if (isAnyTensesSelected(position)) {
                                holder.imgTenseSelected.setVisibility(View.VISIBLE);
                                holder.chkTenses.setChecked(true);
                            } else {
                                holder.imgTenseSelected.setVisibility(View.GONE);
                                holder.chkTenses.setChecked(false);
                            }

                            if (isAllTensesSelected(position)) {
                                holder.imgTenseSelected.setVisibility(View.GONE);
                            }

                            holder.txtVocabLevels.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (view.getId() == R.id.collapsed) {
                                        view.setId(R.id.expanded);
                                        ((SmartTextView) view).setTypeface(null, Typeface.BOLD);
                                        holder.imgVocabArrow.setBackgroundResource(R.drawable.ic_arrow_down_black);
                                        try {
                                            JSONArray vocabLevels = manualModeData.getJSONObject(position).getJSONArray("VocabularyLevel");
                                            if (vocabLevels != null && vocabLevels.length() > 0) {
                                                for (int i = 0; i < vocabLevels.length(); i++) {
                                                    final View childView = LayoutInflater.from(ManualModeActivity.this).inflate(R.layout.manual_mode_list_child_item, null, false);
                                                    final AppCompatCheckBox chkSubMenuSelect = (AppCompatCheckBox) childView.findViewById(R.id.chkSubMenuSelect);
                                                    final LinearLayout lnrChildView = (LinearLayout) childView.findViewById(R.id.lnrChildView);
                                                    final LinearLayout lnrMenuItem = (LinearLayout) childView.findViewById(R.id.lnrMenuItem);
                                                    final View imgSelected = childView.findViewById(R.id.imgSelected);
                                                    final SmartTextView txtMenuItemCaption = (SmartTextView) childView.findViewById(R.id.txtMenuItemCaption);
                                                    final SmartTextView txtMenuItemDesc = (SmartTextView) childView.findViewById(R.id.txtMenuItemDesc);
                                                    final ImageView imgSubMenuArrow = (ImageView) childView.findViewById(R.id.imgSubMenuArrow);
                                                    lnrChildView.setId(i);

                                                    final JSONObject value = vocabLevels.getJSONObject(i);
                                                    if (value.getInt("IsSelected") > 0) {
                                                        chkSubMenuSelect.setChecked(true);
                                                    } else {
                                                        chkSubMenuSelect.setChecked(false);
                                                    }

                                                    if (value.getString("VocabType").trim().contains("Objects Level")) {
                                                        chkSubMenuSelect.setClickable(true);
                                                        chkSubMenuSelect.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {

                                                                try {
                                                                    imgSelected.setVisibility(View.GONE);

                                                                    JSONArray VocabularyLevel = manualModeData.getJSONObject(position).getJSONArray("VocabularyLevel");
                                                                    VocabularyLevel.getJSONObject(lnrChildView.getId()).put("IsSelected", chkSubMenuSelect.isChecked() ? 1 : 0);

                                                                    boolean isNoVocabLevelSelected = true;
                                                                    for (int j = 0; j < VocabularyLevel.length(); j++) {
                                                                        JSONObject vocabLevel = VocabularyLevel.getJSONObject(j);
                                                                        if (vocabLevel.getInt("IsSelected") > 0) {
                                                                            isNoVocabLevelSelected = false;
                                                                        }
                                                                    }

                                                                    if (isNoVocabLevelSelected) {

                                                                        chkSubMenuSelect.setChecked(true);
                                                                        VocabularyLevel.getJSONObject(lnrChildView.getId()).put("IsSelected", chkSubMenuSelect.isChecked() ? 1 : 0);
                                                                    } else {

                                                                        JSONArray VocabularyLevelDetails = VocabularyLevel.getJSONObject(lnrChildView.getId()).getJSONArray("VocabularyLevelDetails");
                                                                        for (int j = 0; j < VocabularyLevelDetails.length(); j++) {
                                                                            JSONObject vocab = VocabularyLevelDetails.getJSONObject(j);
                                                                            vocab.put("IsSelected", chkSubMenuSelect.isChecked() ? 1 : 0);
                                                                        }

                                                                        if (lnrChildView != null && lnrChildView.getChildCount() > 0) {
                                                                            for (int i = 0; i < lnrChildView.getChildCount(); i++) {
                                                                                View child_view = lnrChildView.getChildAt(i);
                                                                                AppCompatCheckBox appCompatCheckBox = (AppCompatCheckBox) child_view.findViewById(R.id.chkSubChildMenuSelect);
                                                                                appCompatCheckBox.setChecked(chkSubMenuSelect.isChecked());
                                                                            }
                                                                        }
                                                                    }

                                                                    if (isAnyVerbSelected(position)) {

                                                                        holder.imgVerbSelected.setVisibility(View.VISIBLE);
                                                                    } else {

                                                                        holder.imgVerbSelected.setVisibility(View.GONE);
                                                                    }

                                                                    if (isAllVerbSelected(position)) {

                                                                        holder.imgVerbSelected.setVisibility(View.GONE);
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        chkSubMenuSelect.setClickable(false);
                                                    }

                                                    if (isAnyVerbLevelSelected(position, i)) {
                                                        imgSelected.setVisibility(View.VISIBLE);
                                                    } else {
                                                        imgSelected.setVisibility(View.GONE);
                                                    }

                                                    if (isAllVerbLevelSelected(position, i)) {

                                                        imgSelected.setVisibility(View.GONE);
                                                    }

                                                    txtMenuItemCaption.setText(value.getString("VocabType").trim());
                                                    txtMenuItemCaption.setId(R.id.collapsed);

                                                    if (!TextUtils.isEmpty(value.getString("VocabDescription"))) {
                                                        txtMenuItemDesc.setVisibility(View.VISIBLE);
                                                        txtMenuItemDesc.setText(value.getString("VocabDescription"));
                                                    } else {
                                                        txtMenuItemDesc.setVisibility(View.GONE);
                                                    }

                                                    imgSubMenuArrow.setBackgroundResource(R.drawable.ic_arrow_right_black);

                                                    lnrMenuItem.setTag(value);
                                                    lnrMenuItem.setOnClickListener(new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View view) {
                                                            if (txtMenuItemCaption.getId() == R.id.collapsed) {
                                                                txtMenuItemCaption.setId(R.id.expanded);
                                                                txtMenuItemCaption.setTypeface(null, Typeface.BOLD);
                                                                imgSubMenuArrow.setBackgroundResource(R.drawable.ic_arrow_down_black);

                                                                lnrChildView.removeAllViews();

                                                                try {
                                                                    JSONArray vocabularyLevelDetails = ((JSONObject) view.getTag()).getJSONArray("VocabularyLevelDetails");
                                                                    if (vocabularyLevelDetails != null && vocabularyLevelDetails.length() > 0) {
                                                                        for (int i = 0; i < vocabularyLevelDetails.length(); i++) {
                                                                            final View childView = LayoutInflater.from(ManualModeActivity.this).inflate(R.layout.manual_mode_list_sub_child_item, null, false);
                                                                            final AppCompatCheckBox chkSubChildMenuSelect = (AppCompatCheckBox) childView.findViewById(R.id.chkSubChildMenuSelect);
                                                                            chkSubChildMenuSelect.setTag(i);
                                                                            final SmartTextView txtMenuItemCaption = (SmartTextView) childView.findViewById(R.id.txtMenuItemCaption);
                                                                            final SmartTextView txtMenuItemDesc = (SmartTextView) childView.findViewById(R.id.txtMenuItemDesc);

                                                                            final JSONObject value = vocabularyLevelDetails.getJSONObject(i);
                                                                            txtMenuItemCaption.setText(value.getString("VocabLevelTitle"));
                                                                            txtMenuItemDesc.setText(value.getString("VocabDescription"));

                                                                            if (value.getInt("IsSelected") > 0) {
                                                                                chkSubChildMenuSelect.setChecked(true);
                                                                            } else {
                                                                                chkSubChildMenuSelect.setChecked(false);
                                                                            }

                                                                            chkSubChildMenuSelect.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {

                                                                                    try {
                                                                                        JSONArray VocabularyLevel = manualModeData.getJSONObject(position).getJSONArray("VocabularyLevel");
                                                                                        JSONArray VocabularyLevelDetails = VocabularyLevel.getJSONObject(lnrChildView.getId()).getJSONArray("VocabularyLevelDetails");
                                                                                        JSONObject vocab = VocabularyLevelDetails.getJSONObject((Integer) v.getTag());
                                                                                        vocab.put("IsSelected", chkSubChildMenuSelect.isChecked() ? 1 : 0);

                                                                                        boolean isNoLevelSelected = true;
                                                                                        for (int j = 0; j < VocabularyLevelDetails.length(); j++) {
                                                                                            JSONObject level = VocabularyLevelDetails.getJSONObject(j);
                                                                                            if (level.getInt("IsSelected") > 0) {
                                                                                                isNoLevelSelected = false;
                                                                                            }
                                                                                        }

                                                                                        if (isNoLevelSelected) {

                                                                                            chkSubChildMenuSelect.setChecked(true);
                                                                                            vocab.put("IsSelected", chkSubChildMenuSelect.isChecked() ? 1 : 0);
                                                                                        }

                                                                                        if (isAnyVerbLevelSelected(position, lnrChildView.getId())) {

                                                                                            imgSelected.setVisibility(View.VISIBLE);
                                                                                            chkSubMenuSelect.setChecked(true);
                                                                                            manualModeData.getJSONObject(position).put("IsSelected", chkSubMenuSelect.isChecked() ? 1 : 0);
                                                                                        } else {

                                                                                            imgSelected.setVisibility(View.GONE);
                                                                                            chkSubMenuSelect.setChecked(false);
                                                                                            manualModeData.getJSONObject(position).put("IsSelected", chkSubMenuSelect.isChecked() ? 1 : 0);
                                                                                        }

                                                                                        if (isAllVerbLevelSelected(position, lnrChildView.getId())) {

                                                                                            imgSelected.setVisibility(View.GONE);
                                                                                        }

                                                                                        if (isAnyVerbSelected(position)) {

                                                                                            holder.imgVerbSelected.setVisibility(View.VISIBLE);
                                                                                        }

                                                                                        if (isAllVerbSelected(position)) {

                                                                                            holder.imgVerbSelected.setVisibility(View.GONE);
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                }
                                                                            });
                                                                            lnrChildView.addView(childView);
                                                                        }
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            } else if (txtMenuItemCaption.getId() == R.id.expanded) {

                                                                txtMenuItemCaption.setId(R.id.collapsed);
                                                                txtMenuItemCaption.setTypeface(null, Typeface.NORMAL);
                                                                imgSubMenuArrow.setBackgroundResource(R.drawable.ic_arrow_right_black);

                                                                lnrChildView.removeAllViews();
                                                            }
                                                        }
                                                    });
                                                    holder.lnrVocabChildView.addView(childView);
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {

                                        view.setId(R.id.collapsed);
                                        ((SmartTextView) view).setTypeface(null, Typeface.NORMAL);
                                        holder.imgVocabArrow.setBackgroundResource(R.drawable.ic_arrow_right_black);
                                        holder.lnrVocabChildView.removeAllViews();
                                    }
                                }
                            });

                            holder.chkTenses.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    try {
                                        JSONArray Tenses = manualModeData.getJSONObject(position).getJSONArray("Tenses/Variations");
                                        for (int i = 0; i < Tenses.length(); i++) {
                                            JSONObject tenseType = Tenses.getJSONObject(i);
                                            tenseType.put("IsSelected", holder.chkTenses.isChecked() ? 1 : 0);

                                            JSONArray Sentence = tenseType.getJSONArray("Sentence");
                                            for (int j = 0; j < Sentence.length(); j++) {
                                                JSONObject sentence = Sentence.getJSONObject(j);
                                                sentence.put("IsSelected", holder.chkTenses.isChecked() ? 1 : 0);
                                            }
                                        }

                                        if (holder.lnrTencesChildView != null && holder.lnrTencesChildView.getChildCount() > 0) {
                                            for (int i = 0; i < holder.lnrTencesChildView.getChildCount(); i++) {
                                                View view = holder.lnrTencesChildView.getChildAt(i);
                                                AppCompatCheckBox chkMenuSelect = (AppCompatCheckBox) view.findViewById(R.id.chkSubMenuSelect);
                                                chkMenuSelect.setChecked(holder.chkTenses.isChecked());
                                                chkMenuSelect.callOnClick();
                                            }
                                        }

                                        if (isAnyTensesSelected(position)) {

                                            holder.imgTenseSelected.setVisibility(View.VISIBLE);
                                            holder.imgSelected.setVisibility(View.VISIBLE);
                                            holder.chkMenuSelect.setChecked(true);
                                            manualModeData.getJSONObject(position).put("IsSelected", holder.chkMenuSelect.isChecked() ? 1 : 0);
                                        } else {

                                            holder.imgTenseSelected.setVisibility(View.GONE);
                                            holder.imgSelected.setVisibility(View.GONE);
                                            holder.chkMenuSelect.setChecked(false);
                                            manualModeData.getJSONObject(position).put("IsSelected", holder.chkMenuSelect.isChecked() ? 1 : 0);
                                        }

                                        if (isAllTensesSelected(position)) {

                                            holder.imgTenseSelected.setVisibility(View.GONE);

                                            holder.imgSelected.setVisibility(View.GONE);
                                            holder.chkMenuSelect.setChecked(true);
                                            manualModeData.getJSONObject(position).put("IsSelected", holder.chkMenuSelect.isChecked() ? 1 : 0);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            holder.txtTenses.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (view.getId() == R.id.collapsed) {
                                        view.setId(R.id.expanded);
                                        ((SmartTextView) view).setTypeface(null, Typeface.BOLD);
                                        holder.imgTenseArrow.setBackgroundResource(R.drawable.ic_arrow_down_black);

                                        try {
                                            JSONArray tensesLevels = manualModeData.getJSONObject(position).getJSONArray("Tenses/Variations");
                                            if (tensesLevels != null && tensesLevels.length() > 0) {
                                                for (int i = 0; i < tensesLevels.length(); i++) {
                                                    final View childView = LayoutInflater.from(ManualModeActivity.this).inflate(R.layout.manual_mode_list_child_item, null, false);
                                                    final AppCompatCheckBox chkSubMenuSelect = (AppCompatCheckBox) childView.findViewById(R.id.chkSubMenuSelect);
                                                    final LinearLayout lnrChildView = (LinearLayout) childView.findViewById(R.id.lnrChildView);
                                                    final SmartTextView txtMenuItemCaption = (SmartTextView) childView.findViewById(R.id.txtMenuItemCaption);
                                                    final View imgSelected = childView.findViewById(R.id.imgSelected);
                                                    final ImageView imgSubMenuArrow = (ImageView) childView.findViewById(R.id.imgSubMenuArrow);
                                                    lnrChildView.setId(i);

                                                    final JSONObject value = tensesLevels.getJSONObject(i);
                                                    if (value.getInt("IsSelected") > 0) {
                                                        chkSubMenuSelect.setChecked(true);
                                                    } else {
                                                        chkSubMenuSelect.setChecked(false);
                                                    }

                                                    if (isAnySentenceSelected(position, i)) {
                                                        imgSelected.setVisibility(View.VISIBLE);
                                                    } else {
                                                        imgSelected.setVisibility(View.GONE);
                                                    }

                                                    if (isAllSentenceSelected(position, i)) {

                                                        imgSelected.setVisibility(View.GONE);
                                                    }

                                                    chkSubMenuSelect.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            try {
                                                                imgSelected.setVisibility(View.GONE);

                                                                JSONArray Tenses = manualModeData.getJSONObject(position).getJSONArray("Tenses/Variations");
                                                                Tenses.getJSONObject(lnrChildView.getId()).put("IsSelected", chkSubMenuSelect.isChecked() ? 1 : 0);
                                                                JSONArray Sentence = Tenses.getJSONObject(lnrChildView.getId()).getJSONArray("Sentence");
                                                                for (int j = 0; j < Sentence.length(); j++) {
                                                                    JSONObject vocab = Sentence.getJSONObject(j);
                                                                    vocab.put("IsSelected", chkSubMenuSelect.isChecked() ? 1 : 0);
                                                                }

                                                                if (lnrChildView != null && lnrChildView.getChildCount() > 0) {
                                                                    for (int i = 0; i < lnrChildView.getChildCount(); i++) {
                                                                        View child_view = lnrChildView.getChildAt(i);
                                                                        AppCompatCheckBox appCompatCheckBox = (AppCompatCheckBox) child_view.findViewById(R.id.chkSubChildMenuSelect);
                                                                        appCompatCheckBox.setChecked(chkSubMenuSelect.isChecked());
                                                                        appCompatCheckBox.callOnClick();
                                                                    }
                                                                }

                                                                if (isAnyTensesSelected(position)) {

                                                                    holder.imgTenseSelected.setVisibility(View.VISIBLE);
                                                                    holder.chkTenses.setChecked(true);

                                                                    holder.imgSelected.setVisibility(View.VISIBLE);
                                                                    holder.chkMenuSelect.setChecked(true);
                                                                    manualModeData.getJSONObject(position).put("IsSelected", holder.chkMenuSelect.isChecked() ? 1 : 0);
                                                                } else {

                                                                    holder.imgTenseSelected.setVisibility(View.GONE);
                                                                    holder.chkTenses.setChecked(false);

                                                                    holder.imgSelected.setVisibility(View.GONE);
                                                                    holder.chkMenuSelect.setChecked(false);
                                                                    manualModeData.getJSONObject(position).put("IsSelected", holder.chkMenuSelect.isChecked() ? 1 : 0);
                                                                }

                                                                if (isAllTensesSelected(position)) {

                                                                    holder.imgTenseSelected.setVisibility(View.GONE);
                                                                    holder.chkTenses.setChecked(true);

                                                                    holder.imgSelected.setVisibility(View.GONE);
                                                                    holder.chkMenuSelect.setChecked(true);
                                                                    manualModeData.getJSONObject(position).put("IsSelected", holder.chkMenuSelect.isChecked() ? 1 : 0);
                                                                }

                                                                if (isEverythingSelected(position)) {

                                                                    holder.imgSelected.setVisibility(View.GONE);
                                                                }

                                                                if (isNothingSelected(position)) {

                                                                    holder.imgSelected.setVisibility(View.GONE);
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });

                                                    txtMenuItemCaption.setText(value.getString("TenseName").trim());
                                                    txtMenuItemCaption.setTag(value);
                                                    txtMenuItemCaption.setId(R.id.collapsed);
                                                    imgSubMenuArrow.setBackgroundResource(R.drawable.ic_arrow_right_black);
                                                    txtMenuItemCaption.setOnClickListener(new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View view) {

                                                            if (view.getId() == R.id.collapsed) {
                                                                view.setId(R.id.expanded);
                                                                ((SmartTextView) view).setTypeface(null, Typeface.BOLD);
                                                                imgSubMenuArrow.setBackgroundResource(R.drawable.ic_arrow_down_black);

                                                                lnrChildView.removeAllViews();

                                                                try {
                                                                    JSONArray tensesLevelDetails = ((JSONObject) view.getTag()).getJSONArray("Sentence");
                                                                    if (tensesLevelDetails != null && tensesLevelDetails.length() > 0) {
                                                                        for (int i = 0; i < tensesLevelDetails.length(); i++) {
                                                                            final View childView = LayoutInflater.from(ManualModeActivity.this).inflate(R.layout.manual_mode_list_sub_child_item, null, false);
                                                                            final AppCompatCheckBox chkSubChildMenuSelect = (AppCompatCheckBox) childView.findViewById(R.id.chkSubChildMenuSelect);
                                                                            chkSubChildMenuSelect.setTag(i);
                                                                            final SmartTextView txtMenuItemCaption = (SmartTextView) childView.findViewById(R.id.txtMenuItemCaption);
                                                                            final SmartTextView txtMenuItemDesc = (SmartTextView) childView.findViewById(R.id.txtMenuItemDesc);

                                                                            final JSONObject value = tensesLevelDetails.getJSONObject(i);
                                                                            txtMenuItemCaption.setText(value.getString("SentenceTypeName"));
                                                                            txtMenuItemDesc.setText(value.getString("SentenceTypeDescription"));

                                                                            if (value.getInt("IsSelected") > 0) {

                                                                                chkSubChildMenuSelect.setChecked(true);
                                                                            } else {

                                                                                chkSubChildMenuSelect.setChecked(false);
                                                                            }

                                                                            chkSubChildMenuSelect.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {

                                                                                    try {
                                                                                        JSONArray Tenses = manualModeData.getJSONObject(position).getJSONArray("Tenses/Variations");
                                                                                        JSONArray Sentence = Tenses.getJSONObject(lnrChildView.getId()).getJSONArray("Sentence");
                                                                                        JSONObject sentence = Sentence.getJSONObject((Integer) v.getTag());
                                                                                        sentence.put("IsSelected", chkSubChildMenuSelect.isChecked() ? 1 : 0);

                                                                                        if (isAnySentenceSelected(position, lnrChildView.getId())) {

                                                                                            imgSelected.setVisibility(View.VISIBLE);

                                                                                            chkSubMenuSelect.setChecked(true);
                                                                                            Tenses.getJSONObject(lnrChildView.getId()).put("IsSelected", chkSubMenuSelect.isChecked() ? 1 : 0);
                                                                                        } else {

                                                                                            imgSelected.setVisibility(View.GONE);

                                                                                            chkSubMenuSelect.setChecked(false);
                                                                                            Tenses.getJSONObject(lnrChildView.getId()).put("IsSelected", chkSubMenuSelect.isChecked() ? 1 : 0);
                                                                                        }

                                                                                        if (isAllSentenceSelected(position, lnrChildView.getId())) {

                                                                                            imgSelected.setVisibility(View.GONE);

                                                                                            chkSubMenuSelect.setChecked(true);
                                                                                            Tenses.getJSONObject(lnrChildView.getId()).put("IsSelected", chkSubMenuSelect.isChecked() ? 1 : 0);
                                                                                        }

                                                                                        if (isAnyTensesSelected(position)) {

                                                                                            holder.imgTenseSelected.setVisibility(View.VISIBLE);
                                                                                            holder.chkTenses.setChecked(true);

                                                                                            holder.imgSelected.setVisibility(View.VISIBLE);
                                                                                            holder.chkMenuSelect.setChecked(true);
                                                                                            manualModeData.getJSONObject(position).put("IsSelected", holder.chkMenuSelect.isChecked() ? 1 : 0);
                                                                                        } else {

                                                                                            holder.imgTenseSelected.setVisibility(View.GONE);
                                                                                            holder.chkTenses.setChecked(false);

                                                                                            holder.imgSelected.setVisibility(View.GONE);
                                                                                            holder.chkMenuSelect.setChecked(false);
                                                                                            manualModeData.getJSONObject(position).put("IsSelected", holder.chkMenuSelect.isChecked() ? 1 : 0);
                                                                                        }

                                                                                        if (isAllTensesSelected(position)) {

                                                                                            holder.imgTenseSelected.setVisibility(View.GONE);
                                                                                            holder.chkTenses.setChecked(true);

                                                                                            holder.imgSelected.setVisibility(View.GONE);
                                                                                            holder.chkMenuSelect.setChecked(true);
                                                                                            manualModeData.getJSONObject(position).put("IsSelected", holder.chkMenuSelect.isChecked() ? 1 : 0);
                                                                                        }

                                                                                        if (isEverythingSelected(position)) {

                                                                                            holder.imgSelected.setVisibility(View.GONE);
                                                                                        }

                                                                                        if (isNothingSelected(position)) {

                                                                                            holder.imgSelected.setVisibility(View.GONE);
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                }
                                                                            });
                                                                            lnrChildView.addView(childView);
                                                                        }
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            } else if (view.getId() == R.id.expanded) {

                                                                view.setId(R.id.collapsed);
                                                                ((SmartTextView) view).setTypeface(null, Typeface.NORMAL);
                                                                imgSubMenuArrow.setBackgroundResource(R.drawable.ic_arrow_right_black);
                                                                lnrChildView.removeAllViews();
                                                            }
                                                        }
                                                    });
                                                    holder.lnrTencesChildView.addView(childView);
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {

                                        view.setId(R.id.collapsed);
                                        ((SmartTextView) view).setTypeface(null, Typeface.NORMAL);
                                        holder.imgTenseArrow.setBackgroundResource(R.drawable.ic_arrow_right_black);
                                        holder.lnrTencesChildView.removeAllViews();
                                    }
                                }
                            });
                        } else if (view.getId() == R.id.expanded) {

                            holder.txtMenuItemCaption.setId(R.id.collapsed);
                            holder.txtMenuItemCaption.setTypeface(null, Typeface.NORMAL);
                            holder.imgMainArrow.setBackgroundResource(R.drawable.ic_arrow_right_black);

                            holder.lnrVocalView.setVisibility(View.GONE);
                            holder.txtVocabLevels.setId(R.id.collapsed);
                            holder.txtVocabLevels.setTypeface(null, Typeface.NORMAL);
                            holder.imgVocabArrow.setBackgroundResource(R.drawable.ic_arrow_right_black);
                            holder.lnrVocabChildView.removeAllViews();

                            holder.lnrTenseView.setVisibility(View.GONE);
                            holder.txtTenses.setId(R.id.collapsed);
                            holder.txtTenses.setTypeface(null, Typeface.NORMAL);
                            holder.imgTenseArrow.setBackgroundResource(R.drawable.ic_arrow_right_black);
                            holder.lnrTencesChildView.removeAllViews();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return manualModeData.length();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private AppCompatCheckBox chkMenuSelect;
            private SmartTextView txtMenuItemCaption;
            private ImageView imgMainArrow;
            private View imgSelected;

            private LinearLayout lnrVocalView;
            private AppCompatCheckBox chkVocabLevels;
            private SmartTextView txtVocabLevels;
            private View imgVerbSelected;
            private ImageView imgVocabArrow;
            private LinearLayout lnrVocabChildView;

            private LinearLayout lnrTenseView;
            private AppCompatCheckBox chkTenses;
            private SmartTextView txtTenses;
            private View imgTenseSelected;
            private ImageView imgTenseArrow;
            private LinearLayout lnrTencesChildView;

            public ViewHolder(View itemView) {
                super(itemView);

                chkMenuSelect = (AppCompatCheckBox) itemView.findViewById(R.id.chkMenuSelect);
                txtMenuItemCaption = (SmartTextView) itemView.findViewById(R.id.txtMenuItemCaption);
                imgMainArrow = (ImageView) itemView.findViewById(R.id.imgMainArrow);
                imgSelected = itemView.findViewById(R.id.imgSelected);

                lnrVocalView = (LinearLayout) itemView.findViewById(R.id.lnrVocalView);
                chkVocabLevels = (AppCompatCheckBox) itemView.findViewById(R.id.chkVocabLevels);
                txtVocabLevels = (SmartTextView) itemView.findViewById(R.id.txtVocabLevels);
                imgVocabArrow = (ImageView) itemView.findViewById(R.id.imgVocabArrow);
                imgVerbSelected = itemView.findViewById(R.id.imgVerbSelected);
                lnrVocabChildView = (LinearLayout) itemView.findViewById(R.id.lnrVocabChildView);

                lnrTenseView = (LinearLayout) itemView.findViewById(R.id.lnrTenseView);
                chkTenses = (AppCompatCheckBox) itemView.findViewById(R.id.chkTenses);
                txtTenses = (SmartTextView) itemView.findViewById(R.id.txtTenses);
                imgTenseArrow = (ImageView) itemView.findViewById(R.id.imgTenseArrow);
                imgTenseSelected = itemView.findViewById(R.id.imgTenseSelected);
                lnrTencesChildView = (LinearLayout) itemView.findViewById(R.id.lnrTensesChildView);
            }
        }
    }

    private boolean isAnyTensesSelected(int position) {
        try {
            JSONObject Template = manualModeData.getJSONObject(position);
            JSONArray Tenses = Template.getJSONArray("Tenses/Variations");
            for (int i = 0; i < Tenses.length(); i++) {
                JSONObject tenseType = Tenses.getJSONObject(i);
                JSONArray Sentence = tenseType.getJSONArray("Sentence");
                for (int j = 0; j < Sentence.length(); j++) {
                    JSONObject sentence = Sentence.getJSONObject(j);
                    if (sentence.getInt("IsSelected") > 0) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isAllTensesSelected(int position) {
        try {
            boolean isAllSelected = true;
            JSONObject Template = manualModeData.getJSONObject(position);
            JSONArray Tenses = Template.getJSONArray("Tenses/Variations");
            for (int i = 0; i < Tenses.length(); i++) {
                JSONObject tenseType = Tenses.getJSONObject(i);
                if (tenseType.getInt("IsSelected") <= 0) {
                    isAllSelected = false;
                }
                JSONArray Sentence = tenseType.getJSONArray("Sentence");
                for (int j = 0; j < Sentence.length(); j++) {
                    JSONObject sentence = Sentence.getJSONObject(j);
                    if (sentence.getInt("IsSelected") <= 0) {
                        isAllSelected = false;
                    }
                }
            }
            return isAllSelected;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isAnySentenceSelected(int position, int sentencePosition) {
        try {
            JSONObject Template = manualModeData.getJSONObject(position);
            JSONArray Tenses = Template.getJSONArray("Tenses/Variations");
            JSONObject tenseType = Tenses.getJSONObject(sentencePosition);
            JSONArray Sentence = tenseType.getJSONArray("Sentence");
            for (int j = 0; j < Sentence.length(); j++) {
                JSONObject sentence = Sentence.getJSONObject(j);
                if (sentence.getInt("IsSelected") > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isAllSentenceSelected(int position, int sentencePosition) {
        try {
            boolean isAllSelected = true;
            JSONObject Template = manualModeData.getJSONObject(position);
            JSONArray Tenses = Template.getJSONArray("Tenses/Variations");
            JSONObject tenseType = Tenses.getJSONObject(sentencePosition);
            JSONArray Sentence = tenseType.getJSONArray("Sentence");
            for (int j = 0; j < Sentence.length(); j++) {
                JSONObject sentence = Sentence.getJSONObject(j);
                if (sentence.getInt("IsSelected") <= 0) {
                    isAllSelected = false;
                }
            }
            return isAllSelected;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isEverythingSelected(int position) {
        try {
            boolean isAllSelected = true;
            JSONObject Template = manualModeData.getJSONObject(position);
            JSONArray Tenses = Template.getJSONArray("Tenses/Variations");
            for (int i = 0; i < Tenses.length(); i++) {
                JSONObject tenseType = Tenses.getJSONObject(i);
                JSONArray Sentence = tenseType.getJSONArray("Sentence");
                for (int j = 0; j < Sentence.length(); j++) {
                    JSONObject sentence = Sentence.getJSONObject(j);
                    if (sentence.getInt("IsSelected") <= 0) {
                        isAllSelected = false;
                    }
                }
            }
            return isAllSelected;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isNothingSelected(int position) {
        try {
            boolean isNothingSelected = true;
            JSONObject Template = manualModeData.getJSONObject(position);
            JSONArray Tenses = Template.getJSONArray("Tenses/Variations");
            for (int i = 0; i < Tenses.length(); i++) {
                JSONObject tenseType = Tenses.getJSONObject(i);
                JSONArray Sentence = tenseType.getJSONArray("Sentence");
                for (int j = 0; j < Sentence.length(); j++) {
                    JSONObject sentence = Sentence.getJSONObject(j);
                    if (sentence.getInt("IsSelected") > 0) {
                        isNothingSelected = false;
                    }
                }
            }
            return isNothingSelected;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isAnyVerbSelected(int position) {
        try {
            JSONObject Template = manualModeData.getJSONObject(position);
            JSONArray VocabularyLevel = Template.getJSONArray("VocabularyLevel");
            for (int i = 0; i < VocabularyLevel.length(); i++) {
                JSONObject vocabType = VocabularyLevel.getJSONObject(i);
                JSONArray VocabularyLevelDetails = vocabType.getJSONArray("VocabularyLevelDetails");
                for (int j = 0; j < VocabularyLevelDetails.length(); j++) {
                    JSONObject vocab = VocabularyLevelDetails.getJSONObject(j);
                    if (vocab.getInt("IsSelected") > 0) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isAnyVerbLevelSelected(int position, int levelPosition) {
        try {
            JSONObject Template = manualModeData.getJSONObject(position);
            JSONArray VocabularyLevel = Template.getJSONArray("VocabularyLevel");
            JSONObject vocabType = VocabularyLevel.getJSONObject(levelPosition);
            JSONArray VocabularyLevelDetails = vocabType.getJSONArray("VocabularyLevelDetails");
            for (int j = 0; j < VocabularyLevelDetails.length(); j++) {
                JSONObject vocab = VocabularyLevelDetails.getJSONObject(j);
                if (vocab.getInt("IsSelected") > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isAllVerbLevelSelected(int position, int levelPosition) {
        try {
            boolean isAllSelected = true;
            JSONObject Template = manualModeData.getJSONObject(position);
            JSONArray VocabularyLevel = Template.getJSONArray("VocabularyLevel");
            JSONObject vocabType = VocabularyLevel.getJSONObject(levelPosition);
            JSONArray VocabularyLevelDetails = vocabType.getJSONArray("VocabularyLevelDetails");
            for (int j = 0; j < VocabularyLevelDetails.length(); j++) {
                JSONObject vocab = VocabularyLevelDetails.getJSONObject(j);
                if (vocab.getInt("IsSelected") <= 0) {
                    isAllSelected = false;
                }
            }
            return isAllSelected;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean isAllVerbSelected(int position) {
        try {
            boolean isAllSelected = true;
            JSONObject Template = manualModeData.getJSONObject(position);
            JSONArray VocabularyLevel = Template.getJSONArray("VocabularyLevel");
            for (int i = 0; i < VocabularyLevel.length(); i++) {
                JSONObject vocabType = VocabularyLevel.getJSONObject(i);
                JSONArray VocabularyLevelDetails = vocabType.getJSONArray("VocabularyLevelDetails");
                for (int j = 0; j < VocabularyLevelDetails.length(); j++) {
                    JSONObject vocab = VocabularyLevelDetails.getJSONObject(j);
                    if (vocab.getInt("IsSelected") <= 0) {
                        isAllSelected = false;
                    }
                }
            }
            return isAllSelected;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
