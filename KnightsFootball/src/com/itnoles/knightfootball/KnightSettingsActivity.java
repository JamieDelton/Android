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

//import android.net.Uri;

import com.itnoles.shared.activity.AbstractSettingsActivity;

public class KnightSettingsActivity extends AbstractSettingsActivity
{
	//private TwitterOAuth mTwitter = new TwitterOAuth();
	
	@Override
	public void onResumeWorker()
	{
		// Checking to see Browser redirected to this apps by Twitter callback if it is exists
		//Uri uri = this.getIntent().getData();
		//mTwitter.getDataFromIntent(uri);
	}
	
	@Override
	public void startTwitterRequestOnBrowser()
	{
		//String authUrl = mTwitter.requestToken();
		//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
	}
}