/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.notificationlight;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.preference.Preference;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.Utils;

public class ApplicationLightPreference extends Preference implements
    View.OnClickListener {

    private static String TAG = "AppLightPreference";
    public static final int DEFAULT_TIME = 1000;
    public static final int DEFAULT_COLOR = 0xFFFFFF; //White

    private ImageView mLightColorView;
    private TextView mOnValueView;
    private TextView mOffValueView;

    private int mColorValue;
    private int mOnValue;
    private int mOffValue;
    private OnLongClickListener mParent;
    private Resources mResources;

    public ApplicationLightPreference(Context context, int color, int onValue, int offValue) {
        super(context);
        mColorValue = color;
        mOnValue = onValue;
        mOffValue = offValue;
        mParent = null;
        init();
    }

    public ApplicationLightPreference(Context context, OnLongClickListener parent, int color, int onValue, int offValue) {
        super(context);
        mColorValue = color;
        mOnValue = onValue;
        mOffValue = offValue;
        mParent = parent;
        init();
    }

    private void init() {
        setLayoutResource(R.layout.preference_application_light);
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        View view = super.getView(convertView, parent);

        View lightPref = (LinearLayout) view.findViewById(R.id.app_light_pref);
        if ((lightPref != null) && lightPref instanceof LinearLayout) {
            lightPref.setOnClickListener(this);
            if (mParent != null) {
                lightPref.setOnLongClickListener(mParent);
            }
        }

        return view;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mLightColorView = (ImageView) view.findViewById(R.id.light_color);
        mOnValueView = (TextView) view.findViewById(R.id.textViewTimeOnValue);
        mOffValueView = (TextView) view.findViewById(R.id.textViewTimeOffValue);

        // Hide the summary text - it takes up too much space on a low res device
        // We use it for storing the package name for the longClickListener
        TextView tView = (TextView) view.findViewById(android.R.id.summary);
        tView.setVisibility(View.GONE);

        mResources = getContext().getResources();
        updatePreferenceViews();
    }

    private void updatePreferenceViews() {
        final int width = (int) mResources.getDimension(R.dimen.device_memory_usage_button_width);
        final int height = (int) mResources.getDimension(R.dimen.device_memory_usage_button_height);

        if (mLightColorView != null) {
            mLightColorView.setEnabled(true);
            mLightColorView.setImageDrawable(createRectShape(width, height, 0xFF000000 + mColorValue));
        }
        if (mOnValueView != null) {
            mOnValueView.setText(mapLengthValue(mOnValue));
        }
        if (mOffValueView != null) {
            if (mOnValue == 0) {
                mOffValueView.setVisibility(View.GONE);
            } else {
                mOffValueView.setVisibility(View.VISIBLE);
            }
            mOffValueView.setText(mapSpeedValue(mOffValue));
        }
    }

    @Override
    public void onClick(View v) {
        if ((v != null) && (R.id.app_light_pref == v.getId())) {
            editPreferenceValues();
        }
    }

    private void editPreferenceValues()
    {
        final LightSettingsDialog d = new LightSettingsDialog(getContext(), 0xFF000000 + mColorValue, mOnValue, mOffValue);
        final int width = (int) mResources.getDimension(R.dimen.dialog_light_settings_width);
        d.setAlphaSliderVisible(false);
        Resources resources = getContext().getResources();

        d.setButton(resources.getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mColorValue =  d.getColor() - 0xFF000000; // strip alpha, led does not support it
                mOnValue = d.getPulseSpeedOn();
                mOffValue = d.getPulseSpeedOff();
                updatePreferenceViews();
                callChangeListener(this);
            }
        });

        d.setButton2(resources.getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        d.show();

        if (Utils.isScreenLarge()) {
            // Make the dialog smaller on large screen devices
            d.getWindow().setLayout(width, LayoutParams.WRAP_CONTENT);
        }
    }

    /**
     * Getters and Setters
     */

    public int getColor() {
        return mColorValue;
    }

    public void setColor(int color) {
        mColorValue = color;
        updatePreferenceViews();
    }

    public void setOnValue(int value) {
        mOnValue = value;
        updatePreferenceViews();
    }

    public int getOnValue() {
        return mOnValue;
    }

    public void setOffValue(int value) {
        mOffValue = value;
        updatePreferenceViews();
    }

    public int getOffValue() {
        return mOffValue;
    }

    public void setOnOffValue(int onValue, int offValue) {
        mOnValue = onValue;
        mOffValue = offValue;
        updatePreferenceViews();
    }

    /**
     * Utility methods
     */

    private static ShapeDrawable createRectShape(int width, int height, int color) {
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.setIntrinsicHeight(height);
        shape.setIntrinsicWidth(width);
        shape.getPaint().setColor(color);
        return shape;
    }

    private String mapLengthValue(Integer time) {
        if (time == DEFAULT_TIME)
            return getContext().getString(R.string.default_time);

        for (String entry : mResources.getStringArray(R.array.notification_pulse_length_entries)) {
            String[] values = entry.split("\\|", -1);

            if (values.length != 2)
                continue;

            if (Integer.decode(values[1]).equals(time))
                return values[0];
        }
        return getContext().getString(R.string.custom_time);
    }

    private String mapSpeedValue(Integer time) {
        if (time == DEFAULT_TIME)
            return getContext().getString(R.string.default_time);

        for (String entry : mResources.getStringArray(R.array.notification_pulse_speed_entries)) {
            String[] values = entry.split("\\|", -1);

            if (values.length != 2)
                continue;

            if (Integer.decode(values[1]).equals(time))
                return values[0];
        }
        return getContext().getString(R.string.custom_time);
    }
}