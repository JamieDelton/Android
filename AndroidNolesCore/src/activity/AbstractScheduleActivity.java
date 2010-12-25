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

import com.itnoles.shared.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*; // Menu and MenuItem
import android.util.Log;

import java.util.*; //List and ArrayList
import org.json.*; //JSONArray and JSONObject

import com.itnoles.shared.BetterBackgroundTask;
import com.itnoles.shared.adapter.ColorAdapter;
import com.itnoles.shared.helper.*; // BetterAsyncTaskCompleteListener and JSONHelper

public abstract class AbstractScheduleActivity extends ListActivity implements BetterAsyncTaskCompleteListener<Void, Void, JSONArray>
{
	private static final String TAG = "ScheduleActivity";
	private String url;

	public AbstractScheduleActivity(String url)
	{
		this.url = url;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_main);
		new BetterBackgroundTask<Void, Void, JSONArray>(this).execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.Stadium).setIcon(R.drawable.map);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				Intent i = new Intent(this, StadiumActivity.class);
				startActivity(i);
				return true;
			default:
				return true;
		}
	}
	
	// Display Data to ListView
	public void onTaskComplete(JSONArray json)
	{
		if (json == null)
			return;
		List<HashMap<String, String>> entries = new ArrayList<HashMap<String, String>>();
		try {
			for (int i=0; i < json.length(); i++) {
				JSONObject rec = json.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("date", rec.getString("date") + "     " + rec.getString("time"));
				map.put("school", rec.getString("school"));
				map.put("tv", rec.getString("tv"));
				entries.add(map);
			}
			setListAdapter(new ColorAdapter(this, entries));
		} catch (JSONException e) {
			Log.e(TAG, "bad json parsing", e);
		}
	}

	// Do This stuff in Background
	public JSONArray readData(Void... params)
	{
		return JSONHelper.getJSONArray(url);
	}
}