/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.admobadaptersample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.rfm.admobadaptersample.sample.BaseActivity;
import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMFastLane;

import java.util.Map;

public class FastLaneSimpleInterstitial extends BaseActivity implements AppEventListener {

    private final String LOG_TAG = "FastLaneInterstitial";
    private PublisherInterstitialAd interstitialAd;
    private Button displayButton;

    private RFMFastLane rfmFastLane;
    private RFMAdRequest rfmAdRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob_interstitial);
        displayButton = (Button)findViewById(R.id.displayad);
        if(displayButton != null) {
            displayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(interstitialAd != null) {
                        interstitialAd.show();
                    }
                    displayButton.setVisibility(View.INVISIBLE);

                }
            });

            displayButton.setVisibility(View.INVISIBLE);
        }

        updateAdView();
        createInterstitial();
        createAdListener();

        setLoadAdAction();

        rfmFastLane = new RFMFastLane(this);
        rfmAdRequest = new RFMAdRequest();
    }

    @Override
    public void updateAdView() {

    }

    private void createRFMFastLaneRequest() {
        if(rfmAdId != null && !rfmAdId.trim().equalsIgnoreCase("0")) {
            rfmAdRequest.setRFMTestAdId(rfmAdId);
        }
        rfmAdRequest.setRFMParams(rfmServer, rfmPubId, rfmAppId);
        rfmAdRequest.setRFMAdAsInterstitial(true);
    }

    @Override
    public void loadAd() {

        createRFMFastLaneRequest();

        rfmFastLane.preFetchAd(rfmAdRequest, new RFMFastLane.RFMFastLaneAdListener() {
            @Override
            public void onAdReceived(Map<String, String> serverExtras) {

                //serverExtras.put("rfm_pr", "2.0-3.0");
                //serverExtras.put("rfm_pr", "0.5-1.0");

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
        if(displayButton != null) {
            displayButton.setText(R.string.displayad);
            displayButton.setVisibility(View.INVISIBLE);
        }
        PublisherAdRequest req = null;
        if (serverExtras != null && serverExtras.size() > 0) {
            Map.Entry<String, String> entry = serverExtras.entrySet().iterator().next();
            String firstKey = entry.getKey();
            String firstValue = entry.getValue();
            req = new PublisherAdRequest.Builder().addCustomTargeting(firstKey, firstValue).build();
        } else {
            req = new PublisherAdRequest.Builder().build();
        }
        interstitialAd.loadAd(req);
        mNumberOfRequests = mNumberOfRequests +1;
        updateCountersView();
    }

    public void createInterstitial() {
        // Create an ad.
        interstitialAd = new PublisherInterstitialAd(this);
        interstitialAd.setAdUnitId(siteId);
    }

    public void createAdListener() {
        // Set the AdListener.
        interstitialAd.setAdListener(new AdListener() {
            /** Called when an ad is loaded. */
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

            /** Called when an ad failed to load. */
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

            /**
             * Called when an Activity is created in front of the app (e.g. an interstitial is shown, or an
             * ad is clicked and launches a new Activity).
             */
            @Override
            public void onAdOpened() {
                appendTextToConsole("Ad opened");
                Log.d(LOG_TAG, "onAdOpened");
            }

            /** Called when an ad is closed and about to return to the application. */
            @Override
            public void onAdClosed() {
                appendTextToConsole("Ad closed");
                Log.d(LOG_TAG, "onAdClosed");
            }

            /**
             * Called when an ad is clicked and going to start a new Activity that will leave the
             * application (e.g. breaking out to the Browser or Maps application).
             */
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

    @Override
    public void onAppEvent(String name, String info) {
        String message = String.format("Received app event (%s, %s)", name, info);
        Log.v(LOG_TAG, "Message for App event " + message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(interstitialAd != null) {
            interstitialAd = null;
        }
    }

}
