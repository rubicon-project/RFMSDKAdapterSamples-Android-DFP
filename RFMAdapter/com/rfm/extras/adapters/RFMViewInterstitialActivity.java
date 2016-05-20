/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.extras.adapters;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.rfm.extras.adapters.RFMAdmobInterstitialAdapter;
import com.rfm.sdk.RFMAdView;

public class RFMViewInterstitialActivity extends Activity {
	
	private PublisherInterstitialAd admobInterstitial;
	private static Activity rfmViewInterstitial;
	private int CONTAINDER_ID = 100;
	private FrameLayout mFrameLayout;
	private RFMAdView mRFMAdView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setId(CONTAINDER_ID);
        RelativeLayout.LayoutParams lpForRelativeLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        
        mFrameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams lpForFrameLayout = new FrameLayout.LayoutParams(
        		ViewGroup.LayoutParams.FILL_PARENT,
        		ViewGroup.LayoutParams.FILL_PARENT,
        		Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        mFrameLayout.setLayoutParams(lpForFrameLayout);
        
        mRFMAdView = RFMAdmobInterstitialAdapter.rfmAdView;
        if(mRFMAdView != null) {

            mRFMAdView.setVisibility(View.VISIBLE);
        }

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            if (mRFMAdView.getParent() != null)
                ((ViewGroup) mRFMAdView.getParent()).removeView(mRFMAdView);
        }

        if (mFrameLayout.getChildCount() == 0)
        	mFrameLayout.addView(mRFMAdView);
  
        relativeLayout.addView(mFrameLayout);
 
        setContentView(relativeLayout, lpForRelativeLayout);
        
        rfmViewInterstitial = this;
 
    }
    
    public static void dismissActivity() {
        if (rfmViewInterstitial != null)
    	    rfmViewInterstitial.finish();
        rfmViewInterstitial = null;
    }
    
	@Override 
    public void onDestroy() { 
        super.onDestroy();
        if (mFrameLayout != null && mRFMAdView != null) {
            mRFMAdView.rfmAdViewDestroy();
            mFrameLayout.removeView(mRFMAdView);
        }

        RFMAdmobInterstitialAdapter.rfmAdView = null;
        mRFMAdView = null;

        if (RFMAdmobInterstitialAdapter.customEventInterstitialListener != null)
            RFMAdmobInterstitialAdapter.customEventInterstitialListener.onAdClosed();

        RFMAdmobInterstitialAdapter.customEventInterstitialListener = null;
        rfmViewInterstitial = null;

        if (admobInterstitial != null) {
        	admobInterstitial.setAdListener(null);
            admobInterstitial = null;
        }
    } 
	
}
