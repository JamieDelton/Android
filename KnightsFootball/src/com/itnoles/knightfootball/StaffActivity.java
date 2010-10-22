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

import com.itnoles.shared.BackgroundTask;
import com.itnoles.shared.helper.*;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;

public class StaffActivity extends ListActivity implements AsyncTaskCompleteListener
{
	private JSONArray json;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		new BackgroundTask(this).execute();
	}
	
	// Display Data to ListView
	public void onTaskComplete()
	{
		if (json != null)
		{
			List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			try {
				for (Object o : json) {
					JSONObject rec = (JSONObject) o;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("name", rec.getString("name"));
					map.put("position", rec.getString("positions"));
					list.add(map);
				}
				setListAdapter(new SimpleAdapter(StaffActivity.this, list, android.R.layout.simple_list_item_2,
				new String[] {"name", "position"}, new int[] {android.R.id.text1, android.R.id.text2}));
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
		json = JSONHelper.getJSONArray("http://jonathan.theoks.net/appstuff/knights_staff.json");
	}
}