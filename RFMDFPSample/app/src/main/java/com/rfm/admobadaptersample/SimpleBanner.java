/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.admobadaptersample;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.rfm.admobadaptersample.sample.BaseActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;

public class SimpleBanner extends BaseActivity implements AppEventListener {
	
	private final String LOG_TAG = "SimpleBanner";
	private PublisherAdView adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob_banner);

        adView = new PublisherAdView(this);
        LinearLayout layout = (LinearLayout) findViewById(R.id.adcontainer);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(mAdWidth*displayDesity, mAdHeight*displayDesity);
        llp.gravity= Gravity.CENTER;

        // Add the adView to it.
        layout.addView(adView, llp);
        updateAdView();
        createBannerRequest();
        setLoadAdAction();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
    }

    @Override
    public void updateAdView() {
        try {
            setAdRequestSize(mAdWidth, mAdHeight, adView);
            DisplayMetrics dm = fetchScreenSize(this);
            if(mAdWidth < -1) {
                mAdWidth = (int) (dm.widthPixels/dm.density);
            }
            if(mAdHeight < -1) {
                mAdHeight = (int) (dm.heightPixels/dm.density);
            }
            if (adView != null) {
                adView.setAdSizes(new com.google.android.gms.ads.AdSize(mAdWidth, mAdHeight));
            }
        } catch (Exception e){
            Log.i(LOG_TAG, "Failed to set ad size "+e.getMessage());
        }
    }

    @Override
    public void loadAd() {
        try {
            adView.loadAd(new PublisherAdRequest.Builder().build());
            mNumberOfRequests = mNumberOfRequests + 1;
            updateCountersView();
        } catch (Exception e) {
            Log.i(LOG_TAG, "Failed to load ad "+e.getMessage());
        }
    }

    @Override
    public void onAppEvent(String name, String info) {
        String message = String.format("Received app event (%s, %s)", name, info);
        appendTextToConsole(message);
        Log.v(LOG_TAG, "Message for App event " + message);
    }

    /** Gets a string error reason from an error code. */
    private String getErrorReason(int errorCode) {
        String errorReason = "";
        switch(errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }

    public void createBannerRequest() {
        if(adView == null) {
            Log.e(LOG_TAG, "Failed to request banner for an invalid Ad View ");
        }
        adView.setAdUnitId(siteId);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                appendTextToConsole("Ad loaded");
                mNumberOfSuccess=mNumberOfSuccess+1;
                updateCountersView();
            }

            /** Called when an ad failed to load. */
            @Override
            public void onAdFailedToLoad(int errorCode) {
                String message = String.format("Ad failed %s", getErrorReason(errorCode));
                appendTextToConsole(message);
                mNumberOfFailures=mNumberOfFailures+1;
                updateCountersView();
            }

            /**
             * Called when an Activity is created in front of the app (e.g. an interstitial is shown, or an
             * ad is clicked and launches a new Activity).
             */
            @Override
            public void onAdOpened() {
                appendTextToConsole("Ad opened");
            }

            /** Called when an ad is closed and about to return to the application. */
            @Override
            public void onAdClosed() {
                appendTextToConsole( "Ad closed");
            }

            /**
             * Called when an ad is clicked and going to start a new Activity that will leave the
             * application (e.g. breaking out to the Browser or Maps application).
             */
            @Override
            public void onAdLeftApplication() {
                appendTextToConsole("Ad left Application ");
            }
        });
    }
}
