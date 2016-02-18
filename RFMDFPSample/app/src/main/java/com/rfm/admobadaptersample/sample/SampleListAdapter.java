/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.admobadaptersample.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nhaarman.listviewanimations.ArrayAdapter;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;
import com.rfm.admobadaptersample.R;

import java.util.ArrayList;

public class SampleListAdapter extends ArrayAdapter<Object> implements UndoAdapter {

    private Activity mContext;

    public enum RowType {
        HEADER_ITEM, LIST_ITEM
    }

    public SampleListAdapter(Activity context) {
        super(new ArrayList<Object>());
        this.mContext = context;
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        Object currentItem = getItem(position);
        if (currentItem instanceof AdUnit) {
            return RowType.LIST_ITEM.ordinal();
        } else {
            return RowType.HEADER_ITEM.ordinal();
        }
    }

    @NonNull
    @Override
    public View getUndoView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.sample_list_undo_row, parent, false);
        }
        return view;
    }

    @NonNull
    @Override
    public View getUndoClickView(@NonNull final View view) {
        return view.findViewById(R.id.undo_row_undobutton);
    }

    private static class HeaderHolder {
        public TextView headerTitle;
    }

    private static class ListItemHolder {
        public TextView testCaseName;
        public TextView siteId;
        public TextView testNumber;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int viewType = this.getItemViewType(position);
        switch (viewType) {
            // header_item view
            case 0:
                HeaderHolder headerHolder;
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.header_item, parent, false);

                    headerHolder = new HeaderHolder();
                    headerHolder.headerTitle = (TextView) v.findViewById(R.id.separator);
                    v.setTag(headerHolder);
                } else {
                    headerHolder = (HeaderHolder) v.getTag();
                }

                // set up the list item
                if (headerHolder.headerTitle != null) {
                    SampleListHeader sampleHeader = (SampleListHeader) getItem(position);
                    headerHolder.headerTitle.setText(sampleHeader.title());
                }

                // return the created view
                return v;

            // ad type list item
            case 1:

                ListItemHolder listItemHolder;
                View v2 = convertView;
                if (v2 == null) {
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v2 = vi.inflate(R.layout.list_item, parent, false);

                    listItemHolder = new ListItemHolder();
                    listItemHolder.testCaseName = (TextView) v2.findViewById(R.id.maintitle);
                    listItemHolder.siteId = (TextView) v2.findViewById(R.id.description);
                    listItemHolder.testNumber = (TextView) v2.findViewById(R.id.item_number);
                    v2.setTag(listItemHolder);
                } else {
                    listItemHolder = (ListItemHolder) v2.getTag();
                }

                final AdUnit adUnit = (AdUnit) getItem(position);

                // set up the list item
                if (listItemHolder.testCaseName != null) {
                    listItemHolder.testCaseName.setText(adUnit.getTestCaseName());
                }
                if (listItemHolder.siteId != null) {
                    listItemHolder.siteId.setText(adUnit.getSiteId());
                }
                if (listItemHolder.testNumber != null) {
                    listItemHolder.testNumber.setText(adUnit.getCount() + "");
                }

                // return the created view
                return v2;
            default:
                return null;
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        int itemType = getItemViewType(position);
        if (itemType == RowType.HEADER_ITEM.ordinal()) {
            return false;
        } else {
            return true;
        }
    }
}
