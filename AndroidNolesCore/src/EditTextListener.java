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
package com.itnoles.shared;

import android.content.*;
import android.preference.EditTextPreference;

public class EditTextListener extends EditTextPreference
{
	public EditTextListener(Context context) {
		super(context);
	}
	
	@Override
	// When the dialog is closed, perform the relevant actions
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			SharedPreferences settings = getSharedPreferences();
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(getKey(), getEditText().getText().toString());
			editor.commit();
			if (getKey().equals("instapaper_password")) {
				InstapaperRequest request = new InstapaperRequest(settings);
				request.loadDataFromURLForcingBasicAuth("https://www.instapaper.com/api/authenticate", getContext());
			}
		}
	}
}