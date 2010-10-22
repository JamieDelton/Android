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

/**
 * InstapaperRequest
 * it is class that handle request for instapaper
 * @author Jonathan Steele
 */

public final class InstapaperRequest
{
	private SharedPreferences prefs;
	private Context context;
	private static final HttpClient httpClient = new DefaultHttpClient();
	
	// Constructor
	public InstapaperRequest(SharedPreferences mSP, Context ctx)
	{
		this.prefs = mSP;
		this.context = ctx;
	}
	
	// make sure it is ready for making a request
	private Boolean isInstapaperReady()
	{
		// If it is not ready, don't try to open the connection.
		return prefs.getBoolean("instapaper_enabled", false) && getUserName().length() > 0 && getPassword().length() > 0;
	}
	
	// get user and password from shared preference in base64 form
	private byte[] getBytesFromUserAndPass()
	{
		return prefs.getString("instapaper_username", "") + ":" + prefs.getString("instapaper_password", "").getBytes();
	}
	
	// Get Data from instapaper for Authenticate
	public void getDataFromURLForcingBasicAuth()
	{
		if (!isInstapaperReady())
			return;
		HttpGet httpGet = new HttpGet("https://www.instapaper.com/api/authenticate");
		httpGet.addHeader("Authorization", "basic " + Base64.encode(getBytesFromUserAndPass(), Base64.DEFAULT));
		try {
			final HttpResponse httpResponse = httpClient.execute(httpGet);
			getStatusCode(httpResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// post data to instapaper with url and title
	public void postDataURLForcingBasicAuth(News news)
	{
		if (!isInstapaperReady())
			return;
		HttpPost httpPost = new HttpPost("https://www.instapaper.com/api/add");
		httpPost.addHeader("Authorization", "basic " + Base64.encode(getBytesFromUserAndPass(), Base64.DEFAULT));
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("url", news.getLink()));
		nameValuePairs.add(new BasicNameValuePair("title", news.getTitle()));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			final HttpResponse httpResponse = httpClient.execute(httpPost);
			getStatusCode(httpResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// get status code from HttpResponse
	private void getStatusCode(HttpResponse response)
	{
		final String message;
		switch(response.getStatusLine().getStatusCode()) {
			case HttpStatus.SC_CREATED: message = "This URL has been successfully added to this Instapaper account."; break;
			case HttpStatus.SC_BAD_REQUEST: message = "Bad request."; break;
			case HttpStatus.SC_FORBIDDEN: message = "Invalid username or password."; break;
			case HttpStatus.SC_INTERNAL_SERVER_ERROR: message = "The service encountered an error. Please try again later."; break;
			default: message = "OK"; break;
		}
		
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle("Instapaper Results");
		ad.setMessage(message);
		ad.setPositiveButton("OK", null);
		ad.show();
	}
}