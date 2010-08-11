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

import java.lang.Exception;
import java.net.*;
import java.io.OutputStreamWriter;

import android.app.AlertDialog;
import android.content.*;
import android.util.Base64;

public class InstapaperRequest
{
	private SharedPreferences prefs;
	public boolean isPost = false;
	public String urlString = null;
	public String title = null;
	
	public InstapaperRequest(SharedPreferences mSP)
	{
		this.prefs = mSP;
	}
	
	private String getUserName()
	{
		return prefs.getString("instapaper_username", "");
	}
	
	private String getPassword()
	{
		return prefs.getString("instapaper_password", "");
	}
	
	private Boolean instapaperReady()
	{
		return prefs.getBoolean("instapaper_enabled", false) && getUserName().length() > 0 && getPassword().length() > 0;
	}

	public void loadDataFromURLForcingBasicAuth(String url, Context context)
	{
		// If it is not ready or connection is not available, don't try to open the connection.
		Boolean networkCheck = Utilities.NetworkCheck(context);
		if (!instapaperReady() || !networkCheck) 
			return;

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setUseCaches(false);
			String userandpass = getUserName() + ":" + getPassword();
			connection.setRequestProperty("Authorization", "basic " + Base64.encode(userandpass.getBytes(), Base64.DEFAULT));
			if (isPost) {
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setDoOutput(true);
				
				// Send Request
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write("url=" + URLEncoder.encode(urlString, "UTF-8") + ",title=" + title);
				out.flush();
				out.close();
			} else {
				connection.connect();
			}
			final String message;
			switch(connection.getResponseCode()) {
				case HttpURLConnection.HTTP_CREATED:
					message = "This URL has been successfully added to this Instapaper account.";
				break;
				case HttpURLConnection.HTTP_BAD_REQUEST:
					message = "Bad request.";
				break;
				case HttpURLConnection.HTTP_FORBIDDEN:
					message = "Invalid username or password.";
				break;
				case HttpURLConnection.HTTP_INTERNAL_ERROR:
					message = "The service encountered an error. Please try again later.";
				break;
				default:
					message = "OK";
			}

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
			alertDialog.setTitle(R.string.Instapaper_result);
			alertDialog.setMessage(message);
			alertDialog.setPositiveButton(R.string.OK, null);
			alertDialog.show();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}