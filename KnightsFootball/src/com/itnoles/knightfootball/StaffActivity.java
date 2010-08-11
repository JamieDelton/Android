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

import com.itnoles.shared.JSONHelper;

import android.app.ListActivity;
import android.os.*;
import android.widget.SimpleAdapter;

public class StaffActivity extends ListActivity {
	private class StaffLoadingTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPostExecute(Void result) {
			if (json != null) {
				List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				try {
					JSONArray lineItems = (JSONArray)json.get("staff");
					for (int i = 0; i < lineItems.length(); ++i) {
						HashMap<String, String> map = new HashMap<String, String>();
						JSONObject rec = lineItems.getJSONObject(i);
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
		
		@Override
		protected Void doInBackground(Void... arg0) {
			JSONHelper jsonHelper = new JSONHelper("staff.json", StaffActivity.this);
			json = jsonHelper.getJSONObject();
			return null;
		}
	}

	private JSONObject json;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new StaffLoadingTask().execute();
	}
}