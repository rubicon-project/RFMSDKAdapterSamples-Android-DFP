/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.admobadaptersample.sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class SQLiteHelper extends SQLiteOpenHelper {

    static final String SAMPLE_ADS_TABLE = "sampleads";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_TEST_CASE_NAME = "testCaseName";
    static final String COLUMN_SITE_ID = "siteId";
    static final String COLUMN_AD_TYPE = "adType";
    static final String COLUMN_REFRESH_COUNT = "refreshCount";
    static final String COLUMN_REFRESH_INTERVAL = "refreshInterval";
    static final String COLUMN_LOCATION_TYPE = "locationType";
    static final String COLUMN_LOCATION_PRECISION = "locationPrecision";
    static final String COLUMN_LAT = "latitude";
    static final String COLUMN_LONG = "longitude";
    static final String COLUMN_TARGETING_KEY_VALUE = "targetingKeyValue";
    static final String COLUMN_AD_WIDTH = "adWidth";
    static final String COLUMN_AD_HEIGHT = "adHeight";
    static final String COLUMN_TEST_MODE = "testMode";
    static final String COLUMN_AD_ID = "adId";
    static final String COLUMN_IS_CUSTOM = "isCustom";

    static final String COLUMN_RFM_SERVER = "rfmServer";
    static final String COLUMN_APP_ID = "appId";
    static final String COLUMN_PUB_ID = "pubId";

    private static final String DATABASE_NAME = "admobadsDatabase.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + SAMPLE_ADS_TABLE
            + " ("

            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TEST_CASE_NAME + " text not null, "
            + COLUMN_SITE_ID + " text not null, "
            + COLUMN_AD_TYPE + " text not null, "
            + COLUMN_REFRESH_COUNT + " integer not null, "
            + COLUMN_REFRESH_INTERVAL + " integer not null, "
            + COLUMN_LOCATION_TYPE + " text not null, "
            + COLUMN_LOCATION_PRECISION + " text not null, "
            + COLUMN_LAT + " text not null, "
            + COLUMN_LONG + " text not null, "
            + COLUMN_TARGETING_KEY_VALUE + " text not null, "
            + COLUMN_AD_WIDTH + " integer not null, "
            + COLUMN_AD_HEIGHT + " integer not null, "
            + COLUMN_TEST_MODE + " integer not null, "
            + COLUMN_AD_ID + " text not null, "
            + COLUMN_IS_CUSTOM + " integer not null, "

            + COLUMN_RFM_SERVER + " text not null, "
            + COLUMN_APP_ID + " text not null, "
            + COLUMN_PUB_ID + " text not null "

            + ");";

    private final Context mContext;

    SQLiteHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final List<AdUnit> adUnitList = new ArrayList<AdUnit>();

        adUnitList.add(new AdUnit(-1, "AdMob Banner", "/6253334/dfp_example_ad/banner",
                AdUnit.AdType.ADMOB_BANNER_AD, 1, 0, AdUnit.LocationType.NORMAL, "6","0.0", "0.0",
                "", 320, 50, true, "", false, "", "", "", 1));

        adUnitList.add(new AdUnit(-1, "AdMob Custom Banner", "/5300653/rfmsdk-sample-android",
                AdUnit.AdType.ADMOB_BANNER_AD, 1, 0, AdUnit.LocationType.NORMAL, "6","0.0", "0.0",
                "", 320, 50, true, "", false, "", "", "", 2));
        adUnitList.add(new AdUnit(-1, "AdMob Interstitial", "ca-app-pub-3940256099942544/1033173712",
                AdUnit.AdType.ADMOB_INTERSTITIAL_AD, 1, 0, AdUnit.LocationType.NORMAL, "6", "0.0", "0.0",
                "", 320, 50, true, "", false, "", "", "", 3));

        adUnitList.add(new AdUnit(-1, "AdMob Custom Interstitial", "/5300653/rfmsdk-sample-android",
                AdUnit.AdType.ADMOB_INTERSTITIAL_AD, 1, 0, AdUnit.LocationType.NORMAL, "6", "0.0", "0.0",
                "", -1, -1, true, "", false, "", "", "", 4));

        adUnitList.add(new AdUnit(-1, "AdMob FastLane Banner", "/5300653/rfmsdk-sample-android-fl",
                AdUnit.AdType.ADMOB_FASTLANE_BANNER_AD, 1, 0, AdUnit.LocationType.NORMAL, "6", "0.0", "0.0",
                "", 320, 50, true, "", false, "http://mrp.rubiconproject.com", "50144540EF720133146F22000B3510F7", "111008", 5));

        sqLiteDatabase.execSQL(DATABASE_CREATE);
        sqLiteDatabase.beginTransaction();

        for (final AdUnit adUnit : adUnitList) {
            final ContentValues values = new ContentValues();
            values.put(COLUMN_TEST_CASE_NAME, adUnit.getTestCaseName());
            values.put(COLUMN_SITE_ID, adUnit.getSiteId());
            values.put(COLUMN_AD_TYPE, adUnit.getActivityClassName());
            values.put(COLUMN_REFRESH_COUNT, adUnit.getRefreshCount());
            values.put(COLUMN_REFRESH_INTERVAL, adUnit.getRefreshInterval());
            values.put(COLUMN_LOCATION_TYPE, adUnit.getLocationType().getLocType());
            values.put(COLUMN_LOCATION_PRECISION, adUnit.getLocationPrecision());
            values.put(COLUMN_LAT, adUnit.getLat());
            values.put(COLUMN_LONG, adUnit.getLong());
            values.put(COLUMN_TARGETING_KEY_VALUE, adUnit.getTargetingKeyValue());
            values.put(COLUMN_AD_WIDTH, adUnit.getAdWidth());
            values.put(COLUMN_AD_HEIGHT, adUnit.getAdHeight());
            values.put(COLUMN_TEST_MODE, adUnit.getTestMode());
            values.put(COLUMN_AD_ID, adUnit.getAdId());
            values.put(COLUMN_IS_CUSTOM, 0);

            values.put(COLUMN_RFM_SERVER, adUnit.getRfmServer());
            values.put(COLUMN_APP_ID, adUnit.getAppId());
            values.put(COLUMN_PUB_ID, adUnit.getPubId());

            sqLiteDatabase.insert(SAMPLE_ADS_TABLE, null, values);
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Downgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        recreateDb(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        recreateDb(database);
    }

    private void recreateDb(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + SAMPLE_ADS_TABLE);
        onCreate(database);
    }
}
