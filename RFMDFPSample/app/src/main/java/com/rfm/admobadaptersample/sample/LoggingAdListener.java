/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.admobadaptersample.sample;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.rfm.sdk.RFMAdView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

/**
 * An {@link AdListener} that logs ad events.
 */
public class LoggingAdListener extends AdListener {
  /** Called when an ad is loaded. */
  Context context = null;
  private static final String TAG = "LoggingAdListener";
  public LoggingAdListener(Context _context) {
	  context = _context;
  }

  @Override
  public void onAdLoaded() {
    Log.d("LOG_TAG", "onAdLoaded");
	AlertDialog.Builder builder = new AlertDialog.Builder(context);
	builder.setMessage("Ad loaded from RFMSDK").setTitle(
			"BannerSamples");

	
	// Add the buttons
	builder.setPositiveButton(android.R.string.ok,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//startImageDownload();
					Log.v(TAG, " Clicked on postiive Button of the dialog");
				}
			});
  }

  /** Called when an ad failed to load. */
  @Override
  public void onAdFailedToLoad(int errorCode) {
    String message = String.format("onAdFailedToLoad (%s)", getErrorReason(errorCode));
	AlertDialog.Builder builder = new AlertDialog.Builder(context);
	builder.setMessage("Ad failed from RFMSDK").setTitle(
			"BannerSamples");


	// Add the buttons
	builder.setPositiveButton(android.R.string.ok,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//startImageDownload();
					Log.v(TAG, " Clicked on postiive Button of the dialog");
				}
			});
	AlertDialog storeImageDialog = builder.create();
	storeImageDialog.show();
	
   Log.d("LOG_TAG", message);
    
  }

  /**
   * Called when an Activity is created in front of the app (e.g. an interstitial is shown, or an
   * ad is clicked and launches a new Activity).
   */
  @Override
  public void onAdOpened() {
    Log.d("LOG_TAG", "onAdOpened");
  }

  /** Called when an ad is closed and about to return to the application. */
  @Override
  public void onAdClosed() {
    Log.d("LOG_TAG", "onAdClosed");
  }

  /**
   * Called when an ad is clicked and going to start a new Activity that will leave the
   * application (e.g. breaking out to the Browser or Maps application).
   */
  @Override
  public void onAdLeftApplication() {
    Log.d("LOG_TAG","onAdLeftApplication");
    
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
}
