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

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class PowerMenuSettings extends SettingsPreferenceFragment implements
    Preference.OnPreferenceChangeListener {

    private static final String PREF_POWER_MENU = "show_power_menu";
    private static final String PREF_REBOOT_MENU = "show_reboot_menu";
    private static final String PREF_PROFILES_MENU = "show_profiles_menu";
    private static final String PREF_SCREENSHOT = "show_screenshot";
    private static final String PREF_AIRPLANE_MODE = "show_airplane_mode";
    private static final String PREF_TORCH_TOGGLE = "show_torch_toggle";
    private static final String PREF_NAVBAR_TOGGLE = "show_navbar_toggle";
    private static final String PREF_SILENT_TOGGLE = "show_silent_toggle";
    private static final String PREF_NAVACTIONS_LAYOUT = "pref_navactions_layout";

    CheckBoxPreference mShowPowerMenu;
    CheckBoxPreference mShowRebootMenu;
    CheckBoxPreference mShowProfilesMenu;
    CheckBoxPreference mShowScreenShot;
    CheckBoxPreference mShowTorchToggle;
    CheckBoxPreference mShowAirplaneMode;
    CheckBoxPreference mShowNavBarToggle;
    CheckBoxPreference mShowSilentToggle;
    ListPreference mNavActionsLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.powermenu_settings);

        PreferenceScreen prefs = getPreferenceScreen();

        mShowPowerMenu = (CheckBoxPreference) findPreference(PREF_POWER_MENU);
        mShowPowerMenu.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.POWER_DIALOG_SHOW_POWER_MENU, 1) == 1);

        mShowRebootMenu = (CheckBoxPreference) findPreference(PREF_REBOOT_MENU);
        mShowRebootMenu.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.POWER_DIALOG_SHOW_REBOOT_MENU, 0) == 1);
        
        mShowProfilesMenu = (CheckBoxPreference) findPreference(PREF_PROFILES_MENU);
        mShowProfilesMenu.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.POWER_DIALOG_SHOW_PROFILES_MENU, 0) == 1);

        mShowScreenShot = (CheckBoxPreference) findPreference(PREF_SCREENSHOT);
        mShowScreenShot.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.POWER_DIALOG_SHOW_SCREENSHOT, 0) == 1);

        mShowAirplaneMode = (CheckBoxPreference) findPreference(PREF_AIRPLANE_MODE);
        mShowAirplaneMode.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.POWER_DIALOG_SHOW_AIRPLANE_MODE, 1) == 1);

        mShowTorchToggle = (CheckBoxPreference) findPreference(PREF_TORCH_TOGGLE);
        mShowTorchToggle.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.POWER_DIALOG_SHOW_TORCH_TOGGLE, 0) == 1);

        mShowNavBarToggle = (CheckBoxPreference) findPreference(PREF_NAVBAR_TOGGLE);
        mShowNavBarToggle.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.POWER_DIALOG_NAVBAR_TOGGLE, 0) == 1);

        mShowSilentToggle = (CheckBoxPreference) findPreference(PREF_SILENT_TOGGLE);
        mShowSilentToggle.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.POWER_DIALOG_SHOW_SILENT_TOGGLE, 1) == 1);

        mNavActionsLayout = (ListPreference) findPreference(PREF_NAVACTIONS_LAYOUT);
        mNavActionsLayout.setOnPreferenceChangeListener(this);
        mNavActionsLayout.setValue(Settings.System.getInt(
                        getActivity().getContentResolver(), Settings.System.NAVACTIONS_LAYOUT,
                        0) + "");

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mShowPowerMenu) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_DIALOG_SHOW_POWER_MENU,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mShowRebootMenu) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_DIALOG_SHOW_REBOOT_MENU,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mShowProfilesMenu) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_DIALOG_SHOW_PROFILES_MENU,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mShowScreenShot) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_DIALOG_SHOW_SCREENSHOT,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mShowAirplaneMode) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_DIALOG_SHOW_AIRPLANE_MODE,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mShowTorchToggle) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_DIALOG_SHOW_TORCH_TOGGLE,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mShowNavBarToggle) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_DIALOG_NAVBAR_TOGGLE,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mShowSilentToggle) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_DIALOG_SHOW_SILENT_TOGGLE,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNavActionsLayout) {
            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                                   Settings.System.NAVACTIONS_LAYOUT, val);
            return true;
        }
        return false;
    }

}
