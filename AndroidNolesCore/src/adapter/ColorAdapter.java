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
package com.itnoles.shared.adapter;

import com.itnoles.shared.R;

import android.content.Context;
import android.view.*; //View and ViewGroup
import android.widget.SimpleAdapter;

import java.util.*; //List and HashMap;

public class ColorAdapter extends SimpleAdapter
{
	private int[] colors = new int[] { 0xFFFFFFFF, 0xFFD1D1D1 };
	public ColorAdapter(Context context, List<HashMap<String, String>> items)
	{
		super(context, items, R.layout.schedule, new String[] {"date", "school", "tv"}, new int[] {R.id.date, R.id.school, R.id.tv});
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