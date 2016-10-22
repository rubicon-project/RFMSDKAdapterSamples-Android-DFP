/*
  * Copyright (C) 2016 Rubicon Project. All rights reserved
 * 
 * @author: Rubicon Project.
 *  file for integrating RFM SDK with Admob SDK
 *  RFM SDK will be triggered via Admob Custom Banner Event
 * 
 */
package com.rfm.extras.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;

import java.util.HashMap;

public class RFMAdmobAdapter implements CustomEventBanner {

    private final String LOG_TAG = "RFMAdmobAdapter";
    private CustomEventBannerListener customEventListener;
    private RFMAdView rfmAdView;
    private RFMAdRequest mAdRequest;
    private Context mContext;

    ////RFM Placement Settings
    private final String RFM_SERVER_NAME = "http://mrp.rubiconproject.com/";
    private final String RFM_PUB_ID = "111008";

    private HashMap<String, String> localTargetingInfoHM = new HashMap<String, String>();

    public RFMAdmobAdapter() {
        localTargetingInfoHM.put("adp_version", "dfp_adp_3.2.0");
    }

    @Override
    public void requestBannerAd(Context _context,
                                CustomEventBannerListener listener, String serverParameter,
                                AdSize adSize, MediationAdRequest mediationAdRequest,
                                Bundle customEventExtras) {

        Log.d(LOG_TAG, "custom banner event trigger, appId: " + serverParameter);
        mContext = _context;
        customEventListener = listener;
        int adHeight = adSize.getHeight();
        int adWidth = adSize.getWidth();

        // Create RFM Ad view
        rfmAdView = new RFMAdView(mContext);
        rfmAdView.setVisibility(View.GONE);

        // Set Ad Request parameters
        if (mAdRequest == null)
            mAdRequest = new RFMAdRequest();

        mAdRequest.setRFMParams(RFM_SERVER_NAME, RFM_PUB_ID, serverParameter);
        if (adSize.isFullWidth()) {
            mAdRequest.setAdDimensionParams(-1, adHeight);
        } else {
            mAdRequest.setAdDimensionParams(adWidth, adHeight);
        }
        HashMap<String, String> targetingParamsHM = getTargetingParams();
        mAdRequest.setTargetingParams(targetingParamsHM);

        // Set listener
        setBannerAdViewListener();

        // Optional Targeting Parameters
        // Uncomment and configure desired parameters
//		mAdRequest.setRFMAdMode(RFMConstants.RFM_AD_MODE_TEST);
//		mAdRequest.setRFMAdMode("9130");
//		mAdRequest.setLocation(null); //Pass a valid Location object for location targeting
//		mAdRequest.setTargetingParams(null);//Pass a valid Key-Value HashMap for k-v targeting
        // End Optional Targeting Parameters

        // Request Ad
        if (!rfmAdView.requestRFMAd(mAdRequest)) {
            customEventListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
        }
    }

    private void setBannerAdViewListener() {
        if (rfmAdView == null) {
            return;
        }

        rfmAdView.setRFMAdViewListener(new RFMAdViewListener() {
            @Override
            public void onAdReceived(RFMAdView adView) {
                Log.d(LOG_TAG, "RFM :onAdReceived ");
                if (rfmAdView != null)
                    rfmAdView.setVisibility(View.VISIBLE);

                if (customEventListener != null) {
                    customEventListener.onAdLoaded(adView);
                }
            }

            @Override
            public void onAdRequested(String requestUrl, boolean adRequestSuccess) {
                Log.d(LOG_TAG, "RequestRFMAd: " + requestUrl);
            }

            @Override
            public void onAdStateChangeEvent(RFMAdView adView, RFMAdViewEvent event) {
                Log.d(LOG_TAG, "RFM :onAdStateChangeEvent: " + event);
                switch (event) {
                    case FULL_SCREEN_AD_WILL_DISPLAY:
                        break;
                    case FULL_SCREEN_AD_DISPLAYED:
                        customEventListener.onAdOpened();
                        break;

                    case FULL_SCREEN_AD_WILL_DISMISS:
                        break;
                    case FULL_SCREEN_AD_DISMISSED:
                        customEventListener.onAdClosed();
                        break;
                    case AD_DISMISSED:
                        break;
                }
            }

            @Override
            public void didDisplayAd(RFMAdView arg0) {
                Log.v(LOG_TAG, "Ad did display");
            }

            @Override
            public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
                Log.v(LOG_TAG, "Failed to display ad");
            }

            @Override
            public void onAdResized(RFMAdView arg0, int arg1, int arg2) {
                Log.v(LOG_TAG, "Ad resized");
            }

            @Override
            public void onAdFailed(RFMAdView adView) {
                Log.d(LOG_TAG, "RFM :onAdFailed ");
                if (rfmAdView != null)
                    rfmAdView.setVisibility(View.GONE);

                if (customEventListener != null) {
                    customEventListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
                }
            }
        });

    }

    private HashMap<String, String> getTargetingParams() {
        HashMap<String, String> targetingHM = new HashMap<String, String>();
        targetingHM.putAll(localTargetingInfoHM);
        return targetingHM;
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
            Log.d(LOG_TAG, "Failed to clean up RFM Adview " + e.getMessage());
        }
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

}