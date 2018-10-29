package com.thesejongproject.src;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

/**
 * Created by ebiztrait on 9/1/17.
 */

public interface BaseActivityHandler {

    public int getLayoutID();

    public void initComponents();

    public void prepareViews();

    public void setActionListeners();

    public void manageAppBar(ActionBar actionBar, Toolbar toolbar);
}
