package com.github.jonathangawrych.deviceadminmanager.gui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.github.jonathangawrych.deviceadminmanager.R;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PasswordPoliciesGui extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_passwords);
	}
}
