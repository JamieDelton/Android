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
package com.itnoles.nolesfootball;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.itnoles.shared.Utilities;

public class NolesFootball extends TabActivity
{
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if (!Utilities.NetworkCheck(this)) {
			Utilities.showAlertDialog(this,"Internet Not Found", R.string.InternetNotFound);
			return;
		}
		
		Resources res = getResources(); // Resource object to get Drawables
		final TabHost tabHost = getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, NolesHeadlinesActivity.class);
		
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("headlines").setIndicator("Headlines",
		                  res.getDrawable(R.drawable.notepad))
		              .setContent(intent);
		tabHost.addTab(spec);
		
		// Do the same for the other tabs
		intent = new Intent().setClass(this, NolesScheduleActivity.class);
		spec = tabHost.newTabSpec("schedule").setIndicator("Schedule",
		                  res.getDrawable(R.drawable.calendar))
		              .setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, LinkActivity.class);
		spec = tabHost.newTabSpec("link").setIndicator("Link",
		                  res.getDrawable(R.drawable.bookmark))
		              .setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, NolesStaffActivity.class);
		spec = tabHost.newTabSpec("staff").setIndicator("Staff",
		                  res.getDrawable(R.drawable.star))
		              .setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, NolesSettingsActivity.class);
		spec = tabHost.newTabSpec("setting").setIndicator("Settings",
		                  res.getDrawable(R.drawable.gear2))
		              .setContent(intent);
		tabHost.addTab(spec);
	}
}