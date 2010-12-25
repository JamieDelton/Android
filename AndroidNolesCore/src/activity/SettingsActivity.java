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
//import android.net.Uri;
import android.preference.*; // ListPreference, CheckBoxPreference, EditTextPreference, PreferenceActivity and Preference
import android.view.Gravity;
import android.widget.TextView;
import android.util.Log;

import com.itnoles.shared.*; //Constants, InstapaperRequest and Utilities
import com.itnoles.shared.helper.SimpleCrypto;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	private static final String TAG = "AbstractSettingsActivity";
	private static final String NEWS = "news";
	SharedPreferences sharedPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Load the XML preferences file
		addPreferencesFromResource(R.xml.preferences);
		
		sharedPref = getSharedPreferences("settings", MODE_PRIVATE);
		
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
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	private void commitChange(SharedPreferences.Editor editor) {
		if (Constants.ISORLATER_GINGERBREAD)
			editor.apply();
		else
			editor.commit();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		Preference pref = findPreference(key);
		SharedPreferences.Editor editor = sharedPref.edit();
		if (key.equals(NEWS))
		{
			ListPreference newsPref = (ListPreference)pref;
			int index = newsPref.findIndexOfValue(newsPref.getValue());
			if (index != -1)
			{
				editor.putString("newstitle", newsPref.getEntries()[index].toString());
				editor.putString("newsurl", newsPref.getEntryValues()[index].toString());
				// Don't forget to commit or apply your edits!!!
				commitChange(editor);
			}
		}
		
		if (key.equals(InstapaperRequest.INSTAPAPER_ENABLED))
		{
			CheckBoxPreference instapaperEnabledPref = (CheckBoxPreference)pref;
			editor.putBoolean(key, instapaperEnabledPref.isChecked());
			commitChange(editor);
		}
			
		if (key.equals(InstapaperRequest.INSTAPAPER_USERNAME))
		{
			try {
				EditTextPreference usernameEditPref = (EditTextPreference)pref;
				String encryptedString = SimpleCrypto.encrypt(key, usernameEditPref.getText());
				editor.putString(key, encryptedString);
				commitChange(editor);
			} catch(Exception e) {
				Log.e(TAG, "bad encrypted string for instapaper username", e);
			}
		}

		if (key.equals(InstapaperRequest.INSTAPAPER_PASSWORD))
		{
			try {
				EditTextPreference passwordEditPref = (EditTextPreference)pref;
				String encryptedString = SimpleCrypto.encrypt(key, passwordEditPref.getText());
				editor.putString(key, encryptedString);
				commitChange(editor);
				// start the auth request for instapaper
				InstapaperRequest request = new InstapaperRequest(this, sharedPref);
				request.sendData("https://www.instapaper.com/api/authenticate");
			} catch (Exception e) {
				Log.e(TAG, "bad encrypted string for instapaper password", e);
			}
		}
	}
}