/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.admobadaptersample.sample;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rfm.admobadaptersample.R;

import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    protected String LOG_TAG = "BaseActivity";

    protected String adUnitTitle;
    protected long adUnitId;
    protected String siteId;

    protected int mAdWidth;
    protected int mAdHeight;
    protected String rfmServer;
    protected String rfmAppId;
    protected String rfmPubId;
    protected String rfmAdId;
    protected boolean adTestMode;

    private boolean isCounterViewExpanded = true;
    private boolean isLogsViewExpanded = true;
    protected int locPrecision;

    protected int displayDesity = 1;
    protected int displayHeight = 100;

    private StringBuffer logData = new StringBuffer();

    private TextView mLogText;

    protected int mNumberOfRequests = 0;
    protected int mNumberOfSuccess = 0;
    protected int mNumberOfFailures = 0;

    private TextView countersTextViewContent;

    private String countersStr = "";

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Bundle extras = getIntent().getExtras();
        AdUnit adUnit = AdUnit.fromBundle(extras);

        adUnitTitle = adUnit.getTestCaseName();

        adUnitId = adUnit.getId();

        siteId = adUnit.getSiteId();

        rfmAdId = adUnit.getAdId();
        rfmServer = adUnit.getRfmServer();
        rfmPubId = adUnit.getPubId();
        rfmAppId = adUnit.getAppId();
        adTestMode = adUnit.getTestMode();

        mAdWidth = adUnit.getAdWidth();
        mAdHeight = adUnit.getAdHeight();

        displayDesity = (int)getResources().getDisplayMetrics().density;
        WindowManager mWinMgr = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        displayHeight = mWinMgr.getDefaultDisplay().getHeight();

        if (!adUnit.getLat().isEmpty())
            locPrecision = Integer.parseInt(adUnit.getLocationPrecision());

        registerReceiver(mReceiver, new IntentFilter(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));

    }

    private DatabaseChangedReceiver mReceiver = new DatabaseChangedReceiver() {
        public void onReceive(Context context, Intent intent) {
            AdDataSource mAdDataSource = AdDataSource.getInstance(BaseActivity.this);
            final List<AdUnit> adUnits = mAdDataSource.getAllAdUnits();
            for (AdUnit _adUnit : adUnits) {
                if (_adUnit.getId() == adUnitId) {
                    mAdWidth = _adUnit.getAdWidth();
                    mAdHeight = _adUnit.getAdHeight();
                    rfmAdId = _adUnit.getAdId();
                    adTestMode = _adUnit.getTestMode();

                    updateAdView();
                }
            }
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    protected void setLoadAdAction() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.sample_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(adUnitTitle);

        TextView backdropSubtext = (TextView) findViewById(R.id.backdrop_subtext);
        backdropSubtext.setText(getResources().getString(R.string.site_id) + siteId);

        FloatingActionButton fetchAd = (FloatingActionButton) findViewById(R.id.fetch_ad);
        fetchAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAd();
            }
        });

        mLogText = (TextView) findViewById(R.id.log_text);

        RelativeLayout counters_text_view_title = (RelativeLayout) findViewById(R.id.expand_list_container);
        countersTextViewContent = (TextView) findViewById(R.id.counters_text_view_content);
        counters_text_view_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView expandButton = (ImageView) findViewById(R.id.expand_button);
                if (isCounterViewExpanded) {
                    expandButton.setImageResource(R.drawable.ic_arrow_up_white);
                    ExpandCollapseHelper.collapse(countersTextViewContent);
                } else {
                    expandButton.setImageResource(R.drawable.ic_arrow_down_white);
                    ExpandCollapseHelper.expand(countersTextViewContent);
                }
                isCounterViewExpanded = !isCounterViewExpanded;
            }
        });

        RelativeLayout logs_text_view_title = (RelativeLayout) findViewById(R.id.logs_text_view_content);
        final TextView logText = (TextView) findViewById(R.id.log_text);
        logs_text_view_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView expandButton2 = (ImageView) findViewById(R.id.expand_button_2);
                if (isLogsViewExpanded) {
                    expandButton2.setImageResource(R.drawable.ic_arrow_up_white);
                    ExpandCollapseHelper.collapse(logText);
                } else {
                    expandButton2.setImageResource(R.drawable.ic_arrow_down_white);
                    ExpandCollapseHelper.expand(logText);
                }
                isLogsViewExpanded = !isLogsViewExpanded;
            }
        });

        ImageView clearButton = (ImageView) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumberOfRequests  = 0;
                mNumberOfSuccess  = 0;
                mNumberOfFailures  = 0;
                updateCountersView();
            }
        });

        ImageView clearButton2 = (ImageView) findViewById(R.id.clear_button_2);
        clearButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLogsView();
            }
        });

        updateCountersView();
        clearLogsView();
    }

    protected void updateCountersView() {
        countersStr = "Requests : " + mNumberOfRequests +  "\n" +
                "Success : " + mNumberOfSuccess + "\n" +
                "Failure : " + mNumberOfFailures;
        countersTextViewContent.setText(countersStr);
    }

    private void clearLogsView() {
        logData.setLength(0);
        logData.append(getResources().getString(R.string.default_stats_console_text));
        mLogText.setText(logData);
    }

    public abstract void loadAd();

    public abstract void updateAdView();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.testcase_settings_actionbar_item:
                Intent i = new Intent(getApplicationContext(), TestCaseSettings.class);
                i.putExtra(AdUnit.ID, adUnitId);

                // SimpleInterstitial is always fullscreen
                String parentClass = getLocalClassName();
                if (parentClass.equals("SimpleInterstitial")) {
                    i.putExtra(TestCaseSettings.DISABLE_SIZE_CHANGE, true);

                }

                startActivity(i);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        String parentClass = getLocalClassName();
        if (parentClass.equals("SimpleInterstitial")) {
            MenuItem item = menu.findItem(R.id.testcase_settings_actionbar_item);
            item.setVisible(false);

        }

        return true;
    }

    protected void appendTextToConsole(String data) {
        logData.append(data + "\n");
        mLogText.setText(logData);
    }

    /**
     * Method to change AdView's Width and Height based on preferences
     */
    protected void setAdRequestSize(int adwidth, int adheight, View viewObj){

        if(viewObj == null) {
            return;
        }
        try {
            Log.v(LOG_TAG, "Into setAdRequestSize, Width = " + adwidth + " | Height = " + adheight);
            LinearLayout.LayoutParams bannerLayout = (LinearLayout.LayoutParams) viewObj.getLayoutParams();
            if (adwidth == -1  || adwidth < -1) {
                bannerLayout.width = LinearLayout.LayoutParams.FILL_PARENT;
            } else {
                bannerLayout.width = (int) (adwidth * displayDesity);
            }
            if (adheight == -1 || adheight < -1) {
                bannerLayout.height = LinearLayout.LayoutParams.FILL_PARENT;
            } else {
                bannerLayout.height = (int) (adheight * displayDesity);
            }

            viewObj.setLayoutParams(bannerLayout);


            viewObj.invalidate();
            viewObj.requestLayout();
        } catch (Exception e) {
            Log.v(LOG_TAG, "Problems while configuring layout size based on preferences "+e.getMessage());
            e.printStackTrace();
        }
    }

    public static DisplayMetrics fetchScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics;
    }

}

