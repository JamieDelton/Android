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

import org.apache.http.*; //HttpEntity, HttpResponse, HttpStatus
import org.apache.http.client.methods.HttpGet;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import java.io.IOException;

public class HttpUtils {
	private static final String TAG = "HttpUtils";

	public static HttpEntity openConnection(AndroidHttpClient client, String urlString) throws IOException
	{
		final HttpGet getRequest = new HttpGet(urlString);
		HttpResponse response = client.execute(getRequest);
		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			Log.w(TAG, "Error " + statusCode + " while retrieving data from " + urlString);
			return null;
		}
		return response.getEntity();
	}
}