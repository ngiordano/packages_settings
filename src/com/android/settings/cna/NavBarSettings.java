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

import java.util.ArrayList;
import java.util.Locale;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.app.Fragment;
import android.app.ListFragment;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.cna.SeekBarPreference;
import com.android.settings.cna.TouchInterceptor;

public class NavBarSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

        private static final String DEFAULT_LAYOUT = "back|home|recent|search0";
        private static final String PREF_NAVBAR_MENU_DISPLAY = "navbar_menu_display";
        private static final String PREF_NAV_COLOR = "nav_button_color";
        private static final String MENU_DISPLAY_LOCATION = "pref_menu_display";
        private static final String NAVBUTTON_CONFIG = "navbutton_config";
        private static final String NAVBUTTON_HEIGHT = "navbutton_hight";
        private static final String NAVBUTTON_WIDTH = "navbutton_width";

        PreferenceScreen mButtonOrder;
        ColorPickerPreference mNavigationBarColor;
        ListPreference menuDisplayLocation;
        ListPreference mNavBarMenuDisplay;
        ListPreference mGlowTimes;
        ListPreference mNavButtonConfig;
        ListPreference mNavigationBarHeight;
        ListPreference mNavigationBarWidth;
        SeekBarPreference mButtonAlpha;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if(getPreferenceManager() != null) {
                addPreferencesFromResource(R.xml.navbar_settings);
                PreferenceScreen prefSet = getPreferenceScreen();
                mButtonOrder = (PreferenceScreen) prefSet.findPreference("button_order");
                String storedNav = Settings.System.getString(getActivity().getContentResolver(),
                                                             Settings.System.NAV_BUTTONS);
                if (storedNav == null) {
                    storedNav = DEFAULT_LAYOUT;
                }
            
                mNavigationBarColor = (ColorPickerPreference) findPreference(PREF_NAV_COLOR);
                mNavigationBarColor.setOnPreferenceChangeListener(this);

                mGlowTimes = (ListPreference) findPreference("glow_times");
                mGlowTimes.setOnPreferenceChangeListener(this);

                menuDisplayLocation = (ListPreference) findPreference(MENU_DISPLAY_LOCATION);
                menuDisplayLocation.setOnPreferenceChangeListener(this);
                menuDisplayLocation.setValue(Settings.System.getInt(getActivity()
                        .getContentResolver(), Settings.System.MENU_LOCATION, 0) + "");

                mNavBarMenuDisplay = (ListPreference) findPreference(PREF_NAVBAR_MENU_DISPLAY);
                mNavBarMenuDisplay.setOnPreferenceChangeListener(this);
                mNavBarMenuDisplay.setValue(Settings.System.getInt(getActivity()
                        .getContentResolver(), Settings.System.MENU_VISIBILITY, 0) + "");

                mNavButtonConfig = (ListPreference) findPreference(NAVBUTTON_CONFIG);
                mNavButtonConfig.setOnPreferenceChangeListener(this);
                mNavButtonConfig.setValue(Settings.System.getInt(getActivity()
                        .getContentResolver(), Settings.System.NAV_BUTTON_CONFIG, 0) + "");

                mNavigationBarHeight = (ListPreference) findPreference("navigation_bar_height");
                mNavigationBarHeight.setOnPreferenceChangeListener(this);

                mNavigationBarWidth = (ListPreference) findPreference("navigation_bar_width");
                mNavigationBarWidth.setOnPreferenceChangeListener(this);

                float defaultAlpha = Settings.System.getFloat(getActivity()
                            .getContentResolver(), Settings.System.NAVIGATION_BAR_BUTTON_ALPHA, 0.6f);
                mButtonAlpha = (SeekBarPreference) findPreference("button_transparency");
                mButtonAlpha.setInitValue((int) (defaultAlpha * 100));
                mButtonAlpha.setOnPreferenceChangeListener(this);
                
                setHasOptionsMenu(true);

            }
        }

    
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.nav_bar, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            switch (item.getItemId()) {
                case R.id.reset:
                    Settings.System.putInt(getActivity().getContentResolver(),
                            Settings.System.NAVIGATION_BAR_TINT, Integer.MIN_VALUE);
                    Settings.System.putFloat(getActivity().getContentResolver(),
                            Settings.System.NAVIGATION_BAR_BUTTON_ALPHA, 0.6f);
                    mButtonAlpha.setValue(60);
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference == mNavigationBarColor) {
                String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                .valueOf(newValue)));
                preference.setSummary(hex);

                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.NAVIGATION_BAR_TINT, intHex);

            } else if (preference == menuDisplayLocation) {
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.MENU_LOCATION, Integer.parseInt((String) newValue));
                return true;

            } else if (preference == mNavBarMenuDisplay) {
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.MENU_VISIBILITY, Integer.parseInt((String) newValue));
                return true;

            } else if (preference == mNavButtonConfig) {
                int val = Integer.parseInt((String) newValue);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.NAV_BUTTON_CONFIG, val);
                return true;

            } else if (preference == mNavigationBarWidth) {
                String newVal = (String) newValue;
                int dp = Integer.parseInt(newVal);
                int width = mapChosenDpToPixels(dp);
                Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_WIDTH,
                                       width);
                toggleBar();
                return true;

            } else if (preference == mNavigationBarHeight) {
                String newVal = (String) newValue;
                int dp = Integer.parseInt(newVal);
                int height = mapChosenDpToPixels(dp);
                Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_HEIGHT,
                                       height);
                toggleBar();
                return true;

            } else if (preference == mGlowTimes) {
                // format is (on|off) both in MS	
                int breakIndex = ((String) newValue).indexOf("|");
                String value = (String) newValue;

                int offTime = Integer.parseInt(value.substring(breakIndex + 1));
                int onTime = Integer.parseInt(value.substring(0, breakIndex));

                Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.NAVIGATION_BAR_GLOW_DURATION[0],
                                    offTime);
                Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.NAVIGATION_BAR_GLOW_DURATION[1],
                                    onTime);
                return true;
                
            } else if (preference == mButtonAlpha) {
                float val = Float.parseFloat((String) newValue);
                Log.e("R", "value: " + val / 100);
                Settings.System.putFloat(getActivity().getContentResolver(),
                Settings.System.NAVIGATION_BAR_BUTTON_ALPHA, val / 100);
                return true;
            }

            return false;
        }

        public void toggleBar() {
            boolean isBarOn = Settings.System.getInt(getContentResolver(),
                                                     Settings.System.NAVIGATION_BAR_HIDE, 0) == 1;
            Handler h = new Handler();
            Settings.System.putInt(getContentResolver(),
                                   Settings.System.NAVIGATION_BAR_HIDE, isBarOn ? 0 : 1);
            Settings.System.putInt(getContentResolver(),
                                   Settings.System.NAVIGATION_BAR_HIDE, isBarOn ? 1 : 0);
        }

        public int mapChosenDpToPixels(int dp) {
            switch (dp) {
                case 48:
                    return getResources().getDimensionPixelSize(R.dimen.navigation_bar_48);
                case 42:
                    return getResources().getDimensionPixelSize(R.dimen.navigation_bar_42);
                case 36:
                    return getResources().getDimensionPixelSize(R.dimen.navigation_bar_36);
                case 30:
                    return getResources().getDimensionPixelSize(R.dimen.navigation_bar_30);
                case 24:
                    return getResources().getDimensionPixelSize(R.dimen.navigation_bar_24);
            }
            return -1;
        }

        public static class NavButtonOrder extends ListFragment {

            private ListView mButtonList;
            private ButtonAdapter mButtonAdapter;
            View mContentView = null;
            Context mContext;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                mContentView = inflater.inflate(R.layout.order_power_widget_buttons_activity, null);
                return mContentView;
            }

            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                mContext = getActivity().getApplicationContext();

                mButtonList = getListView();
                ((TouchInterceptor) mButtonList).setDropListener(mDropListener);
                mButtonAdapter = new ButtonAdapter(mContext);
                setListAdapter(mButtonAdapter);
            }

            @Override
            public void onDestroy() {
                ((TouchInterceptor) mButtonList).setDropListener(null);
                setListAdapter(null);
                super.onDestroy();
            }

            @Override
            public void onResume() {
                super.onResume();
                mButtonAdapter.reloadButtons();
                mButtonList.invalidateViews();
            }

            private TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {
            public void drop(int from, int to) {
            String stored = Settings.System.getString(mContext.getContentResolver(), Settings.System.NAV_BUTTONS);
            if (stored==null) {
                stored = DEFAULT_LAYOUT;
            }
            ArrayList<String> buttons = new ArrayList<String>();
            for(String button : stored.split("\\|")) {
                buttons.add(button);
            }
            if(from < buttons.size()) {
                String button = buttons.remove(from);
                if(to <= buttons.size()) {
                    buttons.add(to, button);
                    String toStore = buttons.get(0);
                    for(int i = 1; i < buttons.size(); i++) {
                        toStore += "|" + buttons.get(i);
                    }
                    Settings.System.putString(mContext.getContentResolver(), Settings.System.NAV_BUTTONS, toStore);
                    mButtonAdapter.reloadButtons();
                    mButtonList.invalidateViews();
                }
            }
        }
    };

    public static class ButtonAdapter extends BaseAdapter {
        private Context mContext;
        private Resources mSystemUIResources = null;
        private LayoutInflater mInflater;
        private ArrayList<String> mButtons = new ArrayList<String>();

        public ButtonAdapter (Context c) {
            mContext = c;
            mInflater = LayoutInflater.from(mContext);
            PackageManager pm = mContext.getPackageManager();
            if(pm != null) {
                try {
                    mSystemUIResources = pm.getResourcesForApplication("com.android.systemui");
                } catch(Exception e) {
                    mSystemUIResources = null;
                }
            }
            reloadButtons();
        }

        public void reloadButtons() {
            String stored = Settings.System.getString(mContext.getContentResolver(), Settings.System.NAV_BUTTONS);
            if (stored==null) {
                stored = DEFAULT_LAYOUT;
            }
            mButtons.clear();
            for(String button : stored.split("\\|")) {
                mButtons.add(button);
            }
        }

        public int getCount() {
            return mButtons.size();
        }

        public Object getItem(int position) {
            return mButtons.get(position);
        }

        public long getItemId(int position) {
            return position;
        }
    
        public View getView(int position, View convertView, ViewGroup parent) {
            final View v;
            if(convertView == null) {
                v = mInflater.inflate(R.layout.order_power_widget_button_list_item, null);
            } else {
                v = convertView;
            }
            final TextView name = (TextView)v.findViewById(R.id.name);
            final ImageView icon = (ImageView)v.findViewById(R.id.icon);
            String curText = mButtons.get(position);
            if (curText.equals("search0")) {
                curText = "search";
            } else if (curText.equals("search1")){
                curText = "search";
            }
            name.setText(curText);
            String resName = null;
            if (mButtons.get(position).equals("home")) {
                resName = "com.android.systemui:drawable/ic_sysbar_home";
            }else if (mButtons.get(position).equals("recent")) {
                resName = "com.android.systemui:drawable/ic_sysbar_recent";
            }else if (mButtons.get(position).equals("back")) {
                resName = "com.android.systemui:drawable/ic_sysbar_back";
            }else {
                resName = "com.android.systemui:drawable/ic_sysbar_search";
            }
            icon.setVisibility(View.GONE);
            if(mSystemUIResources != null) {
                int resId = mSystemUIResources.getIdentifier(resName, null, null);
                if(resId > 0) {
                    try {
                            Drawable d = mSystemUIResources.getDrawable(resId);
                            icon.setVisibility(View.VISIBLE);
                            icon.setImageDrawable(d);
                        } catch(Exception e) {
                        }
                    }
                }
                return v;
            }
        }
    }
}
