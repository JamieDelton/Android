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

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.util.Log;

import java.util.*; // List and ArrayList
import org.json.*; // JSONArray and JSONObject

import com.itnoles.shared.helper.AsyncTaskCompleteListener;

public abstract class AbstractStaffActivity extends ListActivity implements AsyncTaskCompleteListener
{
	private static final String TAG = "AbstractStaffActivity";
	private JSONArray json;
	private String url;
	
	public AbstractStaffActivity(String url)
	{
		this.url = url;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		new com.itnoles.shared.BackgroundTask(this).execute();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		com.itnoles.shared.HttpUtils.closeConnection();
	}
	
	// Display Data to ListView
	public void onTaskComplete()
	{
		if (json == null)
			return;
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			for (int i=0; i < json.length(); i++) {
				JSONObject rec = json.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("name", rec.getString("name"));
				map.put("position", rec.getString("positions"));
				list.add(map);
			}
			setListAdapter(new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
			new String[] {"name", "position"}, new int[] {android.R.id.text1, android.R.id.text2}));
		} catch (JSONException e) {
			Log.e(TAG, "bad json parsing", e);
		}
	}
	
	// Do This stuff in Background
	public void readData()
	{
		json = com.itnoles.shared.helper.JSONHelper.getJSONArray(url);
	}
}