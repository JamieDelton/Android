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

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import com.itnoles.shared.helper.AbstractOAuthAccounts;

/**
 * OAuth class for Twitter
 * @author Jonathan Steele
 */
public final class TwitterOAuth extends AbstractOAuthAccounts {
	private CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer("key go here", "secret go here");
	
	public StatusNetOAuth() {
		super("https://api.twitter.com/oauth/request_token",
		"https://api.twitter.com/oauth/access_token",
		"https://api.twitter.com/oauth/authorize");
		super.setConsumer(consumer);
	}
	
	/**
	 * Post Data to StatusNet
	 * @param status text that may be published to statusnet
	 */
	@Override
	public void postData(String status) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://api.twitter.com/version/statuses/update.xml");
		final List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("status", status));
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			// set this to avoid 417 error (Expectation Failed) 
			post.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			// sign the request
			consumer.sign(post);
			// send the request
			final HttpResponse response = client.execute(post);
			// release connection  
			response.getEntity().consumeContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}