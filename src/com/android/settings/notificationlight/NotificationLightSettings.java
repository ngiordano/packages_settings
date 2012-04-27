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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NotificationLightSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, View.OnLongClickListener {
    private static final String TAG = "NotificationLightSettings";
    private static final String NOTIFICATION_LIGHT_PULSE_DEFAULT_COLOR = "notification_light_pulse_default_color";
    private static final String NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_ON = "notification_light_pulse_default_led_on";
    private static final String NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_OFF = "notification_light_pulse_default_led_off";
    private static final String NOTIFICATION_LIGHT_PULSE_CUSTOM_ENABLE = "notification_light_pulse_custom_enable";
    private static final String NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES = "notification_light_pulse_custom_values";
    private static final String NOTIFICATION_LIGHT_PULSE = "notification_light_pulse";
    private static final String PULSE_PREF = "pulse_enabled";
    private static final String DEFAULT_PREF = "default";
    private static final String CUSTOM_PREF = "custom_enabled";
    public static final int DEFAULT_COLOR = 0xFFFFFF; //White
    public static final int DEFAULT_TIME = 1000;
    private static final int MENU_ADD = 0;
    private static final int DIALOG_APPS = 0;
    private static final int DIALOG_MENU = 1;
    private List<PackageInfo> mInstalledPackages;
    private PackageManager mPackageManager;
    private String mCustomValues;
    private boolean mCustomEnabled;
    private boolean mLightEnabled;
    private List<Application> mApplications;
    private ApplicationLightPreference mDefaultPref;
    private CheckBoxPreference mCustomEnabledPref;
    private Menu mMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notification_light_settings);

        mPackageManager = getPackageManager();
        mInstalledPackages = mPackageManager.getInstalledPackages(0);
        mApplications = new ArrayList<Application>();

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDefault();
        refreshCustomApplications();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void refreshDefault() {
        ContentResolver resolver = getContentResolver();
        int color = Settings.System.getInt(resolver, NOTIFICATION_LIGHT_PULSE_DEFAULT_COLOR, DEFAULT_COLOR);
        int timeOn = Settings.System.getInt(resolver, NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_ON, DEFAULT_TIME);
        int timeOff = Settings.System.getInt(resolver, NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_OFF, DEFAULT_TIME);
        mLightEnabled = Settings.System.getInt(resolver, NOTIFICATION_LIGHT_PULSE, 0) == 1;
        mCustomEnabled = Settings.System.getInt(resolver, NOTIFICATION_LIGHT_PULSE_CUSTOM_ENABLE, 0) == 1;

        // Add the General section
        Context context = getActivity();
        PreferenceScreen prefSet = getPreferenceScreen();
        PreferenceGroup generalPrefs = (PreferenceGroup) prefSet.findPreference("general_section");
        if (generalPrefs != null) {
            generalPrefs.removeAll();

            // Add the checkbox
            CheckBoxPreference cPref = new CheckBoxPreference(context);
            cPref.setTitle(R.string.notification_pulse_title);
            cPref.setKey(PULSE_PREF);
            cPref.setChecked(mLightEnabled);
            cPref.setOnPreferenceChangeListener(this);
            generalPrefs.addPreference(cPref);

            mDefaultPref = new ApplicationLightPreference(context, color, timeOn, timeOff);
            mDefaultPref.setKey(DEFAULT_PREF);
            mDefaultPref.setTitle(R.string.notification_light_default_value);
            mDefaultPref.setEnabled(mLightEnabled);
            mDefaultPref.setPersistent(false);
            mDefaultPref.setOnPreferenceChangeListener(this);
            generalPrefs.addPreference(mDefaultPref);

            // Add the checkbox
            mCustomEnabledPref = new CheckBoxPreference(context);
            mCustomEnabledPref.setTitle(R.string.notification_light_use_custom);
            mCustomEnabledPref.setKey(CUSTOM_PREF);
            mCustomEnabledPref.setChecked(mCustomEnabled);
            mCustomEnabledPref.setEnabled(mLightEnabled);
            mCustomEnabledPref.setOnPreferenceChangeListener(this);
            generalPrefs.addPreference(mCustomEnabledPref);
        }
    }

    private void refreshCustomApplications() {
        mApplications.clear();

        mCustomValues = Settings.System.getString(getContentResolver(), NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES);
        if (mCustomValues == null) {
            mCustomValues = "";
        }

        String[] customs = mCustomValues.split("\\|", -1);

        // Parse the Applications list
        for (String custom : customs) {
            String[] app = custom.split("=", -1);
            if (app.length != 2)
                continue;

            String[] values = app[1].split(";", -1);
            if (values.length != 3)
                continue;

            try {
                mApplications.add(new Application(app[0], Integer.parseInt(values[0]), Integer
                        .parseInt(values[1]), Integer.parseInt(values[2])));
            } catch (NumberFormatException e) {
                // Do nothing
            }
        }

        // Add the Application Preferences
        Context context = getActivity();
        PreferenceScreen prefSet = getPreferenceScreen();
        PackageManager pManager = getPackageManager();
        PreferenceGroup appList = (PreferenceGroup) prefSet.findPreference("applications_list");
        if (appList != null) {
            appList.removeAll();
            if (!mApplications.isEmpty()) {
                for (Application i : mApplications) {
                    try {
                        PackageInfo info = pManager.getPackageInfo(i.name, PackageManager.GET_META_DATA);

                        ApplicationLightPreference pref  = new ApplicationLightPreference(context, this, i.color, i.timeon, i.timeoff);
                        pref.setKey(i.name);
                        pref.setTitle((String) info.applicationInfo.loadLabel(pManager));
                        pref.setIcon((Drawable) info.applicationInfo.loadIcon(pManager));
                        pref.setSummary(i.name); // Does not fit on low res devices, we need it so we hide the view in the preference
                        pref.setPersistent(false);
                        pref.setEnabled(mCustomEnabled && mLightEnabled);
                        pref.setOnPreferenceChangeListener(this);
                        appList.addPreference(pref);
                    } catch (NameNotFoundException e) {
                        // Do nothing
                    }
                }
            }
        }

    }

    private void setCustomEnabled() {
        PreferenceScreen prefSet = getPreferenceScreen();
        PreferenceGroup appList = (PreferenceGroup) prefSet.findPreference("applications_list");
        if (appList != null) {

            // If we have application preferences, loop through them and set the enabled state
            int count = appList.getPreferenceCount();
            if (count > 0) {
                for (int i = 0; i < appList.getPreferenceCount(); i++) {
                    ApplicationLightPreference pref = (ApplicationLightPreference) appList.getPreference(i);
                    pref.setEnabled(mCustomEnabled && mLightEnabled);
                }
            }
        }
    }

    private void addCustomApplication(String packageName) {
        mCustomValues = mCustomValues.trim();

        if (mCustomValues.length() != 0 && !mCustomValues.endsWith("|")) {
            mCustomValues += "|";
        }

        // Add the application with default configuration and store
        mCustomValues += packageName + "=" + DEFAULT_COLOR +";" + DEFAULT_TIME +";" + DEFAULT_TIME;
        Settings.System.putString(getContentResolver(), NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES,
                mCustomValues);

        refreshCustomApplications();
    }

    private void removeCustomApplication(String packageName) {
        StringBuilder newValues = new StringBuilder();

        for (String custom : mCustomValues.split("\\|", -1)) {
            String[] app = custom.split("=", -1);
            if ((app.length != 2) || (app[0].equals(packageName))) {
                continue;
            }
            if (newValues.length() != 0) {
                newValues.append("|");
            }
            newValues.append(custom);
        }

        Settings.System.putString(getContentResolver(), NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES,
                newValues.toString());
        refreshCustomApplications();
    }

    /**
     * Updates the default or application specific notification settings.
     *
     * @param application Package name of application specific settings to
     *            update, if "null" update the default settings.
     * @param color
     * @param timeon
     * @param timeoff
     */
    protected void updateValues(String application, Integer color, Integer timeon, Integer timeoff) {
        ContentResolver resolver = getContentResolver();

        if (application.equals(DEFAULT_PREF)) {
            Settings.System.putInt(resolver, NOTIFICATION_LIGHT_PULSE_DEFAULT_COLOR, color);
            Settings.System.putInt(resolver, NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_ON, timeon);
            Settings.System.putInt(resolver, NOTIFICATION_LIGHT_PULSE_DEFAULT_LED_OFF, timeoff);
            refreshDefault();
            return;
        }

        for (Application app : mApplications) {
            if (!app.name.equals(application)) {
                continue;
            }
            app.color = color;
            app.timeon = timeon;
            app.timeoff = timeoff;
        }

        StringBuilder builder = new StringBuilder();

        for (Application a : mApplications) {
            if (builder.length() != 0) {
                builder.append("|");
            }
            builder.append(a.name).append("=").append(a.color).append(";").append(a.timeon)
                    .append(";").append(a.timeoff);
        }

        Settings.System.putString(resolver, NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES, builder.toString());
        refreshCustomApplications();
    }

    public boolean onLongClick(View v) {
        final TextView tView;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if ((v != null) && ((tView = (TextView) v.findViewById(android.R.id.summary)) != null)) {
            builder.setMessage(R.string.profile_app_delete_confirm);
            builder.setTitle(R.string.profile_menu_delete);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton(android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeCustomApplication(tView.getText().toString());
                        }
                    });
            builder.setNegativeButton(android.R.string.no,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.create().show();
            return true;
        }

        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        String key = preference.getKey();

        if (PULSE_PREF.equals(key)) {
            mLightEnabled = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(), Settings.System.NOTIFICATION_LIGHT_PULSE,
                    mLightEnabled ? 1 : 0);
            mDefaultPref.setEnabled(mLightEnabled);
            mCustomEnabledPref.setEnabled(mLightEnabled);
            setCustomEnabled();
        } else if (CUSTOM_PREF.equals(key)) {
            mCustomEnabled = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(), NOTIFICATION_LIGHT_PULSE_CUSTOM_ENABLE,
                    mCustomEnabled ? 1 : 0);
            setCustomEnabled();
        } else {
            ApplicationLightPreference tPref = (ApplicationLightPreference) preference;
            updateValues(key, tPref.getColor(), tPref.getOnValue(), tPref.getOffValue());
        }

        return true;
    }

    /**
     * Utility classes and supporting methods
     */
    class Application {
        public String name;
        public Integer color;
        public Integer timeon;
        public Integer timeoff;

        public Application(String name, Integer color, Integer timeon, Integer timeoff) {
            this.name = name;
            this.color = color;
            this.timeon = timeon;
            this.timeoff = timeoff;
        }
    }

    class PackageItem implements Comparable<PackageItem> {
        CharSequence title;

        String packageName;

        Drawable icon;

        @Override
        public int compareTo(PackageItem another) {
            return this.title.toString().compareTo(another.title.toString());
        }
    }

    class PackageAdapter extends BaseAdapter {
        protected List<PackageInfo> mInstalledPackageInfo;
        protected List<PackageItem> mInstalledPackages = new LinkedList<PackageItem>();

        private void reloadList() {
            final Handler handler = new Handler();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    synchronized (mInstalledPackages) {
                        mInstalledPackages.clear();
                        for (PackageInfo info : mInstalledPackageInfo) {
                            final PackageItem item = new PackageItem();
                            ApplicationInfo applicationInfo = info.applicationInfo;
                            item.title = applicationInfo.loadLabel(mPackageManager);
                            item.icon = applicationInfo.loadIcon(mPackageManager);
                            item.packageName = applicationInfo.packageName;
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    int index = Collections.binarySearch(mInstalledPackages, item);
                                    if (index < 0) {
                                        index = -index - 1;
                                        mInstalledPackages.add(index, item);
                                    }
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            }).start();
        }

        public PackageAdapter(List<PackageInfo> installedPackagesInfo) {
            mInstalledPackageInfo = installedPackagesInfo;
        }

        public void update() {
            reloadList();
        }

        @Override
        public int getCount() {
            return mInstalledPackages.size();
        }

        @Override
        public PackageItem getItem(int position) {
            return mInstalledPackages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mInstalledPackages.get(position).packageName.hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.preference_icon, null, false);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.title = (TextView) convertView.findViewById(com.android.internal.R.id.title);
                holder.summary = (TextView) convertView
                        .findViewById(com.android.internal.R.id.summary);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            }
            PackageItem applicationInfo = getItem(position);

            if (holder.title != null) {
                holder.title.setText(applicationInfo.title);
            }
            if (holder.summary != null) {
                holder.summary.setVisibility(View.GONE);
            }
            if (holder.icon != null) {
                Drawable loadIcon = applicationInfo.icon;
                holder.icon.setImageDrawable(loadIcon);
                holder.icon.setAdjustViewBounds(true);
                holder.icon.setMaxHeight(72);
                holder.icon.setMaxWidth(72);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        TextView title;
        TextView summary;
        ImageView icon;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        mMenu.add(0, MENU_ADD, 0, R.string.profiles_add)
                .setIcon(R.drawable.ic_menu_add)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ADD:
                if (mCustomEnabled) {
                    showDialog(DIALOG_APPS);
                } else {
                    showDialog(DIALOG_MENU);
                }
                return true;
        }
        return false;
    }

    @Override
    public Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Dialog dialog;
        switch (id) {
            case DIALOG_APPS:
                final ListView list = new ListView(getActivity());
                PackageAdapter adapter = new PackageAdapter(mInstalledPackages);
                list.setAdapter(adapter);
                adapter.update();

                builder.setTitle(R.string.profile_choose_app);
                builder.setView(list);
                dialog = builder.create();

                list.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Add empty application definition, the user will be able to edit it later
                        PackageItem info = (PackageItem) parent.getItemAtPosition(position);
                        addCustomApplication(info.packageName);
                        dialog.cancel();
                    }
                });
                break;
            case DIALOG_MENU:
                builder.setMessage(R.string.dialog_menu_message);
                builder.setTitle(R.string.dialog_menu_title);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                dialog = builder.create();
                break;
            default:
                dialog = null;
        }
        return dialog;
    }
}
