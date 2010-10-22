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

import oauth.signpost.OAuth;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import android.net.Uri;

public abstract class AbstractOAuthAccounts {
	private static final String CALLBACK_URL =  "myapp://oauth";
	
	private DefaultOAuthProvider provider;
	private CommonsHttpOAuthConsumer consumer;
	
	/**
	 * Constructor
	 * @param request_token OAuth Request Token URL
	 * @param access_token OAuth Access Token URL
	 * @param authorize OAuth authorize url
	 */
	public AbstractOAuthAccounts(String request_token, String access_token, String authorize) {
		provider = new DefaultOAuthProvider(request_token, access_token, authorize);
	}
	
	/**
	 * setConsumer
	 * this method set CommonsHttpOAuthConsumer to instance variable
	 */
	public void setConsumer(CommonsHttpOAuthConsumer consumer) {
		this.consumer = consumer;
	}
	
	/**
	 * Request Token
	 */
	public String requestToken() {
		String token = null;
		try {
			token = provider.retrieveRequestToken(consumer, CALLBACK_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return token;
	}
	
	/**
	 * Get Data from Intent
	 * @param uri get url address from callback
	 */
	public void getDataFromIntent(Uri uri) {
		if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
			String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
			// this will populate token and token_secret in consumer
			try {
				provider.retrieveAccessToken(consumer, verifier);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Post Data
	abstract public void postData(String status);
}