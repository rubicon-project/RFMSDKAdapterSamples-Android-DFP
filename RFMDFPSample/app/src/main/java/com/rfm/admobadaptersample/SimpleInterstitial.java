/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.admobadaptersample;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.rfm.admobadaptersample.sample.BaseActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SimpleInterstitial extends BaseActivity {

    private final String LOG_TAG = "SimpleInterstitial";
    private Context mContext;
    private PublisherInterstitialAd interstitialAd;
    private Button displayButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob_interstitial);
        mContext = getApplicationContext();
        displayButton = (Button) findViewById(R.id.displayad);
        if (displayButton != null) {
            displayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (interstitialAd != null) {
                        interstitialAd.show();
                    }
                    displayButton.setVisibility(View.INVISIBLE);

                }
            });

            displayButton.setVisibility(View.INVISIBLE);
        }

        updateAdView();

        setLoadAdAction();

    }

    @Override
    public void updateAdView() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (interstitialAd != null) {
            interstitialAd = null;
        }
    }

    @Override
    public void loadAd() {
        if (displayButton != null) {
            displayButton.setText(R.string.displayad);
            displayButton.setVisibility(View.INVISIBLE);
        }

        createInterstitial();
        createAdListener();

        interstitialAd.loadAd(new PublisherAdRequest.Builder().addTestDevice("2CC6189A7D478F739F11622ECCB6EB5F").
                build());
        mNumberOfRequests = mNumberOfRequests + 1;
        updateCountersView();
    }

    private void createInterstitial() {
        // Create an ad
        interstitialAd = new PublisherInterstitialAd(this);
        interstitialAd.setAdUnitId(siteId);
    }

    private void createAdListener() {
        // Set the AdListener
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(LOG_TAG, "onAdLoaded");
                if (displayButton != null) {
                    displayButton.setEnabled(true);
                    displayButton.setVisibility(View.VISIBLE);
                }
                mNumberOfSuccess = mNumberOfSuccess + 1;
                updateCountersView();
                appendTextToConsole("Ad loaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                String message = String.format("Ad failed %s", getErrorReason(errorCode));
                Log.d(LOG_TAG, message);

                // Change the button text and disable the show button.
                displayButton.setText(R.string.adfailed);
                displayButton.setEnabled(false);
                mNumberOfFailures = mNumberOfFailures + 1;
                updateCountersView();
                appendTextToConsole(message);
            }

            @Override
            public void onAdOpened() {
                appendTextToConsole("Ad opened");
                Log.d(LOG_TAG, "onAdOpened");
            }

            @Override
            public void onAdClosed() {
                appendTextToConsole("Ad closed");
                Log.d(LOG_TAG, "onAdClosed");
            }

            @Override
            public void onAdLeftApplication() {
                appendTextToConsole("Ad left application");
                Log.d(LOG_TAG, "onAdLeftApplication");
            }

            /** Gets a string error reason from an error code. */
            private String getErrorReason(int errorCode) {
                switch (errorCode) {
                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                        return "Internal error";
                    case AdRequest.ERROR_CODE_INVALID_REQUEST:
                        return "Invalid request";
                    case AdRequest.ERROR_CODE_NETWORK_ERROR:
                        return "Network Error";
                    case AdRequest.ERROR_CODE_NO_FILL:
                        return "No fill";
                    default:
                        return "Unknown error";
                }
            }
        });
    }

}
