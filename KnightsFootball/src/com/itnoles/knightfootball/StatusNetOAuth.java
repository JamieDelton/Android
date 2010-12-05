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
package com.itnoles.knightfootball;

import android.util.Log;

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
 * OAuth class for StatusNet that is compitable with Twitter.
 * @author Jonathan Steele
 */
public class StatusNetOAuth extends AbstractOAuthAccounts {
	private static final String TAG = "StatusNetOAuth";
	private static final String CALLBACK_URL = "knightsfb://statusnet";
	private CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer("key go here", "secret go here");
	
	public StatusNetOAuth() {
		super("http://xfsunoles.status.net/api/oauth/request_token",
		"http://xfsunoles.status.net/api/oauth/access_token",
		"http://xfsunoles.status.net/api/oauth/authorize", CALLBACK_URL);
		super.setConsumer(consumer);
	}
	
	/**
	 * Post Data to StatusNet
	 * @param status text that may be published to statusnet
	 */
	@Override
	public void postData(String status) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://xfsunoles.status.net/api/statuses/update.xml");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("status", status));
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			// set this to avoid 417 error (Expectation Failed) 
			post.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			// sign the request
			consumer.sign(post);
			// send the request
			HttpResponse response = client.execute(post);
			// release all allocated resources if it is not null
			HttpEntity entity = response.getEntity();
			if (entity != null)
				entity.consumeContent();
			// shut down the connection manager
			client.getConnectionManager().shutdown();
		} catch (Exception e) {
			Log.e(TAG, "bad post data", e);
		}
	}
}