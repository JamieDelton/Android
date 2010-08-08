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
import android.os.Bundle;
import android.widget.TabHost;

public class NolesFootball extends TabActivity
{
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		TabHost host = getTabHost();
		host.addTab(host.newTabSpec("headlines").setIndicator("Headlines", getResources().getDrawable(R.drawable.openbook)).setContent(new Intent(this, HeadlinesActivity.class)));
		host.addTab(host.newTabSpec("schedule").setIndicator("Schedule", getResources().getDrawable(R.drawable.calendar)).setContent(new Intent(this, ScheduleActivity.class)));
		host.addTab(host.newTabSpec("link").setIndicator("Link", getResources().getDrawable(R.drawable.link)).setContent(new Intent(this, LinkActivity.class)));
		host.addTab(host.newTabSpec("staff").setIndicator("Staff", getResources().getDrawable(R.drawable.star)).setContent(new Intent(this, StaffActivity.class)));
		host.addTab(host.newTabSpec("setting").setIndicator("Settings", getResources().getDrawable(R.drawable.gear2)).setContent(new Intent(this, SettingsActivity.class)));
	}
}