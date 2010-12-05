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
package com.itnoles.shared.helper;

import org.apache.http.HttpEntity;
import org.json.JSONArray;

import android.util.Log;

import java.io.InputStream;

import com.itnoles.shared.HttpUtils;

/**
 * JSONHelper
 * class that download and parse JSON data
 * @author Jonathan Steele
 */

public class JSONHelper
{
	private static final String TAG = "JSONHelper";
	
	public static JSONArray getJSONArray(String urlString)
	{
		JSONArray json = null;
		try {
			final HttpEntity entity = HttpUtils.openConnection(urlString);
			if (entity != null)
			{
				InputStream inputStream = null;
				byte[] buffer = new byte[1024];
				try {
					inputStream = entity.getContent();
					int bytesRead = 0;
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						json = new JSONArray(new String(buffer, 0, bytesRead));
					}
				} finally {
					if (inputStream != null)
						inputStream.close();
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			Log.w(TAG, "bad json array", e);
		}
		return json;
	}
}