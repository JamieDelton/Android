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

import java.io.*;
import org.json.*;

import android.app.Activity;

public class JSONHelper {
	private JSONObject json;
	
	public JSONHelper(String assetString, Activity activity) {
		try {
			InputStream is = activity.getAssets().open(assetString);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			// Convert the buffer into a string
			String jsonString = new String(buffer);
			try {
				json = new JSONObject(jsonString);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public JSONObject getJSONObject() {
		return json;
	}
}