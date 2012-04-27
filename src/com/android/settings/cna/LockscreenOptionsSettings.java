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

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class LockscreenOptionsSettings extends SettingsPreferenceFragment {

        protected Context mContext;

        private static final String PREF_MENU = "pref_lockscreen_menu_unlock";
        private static final String PREF_QUICK_UNLOCK = "pref_lockscreen_quick_unlock";
        private static final String PREF_USER_OVERRIDE = "lockscreen_user_timeout_override";
        private static final String PREF_LOCKSCREEN_BATTERY = "lockscreen_battery";
        private static final String PREF_LOCKSCREEN_BEFORE_UNLOCK = "lockscreen_before_unlock";
        private static final String DISABLE_CAMERA = "pref_lockscreen_disable_camera";
        private static final String PREF_VOLUME_MUSIC = "volume_music_controls";
        private static final String PREF_VOLUME_WAKE = "volume_wake";
        private static final String PREF_LOCKSCREEN_ROTATION = "enable_lockscreen_rotation";

        CheckBoxPreference menuButtonLocation;
        CheckBoxPreference mQuickUnlock;
        CheckBoxPreference mLockScreenTimeoutUserOverride;
        CheckBoxPreference mLockscreenBattery;
        CheckBoxPreference mDisableCamera;
        CheckBoxPreference mVolumeMusic;
        CheckBoxPreference mVolumeWake;
        CheckBoxPreference mLockRotation;
        CheckBoxPreference mLockBeforeLock;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            mContext = getActivity().getApplicationContext();
            super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.lockscreen_options_settings);

        menuButtonLocation = (CheckBoxPreference) findPreference(PREF_MENU);
        menuButtonLocation.setChecked(Settings.System.getInt(getActivity()
                    .getContentResolver(), Settings.System.MENU_UNLOCK_SCREEN,
                    1) == 1);

        mQuickUnlock = (CheckBoxPreference) findPreference(PREF_QUICK_UNLOCK);
        mQuickUnlock.setChecked(Settings.System.getInt(getActivity()
                    .getContentResolver(), Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL,
                    0) == 1);

        mLockScreenTimeoutUserOverride = (CheckBoxPreference) findPreference(PREF_USER_OVERRIDE);
        mLockScreenTimeoutUserOverride.setChecked(Settings.Secure.getInt(getActivity()
                    .getContentResolver(), Settings.Secure.LOCK_SCREEN_LOCK_USER_OVERRIDE,
                    0) == 1);

        mLockscreenBattery = (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_BATTERY);
        mLockscreenBattery.setChecked(Settings.System.getInt(
                    getActivity().getContentResolver(), Settings.System.LOCKSCREEN_BATTERY,
                    0) == 1);

        mDisableCamera = (CheckBoxPreference) findPreference(DISABLE_CAMERA);
        mDisableCamera.setChecked(Settings.System.getInt(getActivity()
                    .getContentResolver(), Settings.System.LOCKSCREEN_DISABLE_CAMERA,
                    0) == 1);

        mVolumeMusic = (CheckBoxPreference) findPreference(PREF_VOLUME_MUSIC);
        mVolumeMusic.setChecked(Settings.System.getInt(getActivity()
                    .getContentResolver(), Settings.System.VOLBTN_MUSIC_CONTROLS,
                    0) == 1);

        mVolumeWake = (CheckBoxPreference) findPreference(PREF_VOLUME_WAKE);
        mVolumeWake.setChecked(Settings.System.getInt(getActivity()
                    .getContentResolver(), Settings.System.VOLUME_WAKE_SCREEN,
                    0) == 1);

        mLockRotation = (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_ROTATION);
        mLockRotation.setChecked(Settings.System.getInt(getActivity()
                    .getContentResolver(), Settings.System.LOCKSCREEN_LANDSCAPE,
                    0) == 1);

        mLockBeforeLock = (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_BEFORE_UNLOCK);
        mLockBeforeLock.setChecked(Settings.System.getInt(getActivity()
                    .getContentResolver(), Settings.System.LOCKSCREEN_BEFORE_UNLOCK,
                    0) == 1);

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == menuButtonLocation) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.MENU_UNLOCK_SCREEN,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mQuickUnlock) {

            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mLockScreenTimeoutUserOverride) {
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.LOCK_SCREEN_LOCK_USER_OVERRIDE,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mVolumeWake) {

            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.VOLUME_WAKE_SCREEN,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mLockRotation) {

            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_LANDSCAPE,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mLockscreenBattery) {

            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_BATTERY,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mVolumeMusic) {

            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.VOLBTN_MUSIC_CONTROLS,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mDisableCamera) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_DISABLE_CAMERA,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mLockBeforeLock) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_BEFORE_UNLOCK,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
