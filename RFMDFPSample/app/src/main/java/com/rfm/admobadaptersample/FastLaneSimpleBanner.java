/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.admobadaptersample;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.rfm.admobadaptersample.sample.BaseActivity;
import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMFastLane;

import java.util.Map;

public class FastLaneSimpleBanner extends BaseActivity implements AppEventListener {

    private final String LOG_TAG = "FastLaneSimpleBanner";
    private PublisherAdView adView;
    private RFMFastLane rfmFastLane;
    private RFMAdRequest rfmAdRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob_banner);
        adView = new PublisherAdView(this);
        LinearLayout layout = (LinearLayout) findViewById(R.id.adcontainer);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(mAdWidth * displayDesity, mAdHeight * displayDesity);
        llp.gravity = Gravity.CENTER;
        // Add the adView to it.
        if (layout != null)
            layout.addView(adView, llp);
        updateAdView();
        createBannerRequest();
        setLoadAdAction();
        rfmFastLane = new RFMFastLane(this);
        rfmAdRequest = new RFMAdRequest();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
        RFMAdView.clearAds();
    }

    @Override
    public void updateAdView() {
        setAdRequestSize(mAdWidth, mAdHeight, adView);
        adView.setAdSizes(new com.google.android.gms.ads.AdSize(mAdWidth, mAdHeight));
    }

    private void createRFMFastLaneRequest() {
        if (rfmAdId != null && !rfmAdId.trim().equalsIgnoreCase("0")) {
            rfmAdRequest.setRFMTestAdId(rfmAdId);
        }
        rfmAdRequest.setRFMParams(rfmServer, rfmPubId, rfmAppId);
        rfmAdRequest.setAdDimensionParams(mAdWidth, mAdHeight);
    }

    @Override
    public void loadAd() {

        createRFMFastLaneRequest();

        rfmFastLane.preFetchAd(rfmAdRequest, new RFMFastLane.RFMFastLaneAdListener() {
            @Override
            public void onAdReceived(Map<String, String> serverExtras) {
                appendTextToConsole("FASTLANE onAdReceived " +
                        (serverExtras != null ? serverExtras.size() : "serverExtras is null!"));

                requestDfpAd(serverExtras);
            }

            @Override
            public void onAdFailed(String errorMessage) {
                appendTextToConsole("FASTLANE onAdFailed " +
                        (errorMessage != null ? errorMessage : ""));

                requestDfpAd(null);
            }
        });

    }

    private void requestDfpAd(Map<String, String> serverExtras) {
        PublisherAdRequest req = null;

        if (serverExtras != null && serverExtras.size() > 0) {
            Map.Entry<String, String> entry = serverExtras.entrySet().iterator().next();
            String firstKey = entry.getKey();
            String firstValue = entry.getValue();
            req = new PublisherAdRequest.Builder().addCustomTargeting(firstKey, firstValue).build();
        } else {
            req = new PublisherAdRequest.Builder().build();
        }

        adView.loadAd(req);
        mNumberOfRequests = mNumberOfRequests + 1;
        updateCountersView();

    }

    @Override
    public void onAppEvent(String name, String info) {
        String message = String.format("Received app event (%s, %s)", name, info);
        Log.v(LOG_TAG, "Message for App event " + message);
    }

    /**
     * Gets a string error reason from an error code.
     */
    private String getErrorReason(int errorCode) {
        String errorReason = "";
        switch (errorCode) {
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

    private void createBannerRequest() {
        adView.setAdUnitId(siteId);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                appendTextToConsole("Ad loaded");
                mNumberOfSuccess = mNumberOfSuccess + 1;
                updateCountersView();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                String message = String.format("Ad failed %s", getErrorReason(errorCode));
                appendTextToConsole(message);
                mNumberOfFailures = mNumberOfFailures + 1;
                updateCountersView();
            }

            @Override
            public void onAdOpened() {
                appendTextToConsole("Ad opened");
            }

            @Override
            public void onAdClosed() {
                appendTextToConsole("Ad closed");
            }

            @Override
            public void onAdLeftApplication() {
                appendTextToConsole("Ad left Application ");
            }
        });
    }
}
