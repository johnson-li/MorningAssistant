package com.johnson.morningAssistant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.iflytek.speech.SpeechUtility;
import com.johnson.Log;
import com.johnson.utils.ApkInstaller;
import com.johnson.utils.Preferences;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by johnson on 9/16/14.
 */
public class SettingFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

        findPreference(getString(R.string.useVoiceEngine)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (checkSpeechServiceInstall()) {
                    SpeechUtility.getUtility(getActivity()).setAppid(getString(R.string.appId));
                    return true;
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("You should install voice engine first");
                    builder.setTitle("warning");
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            String url = SpeechUtility.getUtility(getActivity()).getComponentUrl();
                            String assetsApk = "SpeechService.apk";
                            if (!processInstall(url, assetsApk)) {
                                Log.e("Voice engine initiation error");
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
                return false;
            }
        });
        findPreference(getString(R.string.advancedTime)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String minutes = (String) newValue;
                Pattern pattern = Pattern.compile("^\\d+$");
                Matcher matcher = pattern.matcher(minutes);
                if (matcher.matches()) {
                    findPreference(getString(R.string.advancedTime)).setSummary(minutes + " minutes before regular alarm");
                    return true;
                }
                Toast.makeText(getActivity(), "input should be only digits", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        findPreference(getString(R.string.advancedTime)).setSummary(Preferences.getAdvancedTime() + " minutes before regular alarm");
        findPreference(getString(R.string.timeFormat)).setSummary(Preferences.getTimeFormat() + "-hour mode");
        findPreference(getString(R.string.timeFormat)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                findPreference(getString(R.string.timeFormat)).setSummary(newValue + "-hour mode");
                return true;
            }
        });
        findPreference(getString(R.string.about)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Morning Assistant\n" +
                        "Version 1.0");
                builder.setTitle("About");
                builder.setPositiveButton("Confirm", null);
                builder.create().show();
                return false;
            }
        });
        findPreference(getString(R.string.contactUs)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Design by Johnson\n" +
                        "from Fudan University\n" +
                        "email: johnsonli1993@163.com");
                builder.setTitle("About");
                builder.setPositiveButton("Confirm", null);
                builder.create().show();
                return false;
            }
        });
        findPreference(getString(R.string.help)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), ScreenSlideActivity.class));
                return false;
            }
        });
    }

    boolean processInstall(String url, String assetsApk) {
        if (ApkInstaller.installFromAssets(getActivity(), assetsApk)) {
            Log.i("voice engine installation report success");
            return true;
        }
        else {
            Log.w("voice engine installation report fail");
            return false;
        }
    }

    boolean checkSpeechServiceInstall(){
        String packageName = "com.iflytek.speechcloud";
        List<PackageInfo> packages = getActivity().getPackageManager().getInstalledPackages(0);
        for(PackageInfo packageInfo: packages) {
            if (packageInfo.packageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
