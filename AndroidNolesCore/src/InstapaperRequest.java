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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*; //DefaultHttpClient and BasicCredentialsProvider

import android.app.ProgressDialog;
import android.content.*; //SharedPreference and Context
import android.util.Log;
import android.widget.Toast;

import com.itnoles.shared.helper.*; //SimpleCrypto and BetterAsyncTaskCompleteListener

/**
 * InstapaperRequest
 * it is class that handle request for instapaper
 * @author Jonathan Steele
 */

public class InstapaperRequest implements BetterAsyncTaskCompleteListener<String, Void, Integer>
{
	private static final String TAG = "InstapaperRequest";
	private SharedPreferences prefs;
	private Context context;
	
	public static final String INSTAPAPER_ENABLED = "instapaper_enabled";
	public static final String INSTAPAPER_USERNAME = "instapaper_username";
	public static final String INSTAPAPER_PASSWORD = "instapaper_password";

	// Constructor
	public InstapaperRequest(Context context, SharedPreferences mSP)
	{
		this.context = context;
		this.prefs = mSP;
	}
	
	private String getEncyptedString(String name, String value) throws Exception
	{
		return SimpleCrypto.decrypt(name, value);
	}
	
	private String getUserName() {
		return prefs.getString(INSTAPAPER_USERNAME, "");
	}
	
	private String getPassword() {
		return prefs.getString(INSTAPAPER_PASSWORD, "");
	}
	
	private DefaultHttpClient createCredentials()
	{
		// create credentials for basic auth
		UsernamePasswordCredentials upc = null;
		try {
			String username = getEncyptedString(INSTAPAPER_USERNAME, getUserName());
			String password = getEncyptedString(INSTAPAPER_PASSWORD, getPassword());
			upc = new UsernamePasswordCredentials(username, password);
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
	
	// Send Data To Instapaper
	public void sendData(String url)
	{
		// If it is not ready, don't try to open the connection.
		Boolean isInstapaperReady = prefs.getBoolean(INSTAPAPER_ENABLED, false) && getUserName().length() > 0 && getPassword().length() > 0;
		if (!isInstapaperReady)
			return;
		
		new BetterBackgroundTask<String, Void, Integer>(this).execute(url);
	}
	
	public void onTaskComplete(Integer statusCode)
	{
		final String message;
		switch(statusCode) {
			case HttpStatus.SC_CREATED: message = "This URL has been successfully added to this Instapaper account."; break;
			case HttpStatus.SC_BAD_REQUEST: message = "Bad request."; break;
			case HttpStatus.SC_FORBIDDEN: message = "Invalid username or password."; break;
			case HttpStatus.SC_INTERNAL_SERVER_ERROR: message = "The service encountered an error. Please try again later."; break;
			default: message = "OK"; break;
		}
		Utilities.showToast(context, message, Toast.LENGTH_LONG);
	}
	
	// Do This stuff in Background
	public Integer readData(String ...params)
	{
		final DefaultHttpClient client = createCredentials();
		if (client == null)
			return null;
		
		String url = params[0];
		
		ProgressDialog pd = null;
		if (url.equals("https://www.instapaper.com/api/authenticate"))
			pd = Utilities.showProgressDialog(context, "Signing in...");
		
		// Send the HTTP Get Request
		final HttpGet getRequest = new HttpGet(url);
		int statuscode = 0;
		try {
			HttpResponse httpResponse = client.execute(getRequest);
			statuscode = httpResponse.getStatusLine().getStatusCode();
		} catch (Exception e) {
			getRequest.abort();
			Log.e(TAG, "bad httpResponse for get Data", e);
		} finally {
			client.getConnectionManager().shutdown();
		}
		
		// Dismiss progress bar if it is showing
		if (pd != null && pd.isShowing())
			pd.dismiss();
		return new Integer(statuscode);
	}
}