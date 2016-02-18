/*
 * Copyright (C) 2016 Rubicon Project. All rights reserved
 * 
 * Adapter for integrating RFM SDK with Admob SDK
 * RFM SDK will be triggered via Admob Custom Banner Event
 * version: 3.0.0
 * 
 */
package com.rfm.extras.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;

public class RFMAdmobAdapter implements CustomEventBanner {

	private static final String LOG_TAG = "RFMAdmobAdapter";
	private CustomEventBannerListener customEventListener;
	protected RFMAdView rfmAdView;
	private RFMAdRequest mAdRequest;
	private Context context;

	////RFM Placement Settings
	private static final String RFM_SERVER_NAME = "http://mrp.rubiconproject.com/";
	private static final String RFM_PUB_ID = "111008";

	@Override
	public void requestBannerAd(Context _context,
			CustomEventBannerListener listener, String serverParameter,
			AdSize adSize, MediationAdRequest mediationAdRequest,
			Bundle customEventExtras) {

		Log.d(LOG_TAG,"custom banner event trigger, appId: "+serverParameter);
		context = _context;
		customEventListener = listener;
		int adHeight = adSize.getHeight();
		int adWidth = adSize.getWidth();
		
		//Layout banner ad view
		rfmAdView = new RFMAdView(context);
		rfmAdView.setVisibility(View.GONE);

		// required to play video ads
		rfmAdView.enableHWAcceleration(true);

		//Set Ad Request parameters
		if(mAdRequest == null)
			mAdRequest = new RFMAdRequest();

		mAdRequest.setRFMParams(RFM_SERVER_NAME, RFM_PUB_ID, serverParameter);

		if(adSize.isFullWidth()) {
			mAdRequest.setAdDimensionParams(-1, adHeight);
		}else{
			mAdRequest.setAdDimensionParams(adWidth, adHeight);
		}

		setBannerAdViewListener();
		/*Optional Targeting Parameters*/
       	/*Uncomment and configure desired parameters*/
//		mAdRequest.setRFMAdMode(RFMConstants.RFM_AD_MODE_TEST);
//		mAdRequest.setRFMAdMode("9130");
//		mAdRequest.setLocation(null); //Pass a valid Location object for location targeting
//		mAdRequest.setTargetingParams(null);//Pass a valid Key-Value HashMap for k-v targeting
        /*End Optional Targeting Parameters*/

		//Request Ad
		if(!rfmAdView.requestRFMAd(mAdRequest)) {
			customEventListener.onAdFailedToLoad(11);
		}
	}

	public void setBannerAdViewListener() {
		if (rfmAdView == null) {
			return;
		}

		rfmAdView.setRFMAdViewListener(new RFMAdViewListener() {
			/**
			 * Sent when an ad request loaded an ad; this is a good opportunity to make the ad view
			 * visible if it has been set to invisible till this time.
			 *
			 * @param adView - RFMAdView instance that generated this method
			 *
			 */
			@Override
			public void onAdReceived(RFMAdView adView) {
				Log.d(LOG_TAG, "RFM :onAdReceived ");
				rfmAdView.setVisibility(View.VISIBLE);

				if (customEventListener != null) {
					customEventListener.onAdLoaded(adView);
				}
			}

			/**
			 * Sent when the ad request has been processed.
			 *
			 * @param adView -  RFMAdView instance that generated this method
			 * @param requestUrl - One of two possible values :
			 * a) Request URL if the ad request from client was accepted by SDK and a request will be potentially
			 * attempted.
			 * b) error message if the ad request from client was denied by SDK. Sent typically when the ad view is in a
			 * state where it cannot accept new ad requests.
			 * @param adRequestSuccess -
			 * a) true if the ad request from client was accepted by SDK and a request will be potentially
			 * attempted.
			 * b) false if the ad request from client was denied by SDK. Sent typically when the adview is in a
			 * state where it cannot accept new ad requests.
			 *
			 *
			 */
			@Override
			public void onAdRequested(RFMAdView adView, String requestUrl, boolean adRequestSuccess) {
				Log.d(LOG_TAG, "RequestRFMAd: " + requestUrl);
			}

			/**
			 * Sent when user interaction with ad results in transition of view type from banner to full screen
			 * landing view or vice-versa
			 *
			 * @param adView - RFMAdView instance that generated this method
			 * @param event - User interaction event of type RFMAdViewEvent
			 */
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

			//RFMAdView listener methods

			/**
			 * Sent when an ad request failed to load an ad. Client can choose to set the view to invisible
			 * if it was set to visible.
			 *
			 * @param adView - RFMAdView instance that generated this method
			 *
			 */

			@Override
			public void onAdFailed(RFMAdView adView) {
				Log.d(LOG_TAG, "RFM :onAdFailed ");
				rfmAdView.setVisibility(View.GONE);

				if (customEventListener != null) {
					// 222 is random code, can be customized
					customEventListener.onAdFailedToLoad(222);
				}
			}
		});

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
	}

	@Override
	public void onPause() {}

	@Override
	public void onResume() {}

}