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

package com.android.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

/**
 * Performance Settings
 */
public class PerformanceSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "PerformanceSettings";

    private static final String PROCESSOR = "processor";

    private static final String MEMORY_MANAGEMENT = "memory_management";

    private static final String DISABLE_BOOTANIMATION_PREF = "pref_disable_bootanimation";

    private static final String DISABLE_BOOTANIMATION_PERSIST_PROP = "persist.sys.nobootanimation";

    private static final String DISABLE_BOOTANIMATION_DEFAULT = "0";

    private CheckBoxPreference mDisableBootanimPref;

    private PreferenceScreen mProcessor;

    private PreferenceScreen mMemoryManagement;

    private AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getPreferenceManager() != null) {

            addPreferencesFromResource(R.xml.performance_settings);

            PreferenceScreen prefSet = getPreferenceScreen();

            mProcessor = (PreferenceScreen) prefSet.findPreference(PROCESSOR);
            mMemoryManagement = (PreferenceScreen) prefSet.findPreference(MEMORY_MANAGEMENT);
            mDisableBootanimPref = (CheckBoxPreference) prefSet
                    .findPreference(DISABLE_BOOTANIMATION_PREF);

            String disableBootanimation = SystemProperties.get(DISABLE_BOOTANIMATION_PERSIST_PROP,
                    DISABLE_BOOTANIMATION_DEFAULT);
            mDisableBootanimPref.setChecked("1".equals(disableBootanimation));
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mDisableBootanimPref) {
            SystemProperties.set(DISABLE_BOOTANIMATION_PERSIST_PROP,
                    mDisableBootanimPref.isChecked() ? "1" : "0");
        } else {
            // If we didn't handle it, let preferences handle it.
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {

        return false;
    }

}
