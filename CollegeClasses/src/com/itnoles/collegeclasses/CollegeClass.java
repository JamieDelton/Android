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
package com.itnoles.collegeclasses;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter;

public class CollegeClass extends ListActivity
{
	private ClassDbAdapter mDbHelper;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mDbHelper = new ClassDbAdapter(this);
		mDbHelper.open();
		fillData();
		registerForContextMenu(getListView());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}
	
	private void fillData()
	{
		Cursor mProgramCursor = mDbHelper.getDataBase().rawQuery("SELECT _id, program_name, program_code FROM program", null);
		startManagingCursor(mProgramCursor);
		setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, mProgramCursor,
		new String[]{"program_name", "program_code"}, new int[] {android.R.id.text1, android.R.id.text2}));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(0, Menu.FIRST, 0, "Add Program");
		return true;
    }

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch(item.getItemId()) {
			case Menu.FIRST:
				Intent viewIntent = new Intent(this, AddProgramActivity.class);
				startActivityForResult(viewIntent, 0);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
    }
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		menu.setHeaderTitle("Select Options");
		menu.add(Menu.NONE, 0, Menu.NONE, "Edit Program");
		menu.add(Menu.NONE, 2, Menu.NONE, "Delete Program");
		menu.add(Menu.NONE, 3, Menu.NONE, "View Courses");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
			case 0:
				Intent i = new Intent(this, AddProgramActivity.class);
				i.putExtra(ClassDbAdapter.KEY_ROWID, info.id);
				startActivityForResult(i, 1);
			return true;
			case 2:
				mDbHelper.executeQuery("DELETE FROM program WHERE " + ClassDbAdapter.KEY_ROWID + "=" + info.id);
				mDbHelper.executeQuery("DELETE FROM course WHERE program_id = " + info.id);
				fillData();
			return true;
			case 3:
				Intent detail = new Intent(this, CourseDetailActivity.class);
				detail.putExtra(ClassDbAdapter.KEY_ROWID, info.id);
				startActivity(detail);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK)
			fillData();
	}
}