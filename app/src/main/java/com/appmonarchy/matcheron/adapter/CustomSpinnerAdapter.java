package com.appmonarchy.matcheron.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    public CustomSpinnerAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
    }

    @Override
    public boolean isEnabled(int position) {
        // Disable the first item from Spinner
        // First item will be used for hint
        return position != 0;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        // set the color of first item in the drop down list to gray
        if (position == 0) {
            view.setTextColor(Color.parseColor("#BFBFBF"));
        } else {
            view.setTextColor(Color.BLACK);
        }
        return view;
    }
}