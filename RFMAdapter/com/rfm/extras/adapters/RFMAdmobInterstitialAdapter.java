/*
  * Copyright (C) 2016 Rubicon Project. All rights reserved
 *
 * @author: Rubicon Project.
 *  file for integrating RFM SDK with Admob SDK
 *  RFM SDK will be triggered via Admob Custom Interstitial Event
 */

package com.rfm.extras.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;
import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMInterstitialAd;
import com.rfm.sdk.RFMInterstitialAdViewListener;
import com.rfm.util.RFMLog;

import java.util.HashMap;

public class RFMAdmobInterstitialAdapter implements CustomEventInterstitial {

    private final String LOG_TAG = "RFMAdmobInterstitialAdp";
    private CustomEventInterstitialListener mCustomEventInterstitialListener;
    private RFMInterstitialAd mRFMInterstitialAdView;
    private RFMAdRequest mAdRequest;
    private Context mContext;

    ////RFM Placement Settings
    private static final String RFM_SERVER_NAME = "http://mrp.rubiconproject.com/";
    private static final String RFM_PUB_ID = "111008";

    private HashMap<String, String> localTargetingInfoHM = new HashMap<String, String>();

    public RFMAdmobInterstitialAdapter() {
        localTargetingInfoHM.put("adp_version", "dfp_adp_3.2.0");
    }

    @Override
    public void requestInterstitialAd(Context _context, CustomEventInterstitialListener _customEventInterstitialListener, String serverParameter, MediationAdRequest mediationAdRequest, Bundle bundle) {

        mContext = _context;
        Log.d(LOG_TAG, "custom interstitial event trigger, appId: " + serverParameter);
        mCustomEventInterstitialListener = _customEventInterstitialListener;

        if (mContext == null) {
            mCustomEventInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        // Create RFM Interstitial Ad
        if (mRFMInterstitialAdView == null) {
            mRFMInterstitialAdView = new RFMInterstitialAd(mContext);
        }

        // Set Ad Request parameters
        if (mAdRequest == null) {
            mAdRequest = new RFMAdRequest();
        }

        mAdRequest.setRFMParams(RFM_SERVER_NAME, RFM_PUB_ID, serverParameter);
        mAdRequest.setRFMAdAsInterstitial(true);
        HashMap<String, String> targetingParamsHM = getTargetingParams();
        mAdRequest.setTargetingParams(targetingParamsHM);

        // Set Listener
        setInterstitialAdViewListener();

        // Request Ad
        if (!mRFMInterstitialAdView.requestRFMAd(mAdRequest)) {
            mCustomEventInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
        }
    }

    private void setInterstitialAdViewListener() {

        if (mRFMInterstitialAdView == null) {
            return;
        }

        mRFMInterstitialAdView.setRFMInterstitialAdListener(new RFMInterstitialAdViewListener() {
            @Override
            public void onInterstitialAdWillDismiss(RFMAdView rfmAdView) {
                Log.v("LOG_TAG", "RFM Ad: Interstitial will dismiss");
            }

            @Override
            public void onInterstitialAdDismissed(RFMAdView rfmAdView) {
                Log.v("LOG_TAG", "RFM Ad: Interstitial ad dismissed");
            }

            @Override
            public void onAdRequested(RFMAdView adView, String requestUrl, boolean adRequestSuccess) {
                Log.v("LOG_TAG", "RFM Ad: Requesting Url:" + requestUrl);
            }

            @Override
            public void onAdReceived(RFMAdView adView) {
                if (mCustomEventInterstitialListener != null) {
                    mCustomEventInterstitialListener.onAdLoaded();
                }
            }

            @Override
            public void onAdFailed(RFMAdView adView) {
                if (mCustomEventInterstitialListener != null) {
                    mCustomEventInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
                }
            }

            @Override
            public void onAdStateChangeEvent(RFMAdView adView, RFMAdViewEvent event) {
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
            public void onAdResized(RFMAdView adView, int width, int height) {
                Log.v("LOG_TAG", "RFM Ad: Resized ");
            }

            @Override
            public void didDisplayAd(RFMAdView adView) {
                if (RFMLog.canLogVerbose()) {
                    Log.v(LOG_TAG, " Into didDisplayAd ");
                }
            }

            @Override
            public void didFailedToDisplayAd(RFMAdView adView, String errorMessage) {
                Log.v("LOG_TAG", "RFM Ad: Failed to display Ad ");
            }
        });
    }

    private HashMap<String, String> getTargetingParams() {
        HashMap<String, String> targetingHM = new HashMap<String, String>();
        targetingHM.putAll(localTargetingInfoHM);
        return targetingHM;
    }

    @Override
    public void showInterstitial() {
        mRFMInterstitialAdView.show();
        if (mCustomEventInterstitialListener != null)
            mCustomEventInterstitialListener.onAdOpened();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "RFM : Gracefully clear RFM Ad view ");
        try {
            if (mRFMInterstitialAdView != null) {
                mRFMInterstitialAdView.setRFMInterstitialAdListener(null);
                mRFMInterstitialAdView.reset();
                mRFMInterstitialAdView = null;
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Failed to clean up RFM Adview " + e.getMessage());
        }
        mCustomEventInterstitialListener = null;
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

}
