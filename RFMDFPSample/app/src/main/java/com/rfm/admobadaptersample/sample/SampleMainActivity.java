/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.admobadaptersample.sample;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.DismissableManager;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.rfm.admobadaptersample.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SampleMainActivity extends AppCompatActivity {

    private String LOG_TAG = "SampleMainActivity";
    private static final int INITIAL_DELAY_MILLIS = 300;
    private AdDataSource mAdDataSource;
    private DynamicListView mSampleAdsListView;
    private SampleListAdapter mSampleAdsListAdapter;
    private long firstAdUnitId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sample_toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("RFM DFP Sample");

        mAdDataSource = AdDataSource.getInstance(this);

        mSampleAdsListView = (DynamicListView) findViewById(R.id.dynamic_list);
        mSampleAdsListAdapter = new SampleListAdapter(this);


        SimpleSwipeUndoAdapter simpleSwipeUndoAdapter = new SimpleSwipeUndoAdapter(
                mSampleAdsListAdapter, this, new MyOnDismissCallback(mSampleAdsListAdapter));
        AlphaInAnimationAdapter animAdapter = new AlphaInAnimationAdapter(simpleSwipeUndoAdapter);
        animAdapter.setAbsListView(mSampleAdsListView);
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        mSampleAdsListView.setAdapter(animAdapter);

        mSampleAdsListView.enableSimpleSwipeUndo();

        mSampleAdsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Launch the correct activity when list view item is clicked
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    AdUnit adUnit = (AdUnit) mSampleAdsListAdapter.getItem(position);
                    Intent i = new Intent(getApplicationContext(),
                            Class.forName(adUnit.getActivityClass().getName()));
                    i.putExtras(adUnit.toBundle());
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mSampleAdsListView.setDismissableManager(
                new DismissableManager() {
                    @Override
                    public boolean isDismissable(final long id, final int position) {
                        /* Don't allow swiping the headers and predefined items. */
                        if (!mSampleAdsListAdapter.isEnabled(position)) {
                            return false;
                        }
                        AdUnit adUnit = (AdUnit) mSampleAdsListAdapter.getItem(position);
                        if (!adUnit.isCustom()) {
                            return false;
                        }
                        return true;
                    }
                }
        );

        updateListUI();

        registerReceiver(mReceiver, new IntentFilter(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));

        setJsonToDBUploader();
    }

    private DatabaseChangedReceiver mReceiver = new DatabaseChangedReceiver() {
        public void onReceive(Context context, Intent intent) {
            updateListUI();
        }
    };

    private class MyOnDismissCallback implements OnDismissCallback {

        private final SampleListAdapter mAdapter;

        MyOnDismissCallback(final SampleListAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
            for (int position : reverseSortedPositions) {
                AdUnit adUnit = (AdUnit) mSampleAdsListAdapter.getItem(position);
                deleteAdUnit(adUnit);
            }
        }
    }

    private void addAdUnit(final AdUnit adUnit) {
        AdUnit createdAdUnit = mAdDataSource.createAdUnit(adUnit);
        if (createdAdUnit != null) {
            Log.d(LOG_TAG, "Create new ad unit success!");
        }
        updateListUI();
    }

    private void deleteAdUnit(final AdUnit adUnit) {
        mAdDataSource.deleteSampleAdUnit(adUnit);
        mSampleAdsListAdapter.remove(adUnit);
        Utils.snackbar(SampleMainActivity.this, getResources().getString(R.string.item_deleted), true);
        updateListUI();
    }

    protected void showInputDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(SampleMainActivity.this);

        View promptView = layoutInflater.inflate(R.layout.dialog_add_test_case, null);
        final EditText testCaseNameEditText = (EditText) promptView.findViewById(R.id.test_case_name_edittext);
        final EditText siteIdEditText = (EditText) promptView.findViewById(R.id.site_id_editext);
        final Spinner adTypeSpinner = (Spinner) promptView.findViewById(R.id.ad_type_spinner);

        final AdUnit.AdType[] adTypes = AdUnit.AdType.values();
        final List<String> adTypeStrings = new ArrayList<String>(adTypes.length);

        for (final AdUnit.AdType adType : adTypes) {
            adTypeStrings.add(adType.getName());
        }

        adTypeSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, adTypeStrings));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SampleMainActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        final String testCaseName = testCaseNameEditText.getText().toString();
                        if (testCaseName.isEmpty()) {
                            Utils.snackbar(SampleMainActivity.this, "Test case Name is empty", false);
                            return;
                        }

                        final String siteId = siteIdEditText.getText().toString();
                        if (testCaseName.isEmpty()) {
                            Utils.snackbar(SampleMainActivity.this, "SiteId is empty", false);
                            return;
                        }

                        final AdUnit.AdType adType = adTypes[adTypeSpinner.getSelectedItemPosition()];
                        final AdUnit adUnit = new AdUnit(-1, testCaseName, siteId, adType,
                                1, 0, AdUnit.LocationType.NORMAL, "6", "0.0", "0.0",
                                "", 320, 50, true, "", true, "", "", "");
                        addAdUnit(adUnit);

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    private void updateListUI() {
        boolean addedUserDefinedHeader = false;
        mSampleAdsListAdapter.clear();
        mSampleAdsListAdapter.add(new SampleListHeader("PRESET"));
        final List<AdUnit> adUnits = mAdDataSource.getAllAdUnits();

        firstAdUnitId = adUnits.get(0).getId();
        int count = 0;
        for (final AdUnit adUnit : adUnits) {
            Log.d(LOG_TAG, "_ID: "+ adUnit.getId());
            if (adUnit.isCustom() && !addedUserDefinedHeader) {
                addedUserDefinedHeader = true;
                mSampleAdsListAdapter.add(new SampleListHeader("USER DEFINED"));
            }
            count = count + 1;
            adUnit.setCount(count);
            mSampleAdsListAdapter.add(adUnit);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_testcase:
                showInputDialog();
                return true;
            case R.id.action_settings:
                Intent sampleSettingsIntent = new Intent(getApplicationContext(), SampleSettings.class);
                sampleSettingsIntent.putExtra(AdUnit.ID, firstAdUnitId);
                startActivity(sampleSettingsIntent);
                return true;
            case R.id.action_about:
                Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(aboutIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mAdDataSource.cleanUp();
    }


    // ----  handle the click of transparent rect to upload data into DB ------
    final String INPUT_JSON = "/rubicon/dfpadaptersample/input.json";
    private static int clickCount = 0;
    private Handler mTimerHandler = new Handler();
    private Runnable counterResetRunnable = new Runnable() {
        @Override
        public void run() {
            clickCount = 0;
        }
    };

    private void resetCount(){
        mTimerHandler.removeCallbacks(counterResetRunnable);
        mTimerHandler.postDelayed(counterResetRunnable, 3000);
    }

    private void setJsonToDBUploader() {
        final View uploadFileToDB = findViewById(R.id.upload_file_to_db);
        uploadFileToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCount();
                if (clickCount == 10) {
                    uploadJsonToDB();
                }
                clickCount++;
            }
        });
    }

    private void uploadJsonToDB() {
        String inputJsonPath = Environment.getExternalStorageDirectory().getPath() + INPUT_JSON;
        File file = new File(inputJsonPath);
        if (!file.exists()) {
            Utils.snackbar(SampleMainActivity.this, getResources().getString(R.string.json_file_not_found), false);
            return;
        }

        String jsonString = JsonToSQLLite.readFileToString(inputJsonPath);
        boolean saveStatus = JsonToSQLLite.saveToDB(getApplicationContext(), jsonString);
        if (saveStatus) {
            Utils.snackbar(SampleMainActivity.this, getResources().getString(R.string.db_upload_success), true);
        } else {
            Utils.snackbar(SampleMainActivity.this, getResources().getString(R.string.db_upload_failure), false);
        }
        updateListUI();
    }
    // -------------------------------------------------------------------
}