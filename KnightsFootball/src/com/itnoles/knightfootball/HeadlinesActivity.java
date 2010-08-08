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

import com.itnoles.shared.*;

import android.app.ListActivity;
import android.content.*;
//import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class HeadlinesActivity extends ListActivity {
	private InstapaperRequest request;
	
	/*@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}*/

	@Override
	protected void onResume()
	{
		super.onResume();
		
		Utilities.NetworkCheck(this);
		SharedPreferences prefs = getSharedPreferences("KnightsSetting", MODE_PRIVATE);
		request = new InstapaperRequest(prefs);
		ListView listview = getListView();
		new FeedLoadingTask(this, listview,
		prefs.getString("newsurl", "http://ucfathletics.cstv.com/sports/m-footbl/headline-rss.xml"),
		prefs.getString("newstitle", "Knights Athletics")).execute();
		registerForContextMenu(listview);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		menu.setHeaderTitle("Select Options");
		if (request.instapaperReady()) {
			menu.add(Menu.NONE, 0, Menu.NONE, "Send to Instapaper");
		}
		menu.add(Menu.NONE, 1, Menu.NONE, "Open In WebView");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		News news = (News) getListView().getAdapter().getItem(info.position);
		String link = news.getLink();
		switch(item.getItemId()) {
			case 0:
				request.urlString = link;
				request.title = news.getTitle();
				request.isPost = true;
				request.loadDataFromURLForcingBasicAuth("https://www.instapaper.com/api/add", this);
			return true;
			case 1:
				Intent displayWebView = new Intent(this, WebViewActivity.class);
				displayWebView.putExtra("url", link);
				startActivity(displayWebView);
			return true;
		}
		return super.onContextItemSelected(item);
	}
}