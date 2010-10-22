//  Copyright 2010 Jonathan Steele
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.itnoles.knightfootball;

import com.itnoles.shared.InstapaperRequest;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.*;
import android.view.Gravity;
import android.widget.TextView;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String NEWS = "news";
	private static final String INSTAPAPER_ENABLED = "instapaper_enabled";
	private static final String INSTAPAPER_USERNAME = "instapaper_username";
	private static final String INSTAPAPER_PASSWORD = "instapaper_password";

	private SharedPreferences settings;
	private ListPreference mNewsPref;
	private CheckBoxPreference mInstapaperEnabledPref;
	private EditTextPreference usernameEditPref;
	private EditTextPreference passwordEditPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settings = getSharedPreferences("KnightsSetting", MODE_PRIVATE);
		
		// Load the XML preferences file
		addPreferencesFromResource(R.xml.preferences);
		
		// Get a reference to the preferences
		mNewsPref = (ListPreference)getPreferenceScreen().findPreference(NEWS);
		mInstapaperEnabledPref = (CheckBoxPreference)getPreferenceScreen().findPreference(INSTAPAPER_ENABLED);
		usernameEditPref = (EditTextPreference)getPreferenceScreen().findPreference(INSTAPAPER_USERNAME);
		passwordEditPref = (EditTextPreference)getPreferenceScreen().findPreference(INSTAPAPER_PASSWORD);
	
		TextView mCopyright = new TextView(this);
		mCopyright.setText("Written by Jonathan Steele\nEmail: xfsunoles@gmail.com");
		mCopyright.setGravity(Gravity.CENTER);
		getListView().addFooterView(mCopyright);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		SharedPreferences.Editor editor = settings.edit();
		if (key.equals(NEWS))
		{
			int index = mNewsPref.findIndexOfValue(mNewsPref.getValue());
			if (index != -1)
			{
				editor.putString("newstitle", mNewsPref.getEntries()[index].toString());
				editor.putString("newsurl", mNewsPref.getEntryValues()[index].toString());
				// Don't forget to commit your edits!!!
				editor.commit();
			}
		}
		
		if (key.equals(INSTAPAPER_ENABLED))
			editor.putBoolean(INSTAPAPER_ENABLED, mInstapaperEnabledPref.isChecked()).commit();
			
		if (key.equals(INSTAPAPER_USERNAME))
			editor.putString(INSTAPAPER_USERNAME, usernameEditPref.getText()).commit();

		if (key.equals(INSTAPAPER_PASSWORD))
		{
			editor.putString(INSTAPAPER_PASSWORD, passwordEditPref.getText()).commit();
			// start the auth request for instapaper
			InstapaperRequest request = new InstapaperRequest(settings, this);
			request.getDataFromURLForcingBasicAuth();
		}
	}
}