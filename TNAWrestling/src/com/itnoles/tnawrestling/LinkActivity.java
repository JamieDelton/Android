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
package com.itnoles.tnawrestling;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.widget.*;
import android.view.View;

public class LinkActivity extends ListActivity {
	private static final String[] LINK = new String[] {
		"ShopTNA", "Universal Studio", "Upcoming Autograph Signing", "TNA Facebook"
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
		if (position == 0) {
			url = "http://www.shoptna.com";
		} else if (position == 1) {
			url = "http://www.universalorlando.com/OverviewPages/Shows/dozensofshows.aspx#page=Shows_tna-wrestling.html&expID=13-5505&contentID=13-3808&seq=1";
		} else if (position == 2) {
			url = "http://www.tnawrestling.com/roster/autograph-signings";
		} else {
			url = "http://www.facebook.com/tnawrestling";
		}
        /* Take string from url and parse it to the default browsers */
		final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(viewIntent);
	}
}