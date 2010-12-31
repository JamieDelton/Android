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
package com.itnoles.shared.activity;

import android.app.*; // ListActivity and ProgressDialog
import android.content.*; // Intent and SharedPreferences
import android.os.Bundle;
import android.view.*; // Menu, MenuItem and View
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*; // AdapterView and TextView

import com.itnoles.shared.*; //BetterBackgroundTask, News, InstapaperRequest and Utilities
import com.itnoles.shared.adapter.NewsAdapter;
import com.itnoles.shared.helper.BetterAsyncTaskCompleteListener;

public class HeadlinesActivity extends ListActivity implements BetterAsyncTaskCompleteListener<String, Void, NewsAdapter> {
	private SharedPreferences mPrefs;
	private ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.headlines);
		pd = Utilities.showProgressDialog(this, "Loading...");
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		mPrefs = getSharedPreferences("settings", MODE_PRIVATE);
		
		String defaultTitle = getResources().getStringArray(R.array.listNames)[0];
		final TextView headerText = (TextView) findViewById(R.id.list_header_title);
		headerText.setText(mPrefs.getString("newstitle", defaultTitle));
		
		String defaultUrl = getResources().getStringArray(R.array.listValues)[0];
		new BetterBackgroundTask<String, Void, NewsAdapter>(this).execute(mPrefs.getString("newsurl", defaultUrl));
		registerForContextMenu(getListView());
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		// clear the list when this activity is nolonger visible.
		((NewsAdapter)getListAdapter()).clear();
	}
	
	// Display Data to ListView
	public void onTaskComplete(NewsAdapter adapter)
	{
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
			pd = null;
		}
		setListAdapter(adapter);
	}
	
	// Do This stuff in Background
	public NewsAdapter readData(String ...params)
	{
		java.util.List<News> news = com.itnoles.shared.helper.FeedParser.parse(params[0]);
		return new NewsAdapter(this, news);
	}
	
	// Show the list in the context menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		menu.setHeaderTitle(R.string.share);
		menu.add(Menu.NONE, 0, Menu.NONE, "Send to Instapaper");
		menu.add(Menu.NONE, 1, Menu.NONE, "View on WebView");
		menu.add(Menu.NONE, 2, Menu.NONE, "Share by other apps");
	}

	// When the users selected the item id in the context menu, it called specific item action.
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		News newsList = (News) getListAdapter().getItem(info.position);
		switch(item.getItemId()) {
			case 0:
				// Launch the instapaper request to post data
				InstapaperRequest request = new InstapaperRequest(this, mPrefs);
				request.sendData("https://www.instapaper.com/api/add?url=" + newsList.getLink());
				return true;
			case 1:
				// Launch Activity to view page load in webview
				final Intent displayWebView = new Intent(this, WebViewActivity.class);
				displayWebView.putExtra("url", newsList.getLink());
				startActivity(displayWebView);
				return true;
			case 2:
				final Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT, newsList.getLink());
				startActivity(Intent.createChooser(shareIntent, "Select an action"));
			default:
				return super.onContextItemSelected(item);
		}
	}
}