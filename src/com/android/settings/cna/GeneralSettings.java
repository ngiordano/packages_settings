/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.settings.cna;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.Spannable;
import android.widget.EditText;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.util.CMDProcessor;
import com.android.settings.util.Helpers;

public class GeneralSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String PREF_CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final String COMBINED_BAR_AUTO_HIDE = "combined_bar_auto_hide";
    private static final String PREF_TRANSPARENCY = "status_bar_transparency";
    private static final String PREF_ENABLE_VOLUME_OPTIONS = "enable_volume_options";

    Preference mCustomLabel;
    CheckBoxPreference mCombinedBarAutoHide;
    CheckBoxPreference mHorizontalAppSwitcher;
    ListPreference mTransparency;
    CheckBoxPreference mEnableVolumeOptions;

    String mCustomLabelText = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.general_settings);

        PreferenceScreen prefs = getPreferenceScreen();

        mCombinedBarAutoHide = (CheckBoxPreference) findPreference(COMBINED_BAR_AUTO_HIDE);
        mCombinedBarAutoHide.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.COMBINED_BAR_AUTO_HIDE, 0) == 1));

        mHorizontalAppSwitcher = (CheckBoxPreference) findPreference("horizontal_recents_task_panel");
        mHorizontalAppSwitcher.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.HORIZONTAL_RECENTS_TASK_PANEL, 0) == 1);

        mTransparency = (ListPreference) findPreference(PREF_TRANSPARENCY);
        mTransparency.setOnPreferenceChangeListener(this);
        mTransparency.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUS_BAR_TRANSPARENCY, 0)));

        mEnableVolumeOptions = (CheckBoxPreference) findPreference(PREF_ENABLE_VOLUME_OPTIONS);
        mEnableVolumeOptions.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.ENABLE_VOLUME_OPTIONS, 0) == 1);

        mCustomLabel = findPreference(PREF_CUSTOM_CARRIER_LABEL);
        updateCustomLabelTextSummary();
    }

    private void updateCustomLabelTextSummary() {
        mCustomLabelText = Settings.System.getString(getActivity().getContentResolver(),
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (mCustomLabelText == null) {
            mCustomLabel
            .setSummary("Once set a reboot is required");
        } else {
            mCustomLabel.setSummary(mCustomLabelText);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;

        if (preference == mTransparency) {
            int val = Integer.parseInt((String) newValue);
            result = Settings.System.putInt(getActivity().getContentResolver(),
                     Settings.System.STATUS_BAR_TRANSPARENCY, val);
            restartSystemUI();
        }
        return result;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mCustomLabel) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            alert.setTitle("Custom Carrier Label");
            alert.setMessage("Please enter a new one!");

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(mCustomLabelText != null ? mCustomLabelText : "");
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = ((Spannable) input.getText()).toString();
                    Settings.System.putString(getActivity().getContentResolver(),
                            Settings.System.CUSTOM_CARRIER_LABEL, value);
                    updateCustomLabelTextSummary();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();

        } else if (preference == mCombinedBarAutoHide) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.COMBINED_BAR_AUTO_HIDE,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mHorizontalAppSwitcher) {
            boolean checked = ((CheckBoxPreference) preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.HORIZONTAL_RECENTS_TASK_PANEL,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            restartSystemUI();
            return true;

        } else if (preference == mEnableVolumeOptions) {
            boolean checked = ((CheckBoxPreference) preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.ENABLE_VOLUME_OPTIONS, checked ? 1 : 0);
            return true;

        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void restartSystemUI() {
        try {
            Runtime.getRuntime().exec("pkill -TERM -f  com.android.systemui");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
