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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.cna.ShortcutPickerHelper;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class LockscreenStyleSettings extends SettingsPreferenceFragment implements 
        ShortcutPickerHelper.OnPickListener, Preference.OnPreferenceChangeListener {

        private static final String TAG = "Lockscreens";
        private static final boolean DEBUG = true;
        protected Context mContext;

        private static final String PREF_LOCKSCREEN_TARGETS = "pref_lockscreen_targets";
        private static final String PREF_LOCKSCREEN_LAYOUT = "pref_lockscreen_layout";
        private static final String PREF_LOCKSCREEN_TEXT_COLOR = "lockscreen_text_color";
        private static final String PREF_SMS_PICKER = "sms_picker";
        private static final String PREF_SMS_PICKER_1 = "sms_picker_1";
        private static final String PREF_SMS_PICKER_2 = "sms_picker_2";
        private static final String PREF_SMS_PICKER_3 = "sms_picker_3";
        private static final String PREF_SMS_PICKER_4 = "sms_picker_4";

        public static final int REQUEST_PICK_WALLPAPER = 199;
        public static final int SELECT_ACTIVITY = 2;
        public static final int SELECT_WALLPAPER = 3;

        private static final String WALLPAPER_NAME = "lockscreen_wallpaper.jpg";

        ListPreference mLockscreenTargets;
        ListPreference mLockscreenLayout;
        ColorPickerPreference mLockscreenTextColor;
        Preference mLockscreenWallpaper;

        Preference mSmsPicker;
        Preference mSmsPicker1;
        Preference mSmsPicker2;
        Preference mSmsPicker3;
        Preference mSmsPicker4;

        private ShortcutPickerHelper mPicker;
        private Preference mCurrentCustomActivityPreference;
        private String mCurrentCustomActivityString;
        private String mSmsIntentUri;
        private String mSmsIntentUri1;
        private String mSmsIntentUri2;
        private String mSmsIntentUri3;
        private String mSmsIntentUri4;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            mContext = getActivity().getApplicationContext();
            super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.lockscreen_style_settings);

        mLockscreenLayout = (ListPreference) findPreference(PREF_LOCKSCREEN_LAYOUT);
        mLockscreenLayout.setOnPreferenceChangeListener(this);
        mLockscreenLayout.setValue(Settings.System.getInt(
                    getActivity().getContentResolver(), Settings.System.LOCKSCREEN_LAYOUT,
                    0) + "");

        mLockscreenTargets = (ListPreference) findPreference(PREF_LOCKSCREEN_TARGETS);
        mLockscreenTargets.setOnPreferenceChangeListener(this);
        mLockscreenTargets.setValue(Settings.System.getInt(
                    getActivity().getContentResolver(), Settings.System.LOCKSCREEN_TARGETS,
                    0) + "");

        mLockscreenTextColor = (ColorPickerPreference) findPreference(PREF_LOCKSCREEN_TEXT_COLOR);
        mLockscreenTextColor.setOnPreferenceChangeListener(this);

        mLockscreenWallpaper = findPreference("wallpaper");            

        mSmsPicker = findPreference(PREF_SMS_PICKER);

        mSmsPicker1 = findPreference(PREF_SMS_PICKER_1);

        mSmsPicker2 = findPreference(PREF_SMS_PICKER_2);

        mSmsPicker3 = findPreference(PREF_SMS_PICKER_3);

        mSmsPicker4 = findPreference(PREF_SMS_PICKER_4);

        mPicker = new ShortcutPickerHelper(this, this);

        mSmsIntentUri = Settings.System.getString(getActivity()
                .getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_SMS_INTENT);

        mSmsIntentUri1 = Settings.System.getString(getActivity()
                .getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_LOWER_LEFT_INTENT);

        mSmsIntentUri2 = Settings.System.getString(getActivity()
                .getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_UPPER_LEFT_INTENT);

        mSmsIntentUri3 = Settings.System.getString(getActivity()
                .getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_UPPER_RIGHT_INTENT);

        mSmsIntentUri4 = Settings.System.getString(getActivity()
                .getContentResolver(), Settings.System.LOCKSCREEN_CUSTOM_LOWER_RIGHT_INTENT);

        setHasOptionsMenu(true);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mSmsPicker) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_SMS_INTENT;
            mPicker.pickShortcut();
            return true;
                    
        } else if (preference == mSmsPicker1) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_LOWER_LEFT_INTENT;
            mPicker.pickShortcut();
            return true;

        } else if (preference == mSmsPicker2) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_UPPER_LEFT_INTENT;
            mPicker.pickShortcut();
            return true;

        } else if (preference == mSmsPicker3) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_UPPER_RIGHT_INTENT;
            mPicker.pickShortcut();
            return true;

        } else if (preference == mSmsPicker4) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_LOWER_RIGHT_INTENT;
            mPicker.pickShortcut();
            return true;

        } else if (preference == mLockscreenWallpaper) {

            int width = getActivity().getWallpaperDesiredMinimumWidth();
            int height = getActivity().getWallpaperDesiredMinimumHeight();
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            float spotlightX = (float) display.getWidth() / width;
            float spotlightY = (float) display.getHeight() / height;

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                                       null);
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", width);
            intent.putExtra("aspectY", height);
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);
            intent.putExtra("scale", true);
            // intent.putExtra("return-data", false);
            intent.putExtra("spotlightX", spotlightX);
            intent.putExtra("spotlightY", spotlightY);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getLockscreenExternalUri());
            intent.putExtra("outputFormat",
                            Bitmap.CompressFormat.JPEG.toString());

            startActivityForResult(intent, REQUEST_PICK_WALLPAPER);
            return true;

        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public void refreshSettings() {
        mSmsPicker.setSummary(mPicker.getFriendlyNameForUri(mSmsIntentUri));

        mSmsPicker1.setSummary(mPicker.getFriendlyNameForUri(mSmsIntentUri1));

        mSmsPicker2.setSummary(mPicker.getFriendlyNameForUri(mSmsIntentUri2));

        mSmsPicker3.setSummary(mPicker.getFriendlyNameForUri(mSmsIntentUri3));

        mSmsPicker4.setSummary(mPicker.getFriendlyNameForUri(mSmsIntentUri4));
    }

    public void shortcutPicked(String uri, String friendlyName, boolean isApplication) {
        if (Settings.System.putString(getContentResolver(), mCurrentCustomActivityString, uri)) {
            mCurrentCustomActivityPreference.setSummary(friendlyName);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean handled = false;
        if (preference == mLockscreenLayout) {
            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.LOCKSCREEN_LAYOUT, val);
            return true;

        } else if (preference == mLockscreenTargets) {
            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.LOCKSCREEN_TARGETS, val);
            return true;

        } else if (preference == mLockscreenTextColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_TEXT_COLOR, intHex);
            if (DEBUG) Log.d(TAG, String.format("new color hex value: %d", intHex));
            return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_WALLPAPER) {

                FileOutputStream wallpaperStream = null;
                try {
                    wallpaperStream = mContext.openFileOutput(WALLPAPER_NAME,
                                                                Context.MODE_PRIVATE);
                } catch (FileNotFoundException e) {
                    return; // NOOOOO
                }

                // should use intent.getData() here but it keeps returning null
                Uri selectedImageUri = getLockscreenExternalUri();
                Log.e(TAG, "Selected image uri: " + selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath());

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                                wallpaperStream);
                        
            } else if (requestCode == ShortcutPickerHelper.REQUEST_PICK_SHORTCUT
                    || requestCode == ShortcutPickerHelper.REQUEST_PICK_APPLICATION
                    || requestCode == ShortcutPickerHelper.REQUEST_CREATE_SHORTCUT) {
                mPicker.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.lockscreens, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.remove_wallpaper:
                File f = new File(mContext.getFilesDir(), WALLPAPER_NAME);
                Log.e(TAG, mContext.deleteFile(WALLPAPER_NAME) + "");
                Log.e(TAG, mContext.deleteFile(WALLPAPER_NAME) + "");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private Uri getLockscreenExternalUri() {
        File dir = mContext.getExternalCacheDir();
        File wallpaper = new File(dir, WALLPAPER_NAME);

        return Uri.fromFile(wallpaper);
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
