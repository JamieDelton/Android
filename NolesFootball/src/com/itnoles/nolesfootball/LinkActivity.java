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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.widget.*;
import android.view.View;

public class LinkActivity extends ListActivity {
	private static final String[] LINK = new String[] {
		"Tickets", "Upcoming Events and Promo", "Seminoles Booster", "Radio Affilate", "Traditions", "Team Ranking", "Seminoles Podcast"
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
		switch(position) {
			case 0: url = "http://www.seminoles.com/tickets/fsu-tickets.html"; break;
			case 1: url = "http://www.seminoles.com/genrel/nole-zone.html"; break;
			case 2: url = "http://seminole-boosters.fsu.edu/Community/Page.aspx?pid=520&srcid=223"; break;
			case 3: url = "http://www.seminoles.com/multimedia/broadcast.html"; break;
			case 4: url = "http://www.seminoles.com/trads/fsu-trads.html"; break;
			case 5: url = "http://www.seminoles.com/genrel/rankings.html"; break;
			default: url = "http://www.seminoles.com/podcasts/fsu-podcasts.html"; break;
		}
		
		/* Take string from url and parse it to the default browsers */
		final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(viewIntent);
	}
}