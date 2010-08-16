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

import java.io.IOException;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.*;
import android.util.Base64;

public class InstapaperRequest
{
	private SharedPreferences prefs;
	private Context context;
	
	public InstapaperRequest(SharedPreferences mSP, Context context)
	{
		this.prefs = mSP;
		this.context = context;
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

	public void loadDataFromURLForcingBasicAuth(Boolean isPost, News news)
	{
		// If it is not ready or connection is not available, don't try to open the connection.
		Boolean networkCheck = Utilities.NetworkCheck(context);
		if (!instapaperReady() || !networkCheck) 
			return;

		HttpClient httpClient = new DefaultHttpClient();
		String userandpass = getUserName() + ":" + getPassword();
		if (isPost) {
			HttpPost httpPost = new HttpPost("https://www.instapaper.com/api/add");
			httpPost.addHeader("Authorization", "basic " + Base64.encode(userandpass.getBytes(), Base64.DEFAULT));
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("url", news.getLink()));
				nameValuePairs.add(new BasicNameValuePair("title", news.getTitle()));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				getStatusCode(httpResponse);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			HttpGet httpGet = new HttpGet("https://www.instapaper.com/api/authenticate");
			httpGet.addHeader("Authorization", "basic " + Base64.encode(userandpass.getBytes(), Base64.DEFAULT));
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				getStatusCode(httpResponse);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void getStatusCode(HttpResponse response)
	{
		final String message;
		switch(response.getStatusLine().getStatusCode()) {
			case HttpStatus.SC_CREATED:
				message = "This URL has been successfully added to this Instapaper account.";
			break;
			case HttpStatus.SC_BAD_REQUEST:
				message = "Bad request.";
			break;
			case HttpStatus.SC_FORBIDDEN:
				message = "Invalid username or password.";
			break;
			case HttpStatus.SC_INTERNAL_SERVER_ERROR:
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
	}
}