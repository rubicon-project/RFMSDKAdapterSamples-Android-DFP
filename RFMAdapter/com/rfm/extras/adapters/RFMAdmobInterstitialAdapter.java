
/*
  * Copyright (C) 2016 Rubicon Project. All rights reserved
 *
 * @author: Rubicon Project.
 *  file for integrating RFM SDK with Admob SDK
 *  RFM SDK will be triggered via Admob Custom Interstitial Event
 *
 */
package com.rfm.extras.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;
import com.rfm.sdk.AdState;
import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMInterstitialAdViewListener;
import com.rfm.util.RFMLog;

import java.util.HashMap;

public class RFMAdmobInterstitialAdapter implements CustomEventInterstitial {

    private static final String LOG_TAG = "RFMAdmobAdapter";
    public static CustomEventInterstitialListener customEventInterstitialListener;
    public static RFMAdView rfmAdView;
    private RFMAdRequest mAdRequest;
    private Context context;

    ////RFM Placement Settings
    private static final String RFM_SERVER_NAME = "http://mrp.rubiconproject.com/";
    private static final String RFM_PUB_ID = "111008";

    private HashMap<String, String> localTargetingInfoHM = new HashMap<String, String>();

    RFMAdmobInterstitialAdapter() {
        localTargetingInfoHM.put("adp_version", "dfp_adp_3.1.0");
    }

    @Override
    public void requestInterstitialAd(Context _context, CustomEventInterstitialListener _customEventInterstitialListener, String serverParameter, MediationAdRequest mediationAdRequest, Bundle bundle) {

        context = _context;
        Log.d(LOG_TAG,"custom interstitial event trigger, appId: "+serverParameter);
        customEventInterstitialListener = _customEventInterstitialListener;

        //Layout banner ad view
        if (rfmAdView == null) {
            rfmAdView = new RFMAdView(context);
            rfmAdView.setVisibility(View.GONE);
        }

        // required to play video ads
        rfmAdView.enableHWAcceleration(true);

        //Set Ad Request parameters
        if(mAdRequest == null) {
            mAdRequest = new RFMAdRequest();
        }

        mAdRequest.setRFMParams(RFM_SERVER_NAME, RFM_PUB_ID, serverParameter);
        mAdRequest.setRFMAdAsInterstitial(true);

        HashMap<String, String> targetingParamsHM = getTargetingParams();
        mAdRequest.setTargetingParams(targetingParamsHM);

        setInterstitialAdViewListener();

        //Request Ad
        if (!rfmAdView.requestRFMAd(mAdRequest)) {
            customEventInterstitialListener.onAdFailedToLoad(11);
        }
    }

    public void setInterstitialAdViewListener() {

        if (rfmAdView == null) {
            return;
        }

        // Optional listener for RFMAd status
        rfmAdView.setRFMAdViewListener(new RFMInterstitialAdViewListener() {

            public void onAdRequested(RFMAdView adView, String requestUrl, boolean adRequestSuccess) {
                Log.v("LOG_TAG", "RFM Ad: Requesting Url:" + requestUrl);
            }

            /*
                * Sent when an ad request loaded an ad; this is a good opportunity to make the ad view
                * visible if it has been set to invisible till this time.
                *
                * @param adView - RFMAdView instance that generated this method
                *
                */
            public void onAdReceived(RFMAdView adView) {
                Log.v("LOG_TAG", "RFM Ad: Received");
                if (customEventInterstitialListener != null) {
                    customEventInterstitialListener.onAdLoaded();
                }
            }

            /*
             * Sent when an ad request failed to load an ad. Client can choose to set the view to invisible
             * if it was set to visible.
             *
             * @param adView - RFMAdView instance that generated this method
             *
             */
            public void onAdFailed(RFMAdView adView) {
                if (customEventInterstitialListener != null) {
                    customEventInterstitialListener.onAdFailedToLoad(111);
                }
            }

            public void onInterstitialAdWillDismiss(RFMAdView adView) {
                Log.v("LOG_TAG", "RFM Ad: Interstitial will dismiss");
            }

            public void onInterstitialAdDismissed(RFMAdView adView) {
                if (rfmAdView != null)
                    rfmAdView.setVisibility(View.GONE);
                RFMViewInterstitialActivity.dismissActivity();
                Log.v("LOG_TAG", "RFM Ad: Interstitial ad dismissed");
            }

            @Override
            public void onAdStateChangeEvent(RFMAdView arg0, RFMAdViewEvent event) {
                // For interstitial Ad this may not be exactly applicable
                switch (event) {
                    case FULL_SCREEN_AD_DISPLAYED:
                        Log.v("LOG_TAG", "RFM Ad: Full screen ad displayed");
                        break;

                    case FULL_SCREEN_AD_DISMISSED:
                        Log.v("LOG_TAG", "RFM Ad: Full screen ad dismissed");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onAdResized(RFMAdView arg0, int arg1, int arg2) {
                Log.v("LOG_TAG", "RFM Ad: Resized ");
            }

            @Override
            public void didDisplayAd(RFMAdView arg0) {
                if (RFMLog.canLogVerbose()) {
                    Log.v(LOG_TAG, " Into didDisplayAd ");
                }
            }

            @Override
            public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
                Log.v("LOG_TAG", "RFM Ad: Failed to display Ad ");
            }
        });

    }

    private HashMap<String, String> getTargetingParams() {
        HashMap<String, String> targetingHM = new HashMap<String, String>();
        targetingHM.putAll(localTargetingInfoHM);
        return targetingHM;
    }

    private void showFullScreenRFMInterstitial() {
        Intent i = new Intent(context, RFMViewInterstitialActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Activity) context).startActivity(i);
    }

    @Override
    public void showInterstitial() {
        if(rfmAdView != null && rfmAdView.isAdAvailableToDisplay() ||
                rfmAdView.getAdStateRO().getCurrentState() == AdState.AdViewState.INTERSTITIAL_DISP) {
            showFullScreenRFMInterstitial();
            if(customEventInterstitialListener != null) {
                customEventInterstitialListener.onAdOpened();
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "RFM : Gracefully clear RFM Ad view ");
        try {
            if (rfmAdView != null) {
                rfmAdView.rfmAdViewDestroy();
                rfmAdView = null;
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Failed to clean up RFM Adview "+e.getMessage());
        }
        customEventInterstitialListener = null;
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

}
