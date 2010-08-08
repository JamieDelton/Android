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

import android.content.*;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.widget.TextView;

public class SettingsActivity extends PreferenceActivity {
	private SharedPreferences settings;
	private InstapaperRequest request;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		settings = getSharedPreferences("KnightsSetting", MODE_PRIVATE);
		request = new InstapaperRequest(settings);
		
		setPreferenceScreen(createPreferenceHierarchy());
		
		TextView mCopyright = new TextView(this);
		mCopyright.setText("Written by Jonathan Steele\nEmail: xfsunoles@gmail.com");
		mCopyright.setGravity(Gravity.CENTER);
		getListView().addFooterView(mCopyright);
	}
	
	private PreferenceScreen createPreferenceHierarchy() {
		 // Root
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
	
		final ListPreference mNewsPref = new ListPreference(this);
		mNewsPref.setEntries(R.array.listArray);
		mNewsPref.setEntryValues(R.array.listValues);
		mNewsPref.setDialogTitle(R.string.listPref_dialogTitle);
		mNewsPref.setKey("news");
		mNewsPref.setTitle(R.string.listPref_title);
		mNewsPref.setSummary(R.string.listPref_summary);
		mNewsPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(final Preference preference, final Object newValue) {
				SharedPreferences.Editor editor = settings.edit();
				int index = mNewsPref.findIndexOfValue(newValue.toString());
				if (index != -1) {
					editor.putString("newstitle", mNewsPref.getEntries()[index].toString());
					editor.putString("newsurl", mNewsPref.getEntryValues()[index].toString());
				}
				// Don't forget to commit your edits!!!
				editor.commit();
				return true;
			}
		});
		root.addPreference(mNewsPref);
		
		PreferenceCategory instapaperPrefCat = new PreferenceCategory(this);
		instapaperPrefCat.setTitle(R.string.Instapaper);
		root.addPreference(instapaperPrefCat);

		// Edit text preference
		EditTextPreference usernameEditPref = new EditTextListener(this);
		usernameEditPref.setDialogTitle(R.string.Instapaper_username_title);
		usernameEditPref.setKey("instapaper_username");
		usernameEditPref.setTitle(R.string.Instapaper_username_title);
		usernameEditPref.setSummary(R.string.Instapaper_username_summary);
		usernameEditPref.getEditText().setSingleLine();
		instapaperPrefCat.addPreference(usernameEditPref);
		
		EditTextPreference passwordEditPref = new EditTextListener(this);
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
	
	private class EditTextListener extends EditTextPreference
	{
		public EditTextListener(Context context){
			super(context);
		}
		
		@Override
		// When the dialog is closed, perform the relevant actions
		protected void onDialogClosed(boolean positiveResult) {
			if (positiveResult) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(getKey(), getEditText().getText().toString());
				editor.commit();
				if (getKey().equals("instapaper_password")) {
					request.loadDataFromURLForcingBasicAuth("https://www.instapaper.com/api/authenticate", SettingsActivity.this);
				}
			}
		}
	}
}