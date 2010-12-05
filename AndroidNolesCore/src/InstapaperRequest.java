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

import org.apache.http.*;
import org.apache.http.auth.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;

import android.content.*; //SharedPreference and Context
import android.util.Log;

import java.util.*; //List and ArrayList

import com.itnoles.shared.helper.SimpleCrypto;

/**
 * InstapaperRequest
 * it is class that handle request for instapaper
 * @author Jonathan Steele
 */

public class InstapaperRequest
{
	private static final String TAG = "InstapaperRequest";
	private SharedPreferences prefs;

	public static final String INSTAPAPER_ENABLED = "instapaper_enabled";
	public static final String INSTAPAPER_USERNAME = "instapaper_username";
	public static final String INSTAPAPER_PASSWORD = "instapaper_password";
	
	// Constructor
	public InstapaperRequest(SharedPreferences mSP)
	{
		this.prefs = mSP;
	}
	
	private String getUserName() throws Exception {
		return SimpleCrypto.decrypt(INSTAPAPER_USERNAME, prefs.getString(INSTAPAPER_USERNAME, ""));
	}
	
	private String getPassword() throws Exception {
		return SimpleCrypto.decrypt(INSTAPAPER_PASSWORD, prefs.getString(INSTAPAPER_PASSWORD, ""));
	}
	
	// make sure it is ready for making a request
	private Boolean isInstapaperReady()
	{
		// If it is not ready, don't try to open the connection.
		try {
			return prefs.getBoolean(INSTAPAPER_ENABLED, false) && getUserName().length() > 0 && getPassword().length() > 0;
		} catch (Exception e) {
			Log.e(TAG, "bad boolean", e);
			return false;
		}
	}
	
	private DefaultHttpClient createCredentials()
	{
		// create credentials for basic auth
		UsernamePasswordCredentials upc = null;
		try {
			upc = new UsernamePasswordCredentials(getUserName(),getPassword());
		} catch (Exception e) {
			Log.e(TAG, "bad userpasswordcredentials", e);
			return null;
		}
		// create a basic credentials provider and pass the credentials
		BasicCredentialsProvider credProvider = new BasicCredentialsProvider();
		credProvider.setCredentials(AuthScope.ANY, upc);
		// set credentials provider for our default http client so it will use those
		// credentials
		DefaultHttpClient client = new DefaultHttpClient();
		client.setCredentialsProvider(credProvider);
		return client;
	}
	
	// Get Data from instapaper for Authenticate
	public void getDataFromURLForcingBasicAuth(Context context)
	{
		if (!isInstapaperReady())
			return;
		
		final DefaultHttpClient httpClient = createCredentials();
		if (httpClient == null)
			return;
		
		final HttpGet getRequest = new HttpGet("https://www.instapaper.com/api/authenticate");
		try {
			HttpResponse httpResponse = httpClient.execute(getRequest);
			getHTTPResources(httpResponse, context);
		} catch (Exception e) {
			getRequest.abort();
			Log.e(TAG, "bad httpResponse for get Data", e);
		} finally {
			// shut down the connection manager
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	// post data to instapaper with url and title
	public void postDataURLForcingBasicAuth(News news, Context context)
	{
		if (!isInstapaperReady())
			return;
		
		final DefaultHttpClient httpClient = createCredentials();
		if (httpClient == null)
			return;
		
		final HttpPost httpPost = new HttpPost("https://www.instapaper.com/api/add");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("url", news.getLink()));
		nameValuePairs.add(new BasicNameValuePair("title", news.getTitle()));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			getHTTPResources(httpResponse, context);
		} catch (Exception e) {
			httpPost.abort();
			Log.e(TAG, "bad httpResponse for post Data", e);
		} finally {
			// shut down the connection manager
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	private void getHTTPResources(HttpResponse response, Context context) throws Exception
	{
		final String message;
		switch(response.getStatusLine().getStatusCode()) {
			case HttpStatus.SC_CREATED: message = "This URL has been successfully added to this Instapaper account."; break;
			case HttpStatus.SC_BAD_REQUEST: message = "Bad request."; break;
			case HttpStatus.SC_FORBIDDEN: message = "Invalid username or password."; break;
			case HttpStatus.SC_INTERNAL_SERVER_ERROR: message = "The service encountered an error. Please try again later."; break;
			default: message = "OK"; break;
		}
		
		Utilities.showAlertDialog(context, "Instapaper Results", message);
		
		// release all allocated resources if it is not null
		HttpEntity entity = response.getEntity();
		if (entity != null)
			entity.consumeContent();
	}
}