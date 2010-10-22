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
package com.itnoles.shared.helper;

import java.util.List;

import com.itnoles.shared.News;

import android.content.Context;
import android.view.*;
import android.widget.*;

/**
 * NewsAdapter
 * bind News and ArrayAdapter together
 * @author Jonathan Steele
 */

public class NewsAdapter extends ArrayAdapter<News> {
	private List<News> items;
	
	// Constructor
	public NewsAdapter(Context context, List<News> items) {
		super(context, android.R.layout.simple_list_item_2, items);
		this.items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(android.R.layout.simple_list_item_2, null);
		}
		
		News news = items.get(position);
		if (news != null) {
			TextView title = (TextView) v.findViewById(android.R.id.text1);
			if (title != null) {
				title.setText(news.getTitle());
			}
			TextView subtitle = (TextView) v.findViewById(android.R.id.text2);
			if (subtitle != null) {
				subtitle.setText(news.getPubdate());
			}
		}
		return v;
	}
}