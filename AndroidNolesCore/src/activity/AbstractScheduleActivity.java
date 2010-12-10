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
import android.view.View;
import android.widget.Button;
import android.util.Log;

import java.util.*; //List and ArrayList
import org.json.*; //JSONArray and JSONObject

import com.itnoles.shared.helper.*; // AsyncTaskCompleteListener and JSONHelper

public abstract class AbstractScheduleActivity extends ListActivity implements AsyncTaskCompleteListener
{
	private static final String TAG = "ScheduleActivity";
	private JSONArray json;
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
		final Button stadiumButton = (Button)this.findViewById(R.id.stadiumButton);
		if (stadiumButton != null) {
			stadiumButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// Perform action on click
					startActivity(new Intent(AbstractScheduleActivity.this, StadiumActivity.class));
				}
			});
		}
		new com.itnoles.shared.BackgroundTask(this).execute();
	}
	
	// Display Data to ListView
	public void onTaskComplete()
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
			setListAdapter(new com.itnoles.shared.adapter.ColorAdapter(this, entries));
		} catch (JSONException e) {
			Log.e(TAG, "bad json parsing", e);
		}
	}

	// Do This stuff in Background
	public void readData()
	{
		json = JSONHelper.getJSONArray(url);
	}
}