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
package com.itnoles.shared.activity;

import com.itnoles.shared.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.*; // ListPreference, CheckBoxPreference, EditTextPreference, PreferenceActivity, Preference
import android.view.Gravity;
import android.widget.TextView;
import android.util.Log;

import com.itnoles.shared.InstapaperRequest;
import com.itnoles.shared.helper.SimpleCrypto;

public abstract class AbstractSettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	private static final String TAG = "AbstractSettingsActivity";
	private static final String NEWS = "news";
	private static final String TWITTER_ENABLED = "twitter_enabled";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the XML preferences file
		addPreferencesFromResource(R.xml.preferences);
		
		// Display copyright message
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
		onResumeWorker();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		Preference pref = findPreference(key);
		SharedPreferences sharedPref = getSharedPreferences("settings", MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		if (key.equals(NEWS))
		{
			ListPreference newsPref = (ListPreference)pref;
			int index = newsPref.findIndexOfValue(newsPref.getValue());
			if (index != -1)
			{
				editor.putString("newstitle", newsPref.getEntries()[index].toString());
				editor.putString("newsurl", newsPref.getEntryValues()[index].toString());
				// Don't forget to commit your edits!!!
				editor.commit();
			}
		}
		
		if (key.equals(TWITTER_ENABLED))
		{
			CheckBoxPreference twitterEnabledPref = (CheckBoxPreference)pref;
			editor.putBoolean(TWITTER_ENABLED, twitterEnabledPref.isChecked()).commit();
			startTwitterRequestOnBrowser();
		}
		
		if (key.equals(InstapaperRequest.INSTAPAPER_ENABLED))
		{
			CheckBoxPreference instapaperEnabledPref = (CheckBoxPreference)pref;
			editor.putBoolean(InstapaperRequest.INSTAPAPER_ENABLED, instapaperEnabledPref.isChecked()).commit();
		}
			
		if (key.equals(InstapaperRequest.INSTAPAPER_USERNAME))
		{
			try {
				EditTextPreference usernameEditPref = (EditTextPreference)pref;
				String encryptedString = SimpleCrypto.encrypt(InstapaperRequest.INSTAPAPER_USERNAME, usernameEditPref.getText());
				editor.putString(InstapaperRequest.INSTAPAPER_USERNAME, encryptedString).commit();
			} catch(Exception e) {
				Log.e(TAG, "bad encrypted string for instapaper username", e);
			}
		}

		if (key.equals(InstapaperRequest.INSTAPAPER_PASSWORD))
		{
			try {
				EditTextPreference passwordEditPref = (EditTextPreference)pref;
				String encryptedString = SimpleCrypto.encrypt(InstapaperRequest.INSTAPAPER_PASSWORD, passwordEditPref.getText());
				editor.putString(InstapaperRequest.INSTAPAPER_PASSWORD, encryptedString).commit();
				// start the auth request for instapaper
				InstapaperRequest request = new InstapaperRequest(sharedPref);
				request.getDataFromURLForcingBasicAuth(this);
			} catch (Exception e) {
				Log.e(TAG, "bad encrypted string for instapaper password", e);
			}
		}
	}
	
	abstract public void onResumeWorker();
	abstract public void startTwitterRequestOnBrowser();
}