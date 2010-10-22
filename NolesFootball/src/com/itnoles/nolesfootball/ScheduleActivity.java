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

import java.util.*;
import org.json.*;

import com.itnoles.shared.*;
import com.itnoles.shared.helper.*;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class ScheduleActivity extends ListActivity implements AsyncTaskCompleteListener
{
	private JSONArray json;

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
					startActivity(new Intent(ScheduleActivity.this, StadiumActivity.class));
				}
			});	
		}
		new BackgroundTask(this).execute();
	}

	// Display Data to ListView
	public void onTaskComplete()
	{
		if (json != null)
		{
			List<HashMap<String, String>> entries = new ArrayList<HashMap<String, String>>();
			try {
				for (Object o : json) {
					JSONObject rec = (JSONObject) o;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("date", rec.getString("date") + "     " + rec.getString("time"));
					map.put("school", rec.getString("school"));
					map.put("tv", rec.getString("tv"));
					entries.add(map);
				}
				setListAdapter(new ColorAdapter(entries));
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	// Before Display the data
	public void preReadData(){}
	
	// Do This stuff in Background
	public void readData()
	{
		json = JSONHelper.getJSONArray("http://jonathan.theoks.net/appstuff/noles_schedule.json");
	}
	
	private class ColorAdapter extends SimpleAdapter
	{
		private int[] colors = new int[] { 0xFFFFFFFF, 0xFFD1D1D1 };
		public ColorAdapter(List<HashMap<String, String>> items)
		{
			super(ScheduleActivity.this, items, R.layout.schedule, new String[] {"date", "school", "tv"}, new int[] {R.id.date, R.id.school, R.id.tv});
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = super.getView(position, convertView, parent);
			int colorPos = position % colors.length;
			view.setBackgroundColor(colors[colorPos]);
			return view;
		}
	}
}