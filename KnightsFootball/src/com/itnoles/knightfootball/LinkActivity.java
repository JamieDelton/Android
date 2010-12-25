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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.widget.*;
import android.view.View;

public class LinkActivity extends ListActivity {
	private static final String[] LINK = new String[] {
		"Tickets", "UCF Gameday", "UCFAlumni.com", "Alumni Tracker", "Golden Knights Club", "Radio Network", "UCF Photos"
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, LINK));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, final int position, long id) {
		super.onListItemClick(l, v, position, id);

		final String url;
		switch (position) {
			case 0: url = "http://ucfathletics.cstv.com/tickets/ucf-tickets.html"; break;
			case 1: url = "http://ucfathletics.cstv.com/brighthouse/"; break;
			case 2: url = "http://www.ucfalumni.com/"; break;
			case 3: url = "http://cbs.sportsline.com/collegefootball/alumni-tracker/school/5233"; break;
			case 4: url = "http://www.ucfathleticfund.com/"; break;
			case 5: url = "http://ucfathletics.cstv.com/multimedia/ucf-isp-radio-network.html"; break;
			default: url = "http://ucfphotos.photoshelter.com/gallery-list"; break;
		}
		
		// Take string from url and parse it to the default browsers
		final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(viewIntent);
	}
}