/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.admobadaptersample.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.rfm.admobadaptersample.R;
import com.rfm.sdk.RFMAdView;

public class AboutActivity extends AppCompatActivity {

    private final String LOG_TAG = "AboutActivity";
    private Toolbar toolbar;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.rfm.admobadaptersample.R.layout.activity_about);
        mContext = getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black);
        }

        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("About");


        RFMAdView mRFMBannerAdView = new RFMAdView(mContext);
        TextView rfmSdkVersionTextView = (TextView) findViewById(R.id.rfm_sdk_version_textview);
        rfmSdkVersionTextView.setText(mRFMBannerAdView.getSDKVersion());

        TextView sampleAppVersionTextView = (TextView) findViewById(R.id.sample_version_textview);
        sampleAppVersionTextView.setText(com.rfm.admobadaptersample.BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}