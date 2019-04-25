package com.thesejongproject.src;

import android.content.ContentValues;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.thesejongproject.R;
import com.thesejongproject.caching.SmartCaching;
import com.thesejongproject.customviews.SmartTextView;
import com.thesejongproject.smart.Constants;
import com.thesejongproject.smart.PagingProvider;
import com.thesejongproject.smart.SmartApplication;
import com.thesejongproject.weservice.SmartWebManager;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by ebiztrait on 7/3/17.
 */

public class ExercisesHistoryActivity extends BaseActivity {

    private RecyclerView rvExercisesHistory;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewHistoryAdapter recyclerViewHistoryAdapter;

    private ArrayList<ContentValues> exerciseHistory = new ArrayList<>();

    private PagingProvider pagingProvider;
    private boolean isCalling = true;

    private Handler pagingHandler = new Handler();

    private SmartCaching smartCaching;

    @Override
    public int getLayoutID() {
        return R.layout.exercises_history_layout;
    }

    @Override
    public void initComponents() {
        smartCaching = new SmartCaching(this);
        pagingProvider = new PagingProvider();

        rvExercisesHistory = (RecyclerView) findViewById(R.id.rvExercisesHistory);
        rvExercisesHistory.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        rvExercisesHistory.setLayoutManager(linearLayoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvExercisesHistory.getContext(),
                linearLayoutManager.getOrientation());
        mDividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.tsp_list_devider));
        rvExercisesHistory.addItemDecoration(mDividerItemDecoration);
    }

    @Override
    public void prepareViews() {

        try {
            getExerciseHistory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setActionListeners() {

        rvExercisesHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();

                if ((firstVisibleItem + visibleItemCount) >= totalItemCount && totalItemCount > 0) {
                    if (!isCalling && pagingProvider.hasNextPage()) {
                        //update call status
                        isCalling = true;

                        pagingHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                //adding temporary progress item
                                exerciseHistory.add(null);
                                recyclerViewHistoryAdapter.notifyItemInserted(exerciseHistory.size());
                            }
                        });

                        try {
                            JSONObject params = new JSONObject();
                            params.put("UserID", SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER_ID, "0"));
                            params.put("PageNumber", pagingProvider.getPageNo());

                            HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
                            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ExercisesHistoryActivity.this);
                            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url) + getString(R.string.get_exercise_history_url));
                            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_GET_EXERCISE_HISTORY);
                            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
                            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                                @Override
                                public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {

                                    exerciseHistory.remove(exerciseHistory.size() - 1);
                                    recyclerViewHistoryAdapter.notifyItemRemoved(exerciseHistory.size());

                                    isCalling = false;

                                    if (responseCode == 200) {
                                        try {
                                            JSONObject additional_data = response.getJSONObject("AdditinalData");
                                            pagingProvider.setPagingParams(additional_data.getInt("PageLimit"), additional_data.getInt("TotalRows"));

                                            HashMap<String, ArrayList<ContentValues>> resultWK = smartCaching.parseResponse(
                                                    response.getJSONArray("Results"), TABLE_EXERCISE_HISTORY);
                                            ArrayList<ContentValues> data = resultWK.get(TABLE_EXERCISE_HISTORY);

                                            exerciseHistory.addAll(data);
                                            recyclerViewHistoryAdapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onResponseError() {

                                    exerciseHistory.remove(exerciseHistory.size() - 1);
                                    recyclerViewHistoryAdapter.notifyItemRemoved(exerciseHistory.size());
                                }
                            });
                            SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams);
                        } catch (Exception e) {
                            e.printStackTrace();

                            exerciseHistory.remove(exerciseHistory.size() - 1);
                            recyclerViewHistoryAdapter.notifyItemRemoved(exerciseHistory.size());
                        }
                    }
                }
            }
        });
    }

    private void getExerciseHistory() throws Exception {
        showLoadingDialog(ExercisesHistoryActivity.this);

        //update call status
        isCalling = true;

        JSONObject params = new JSONObject();
        params.put("UserID", SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER_ID, "0"));
        params.put("PageNumber", pagingProvider.getPageNo());

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ExercisesHistoryActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.base_url) + getString(R.string.get_exercise_history_url));
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_GET_EXERCISE_HISTORY);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                hideLoadingDialog();

                //update call status
                isCalling = false;

                if (responseCode == 200) {
                    try {
                        JSONObject additional_data = response.getJSONObject("AdditinalData");
                        pagingProvider.setPagingParams(additional_data.getInt("PageLimit"), additional_data.getInt("TotalRows"));

                        HashMap<String, ArrayList<ContentValues>> resultWK = smartCaching.parseResponse(
                                response.getJSONArray("Results"), TABLE_EXERCISE_HISTORY);
                        exerciseHistory = resultWK.get(TABLE_EXERCISE_HISTORY);

                        recyclerViewHistoryAdapter = new RecyclerViewHistoryAdapter();
                        rvExercisesHistory.setAdapter(recyclerViewHistoryAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseError() {

                //update call status
                isCalling = false;
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams);
    }

    private class RecyclerViewHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_PROGRESS = 0;
        private final int VIEW_ITEM = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = null;
            if (viewType == VIEW_ITEM) {
                View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item,
                        parent, false);
                viewHolder = new ViewHolder(parentView);
            } else {
                View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_footer_view,
                        parent, false);
                viewHolder = new ProgressViewHolder(parentView);
            }
            return viewHolder;
        }

        @Override
        public int getItemViewType(int position) {
            if (exerciseHistory.get(position) != null) {
                return VIEW_ITEM;
            } else {
                return VIEW_PROGRESS;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof ViewHolder) {

                ViewHolder holder = (ViewHolder) viewHolder;

                ContentValues row = exerciseHistory.get(position);

                holder.txtKoreanSentence.setText(row.getAsString("KWord"));
                holder.txtEnglishSentence.setText(row.getAsString("EWord"));
                holder.txtAttempts.setText(row.getAsString("AttemptCount") + " attempts");
                holder.txtDate.setText(formatDate(row.getAsString("DateTime")));
            } else {

                ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            try {
                return exerciseHistory.size();
            } catch (Exception e) {
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private SmartTextView txtKoreanSentence;
            private SmartTextView txtEnglishSentence;
            private SmartTextView txtAttempts;
            private SmartTextView txtDate;

            private ViewHolder(View itemView) {
                super(itemView);

                txtKoreanSentence = (SmartTextView) itemView.findViewById(R.id.txtKoreanSentence);
                txtEnglishSentence = (SmartTextView) itemView.findViewById(R.id.txtEnglishSentence);
                txtAttempts = (SmartTextView) itemView.findViewById(R.id.txtAttempts);
                txtDate = (SmartTextView) itemView.findViewById(R.id.txtDate);
            }
        }

        private class ProgressViewHolder extends RecyclerView.ViewHolder {

            private ProgressBar progressBar;

            private ProgressViewHolder(View itemView) {
                super(itemView);
                progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            }
        }
    }

    private String formatDate(String dateString) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM dd,yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat2.format(convertedDate);
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                supportFinishAfterTransition();
            }
        });
    }
}
