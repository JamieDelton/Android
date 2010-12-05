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

import android.net.Uri;
import android.util.Log;

import oauth.signpost.*;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

public abstract class AbstractOAuthAccounts {
	private String callbackurl;
	private OAuthProvider provider;
	private CommonsHttpOAuthConsumer consumer;
	private static final String TAG = "AbstractOAuthAccounts";
	
	/**
	 * Constructor
	 * @param request_token OAuth Request Token URL
	 * @param access_token OAuth Access Token URL
	 * @param authorize OAuth authorize url
	 * @param callbackurl OAuth Callback url
	 */
	public AbstractOAuthAccounts(String request_token, String access_token, String authorize, String callbackurl) {
		provider = new DefaultOAuthProvider(request_token, access_token, authorize);
		this.callbackurl = callbackurl;
	}
	
	public void setConsumer(CommonsHttpOAuthConsumer consumer) {
		this.consumer = consumer;
	}
	
	/**
	 * Request Token
	 */
	public String requestToken() {
		String token = null;
		try {
			token = provider.retrieveRequestToken(consumer, callbackurl);
		} catch (Exception e) {
			Log.e(TAG, "bad request Token", e);
		}
		return token;
	}
	
	/**
	 * Get Data from Intent
	 * @param uri get url address from callback
	 */
	public void getDataFromIntent(Uri uri) {
		if (uri != null && uri.toString().startsWith(callbackurl)) {
			String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
			// this will populate token and token_secret in consumer
			try {
				provider.retrieveAccessToken(consumer, verifier);
			} catch (Exception e) {
				Log.e(TAG, "bad access Token", e);
			}
		}
	}
	
	// Post Data
	abstract public void postData(String status);
}