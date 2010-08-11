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

import com.itnoles.shared.EditTextListener;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.*;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.widget.TextView;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String NEWS = "news";
	private static final String ENABLED = "instapaper_enabled";

	private ListPreference mNewsPref;
	private CheckBoxPreference mInstapaperEnabledPref;
	private EditTextPreference usernameEditPref;
	private EditTextPreference passwordEditPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getPreferenceManager().setSharedPreferencesName("KnightsSetting");

		setPreferenceScreen(createPreferenceHierarchy());
		
		// The setDependency call must be made after the setPreferencesScreen() call
		usernameEditPref.setDependency(ENABLED);
		passwordEditPref.setDependency(ENABLED);
		
		// Set up a listener whenever a key changes            
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		TextView mCopyright = new TextView(this);
		mCopyright.setText("Written by Jonathan Steele\nEmail: xfsunoles@gmail.com");
		mCopyright.setGravity(Gravity.CENTER);
		getListView().addFooterView(mCopyright);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
	}
	
	private PreferenceScreen createPreferenceHierarchy() {
		 // Root
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
	
		mNewsPref = new ListPreference(this);
		mNewsPref.setEntries(R.array.listNames);
		mNewsPref.setEntryValues(R.array.listValues);
		mNewsPref.setDialogTitle(R.string.listPref_dialogTitle);
		mNewsPref.setKey(NEWS);
		mNewsPref.setTitle(R.string.listPref_title);
		mNewsPref.setSummary(R.string.listPref_summary);
		root.addPreference(mNewsPref);
		
		mInstapaperEnabledPref = new CheckBoxPreference(this);
		mInstapaperEnabledPref.setKey(ENABLED);
		mInstapaperEnabledPref.setTitle(R.string.Instapaper_enabled_title);
		mInstapaperEnabledPref.setSummary(R.string.Instapaper_enabled_summary);
		root.addPreference(mInstapaperEnabledPref);

		PreferenceCategory instapaperPrefCat = new PreferenceCategory(this);
		instapaperPrefCat.setTitle(R.string.Instapaper);
		root.addPreference(instapaperPrefCat);

		// Edit text preference
		usernameEditPref = new EditTextListener(this);
		usernameEditPref.setDialogTitle(R.string.Instapaper_username_title);
		usernameEditPref.setKey("instapaper_username");
		usernameEditPref.setTitle(R.string.Instapaper_username_title);
		usernameEditPref.setSummary(R.string.Instapaper_username_summary);
		usernameEditPref.getEditText().setSingleLine();
		instapaperPrefCat.addPreference(usernameEditPref);
		
		passwordEditPref = new EditTextListener(this);
		passwordEditPref.setDialogTitle(R.string.Instapaper_password_title);
		passwordEditPref.setKey("instapaper_password");
		passwordEditPref.setTitle(R.string.Instapaper_password_title);
		passwordEditPref.setSummary(R.string.Instapaper_password_summary);
		passwordEditPref.getEditText().setSingleLine();
		passwordEditPref.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		passwordEditPref.getEditText().setTransformationMethod(new PasswordTransformationMethod());
		instapaperPrefCat.addPreference(passwordEditPref);
		
		return root;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if (key.equals(NEWS)) {
			int index = mNewsPref.findIndexOfValue(mNewsPref.getValue());
			if (index != -1) {
				editor.putString("newstitle", mNewsPref.getEntries()[index].toString());
				editor.putString("newsurl", mNewsPref.getEntryValues()[index].toString());
			}
		}
		
		if (key.equals(ENABLED)) {
			editor.putBoolean(ENABLED, mInstapaperEnabledPref.isChecked());
		}
		// Don't forget to commit your edits!!!
		editor.commit();
	}
}