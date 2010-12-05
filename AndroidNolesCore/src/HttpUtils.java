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

import org.apache.http.*; //HttpEntity, HttpResponse and HttpStatus
import org.apache.http.client.methods.HttpGet;

import android.net.http.AndroidHttpClient;
import android.util.Log;

public class HttpUtils {
	private static final String TAG = "HttpUtils";
	// AndroidHttpClient is not allowed to be used from the main thread
	private static final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
	
	public static HttpEntity openConnection(String url)
	{
		final HttpGet getRequest = new HttpGet(url);
		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w(TAG, "Error " + statusCode + " while retrieving data from " + url);
				return null;
			}
			return response.getEntity();
		} catch (Exception e) {
			getRequest.abort();
			Log.w(TAG, "Error while retrieving data from " + url, e);
		}
		return null;
	}
	
	public static void closeConnection() {
		client.close();
	}
}