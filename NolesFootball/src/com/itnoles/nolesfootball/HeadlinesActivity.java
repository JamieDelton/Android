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

import java.util.List;

import com.itnoles.shared.*;
import com.itnoles.shared.helper.*;

import android.app.ListActivity;
import android.content.*;
//import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class HeadlinesActivity extends ListActivity implements AsyncTaskCompleteListener {
	private SharedPreferences mPrefs;
	private SeparatedListAdapter adapter;
	
	/*@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}*/

	@Override
	protected void onResume()
	{
		super.onResume();
		
		mPrefs = getSharedPreferences("NolesSetting", MODE_PRIVATE);

		new BackgroundTask(this).execute();
		registerForContextMenu(getListView());
	}
	
	// Display Data to ListView
	public void onTaskComplete()
	{
		setListAdapter(adapter);
	}
	
	// Before Display the data
	public void preReadData()
	{
		Utilities.showToast(this, R.string.ReadingRSS);
	}
	
	// Do This stuff in Background
	public void readData()
	{
		List<News> news = FeedParser.parse(mPrefs.getString("newsurl", "http://www.seminoles.com/sports/m-footbl/headline-rss.xml"));
		adapter = new SeparatedListAdapter(this);
		adapter.addSection(mPrefs.getString("newstitle", "Noles Athletics"), new NewsAdapter(this, news));
	}
	
	// Show the list in the context menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		menu.setHeaderTitle("Select Options");
		menu.add(Menu.NONE, 0, Menu.NONE, "Send to Instapaper");
		menu.add(Menu.NONE, 1, Menu.NONE, "Open In WebView");
	}
	
	
	// When the users selected the item id in the cotext menu, it called specific item action.
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		News newsList = (News) getListAdapter().getItem(info.position);
		switch(item.getItemId()) {
			case 0:
				InstapaperRequest request = new InstapaperRequest(mPrefs, this);
				request.postDataURLForcingBasicAuth(newsList);
			return true;
			case 1:
				final Intent displayWebView = new Intent();
				displayWebView.setClass(this, WebViewActivity.class);
				displayWebView.putExtra("url", newsList.getLink());
				startActivity(displayWebView);
			return true;
		}
		return super.onContextItemSelected(item);
	}
}