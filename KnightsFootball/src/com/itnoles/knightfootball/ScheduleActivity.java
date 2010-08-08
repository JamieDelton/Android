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

import java.util.*;
import org.json.*;

import android.app.ListActivity;
import android.content.Intent;
import android.os.*;
import android.view.*;
import android.widget.*;

public class ScheduleActivity extends ListActivity {
	private class ScheduleLoadingTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPostExecute(Void result) {
			if (json != null) {
				List<HashMap<String, String>> staffEntries = new ArrayList<HashMap<String, String>>();
				try {
					JSONArray lineItems = (JSONArray)json.get("schedule");
					for (int i = 0; i < lineItems.length(); ++i) {
						HashMap<String, String> map = new HashMap<String, String>();
						JSONObject rec = lineItems.getJSONObject(i);
						map.put("date", rec.getString("date") + "     " + rec.getString("time"));
						map.put("school", rec.getString("school"));
						map.put("tv", rec.getString("tv"));
						staffEntries.add(map);
					}
					setListAdapter(new ColorAdapter(staffEntries));
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			JSONHelper jsonHelper = new JSONHelper("schedule.json", ScheduleActivity.this);
			json = jsonHelper.getJSONObject();
			return null;
		}
	}
	
	private JSONObject json;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_main);
		final Button stadiumButton = (Button)this.findViewById(R.id.stadiumButton);
		stadiumButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				startActivity(new Intent(ScheduleActivity.this, StadiumActivity.class));
			}
		});
		new ScheduleLoadingTask().execute();
	}

	private class ColorAdapter extends SimpleAdapter {
		private int[] colors = new int[] { 0xFFFFFFFF, 0xFFD1D1D1 };
		public ColorAdapter(List<HashMap<String, String>> items) {
			super(ScheduleActivity.this, items, R.layout.schedule, new String[] {"date", "school", "tv"}, new int[] {R.id.date, R.id.school, R.id.tv});
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			int colorPos = position % colors.length;
			view.setBackgroundColor(colors[colorPos]);
			return view;
		}
	}
}