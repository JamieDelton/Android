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
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.*;

public class CourseDetailActivity extends ListActivity
{
	private ClassDbAdapter mDbHelper;
	private Long mRowId;
	private Button creditButton;
	private RelativeLayout layout;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_main);
		mRowId = getIntent().getExtras().getLong(ClassDbAdapter.KEY_ROWID);
		
		layout = (RelativeLayout) findViewById(R.id.Button1Layout);
		if (layout != null)
			layout.setVisibility(View.INVISIBLE);
		
		creditButton = (Button) findViewById(R.id.Button1);
		creditButton.setVisibility(View.INVISIBLE);

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
		Cursor mCourseCursor = mDbHelper.rawQuery("SELECT p.program_name, p.program_credit, c._id, c.name, c.code, c.passed, (SELECT SUM(course_credit) FROM course WHERE passed=1) AS passed_credit FROM program p, course c WHERE program_id='" + mRowId + "'");
		startManagingCursor(mCourseCursor);
		if (mCourseCursor.getCount() > 0) {
			int program_credit = mCourseCursor.getInt(mCourseCursor.getColumnIndex("program_credit"));
			int passed_credit = mCourseCursor.getInt(mCourseCursor.getColumnIndex("passed_credit"));
			
			layout.setVisibility(View.VISIBLE);

			creditButton.setText("Credit Needed " + new Integer(program_credit - passed_credit).toString());
			creditButton.setVisibility(View.VISIBLE);

			SeparatedListAdapter adapter = new SeparatedListAdapter(this);
			adapter.addSection(mCourseCursor.getString(mCourseCursor.getColumnIndex("program_name")), new ImageCursorAdapter(mCourseCursor));
			setListAdapter(adapter);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(0, Menu.FIRST, 0, "Add Course");
		return true;
    }

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch(item.getItemId()) {
			case Menu.FIRST:
				Intent viewIntent = new Intent(this, AddCourseActivity.class);
				viewIntent.putExtra("programid", mRowId);
				startActivityForResult(viewIntent, 0);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
    }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		menu.setHeaderTitle("Select Options");
		menu.add(Menu.NONE, 0, Menu.NONE, "Edit Course");
		menu.add(Menu.NONE, 2, Menu.NONE, "Delete Course");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
			case 0:
				Intent i = new Intent(this, AddCourseActivity.class);
				i.putExtra(ClassDbAdapter.KEY_ROWID, info.id);
				i.putExtra("programid", mRowId);
				startActivityForResult(i, 1);
			return true;
			case 2:
				mDbHelper.executeQuery("DELETE FROM course WHERE program_id = " + info.id);
				fillData();
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
	
	private class ImageCursorAdapter extends SimpleCursorAdapter
	{
		public ImageCursorAdapter(Cursor c) {
			super(CourseDetailActivity.this, R.layout.course_detail_item, c, new String[]{"name", "code"}, new int[] {android.R.id.text1, android.R.id.text2});
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			if (cursor.getInt(cursor.getColumnIndex("passed")) != 0) {
				ImageView imageView = (ImageView) view.findViewById(R.id.checkmark);
				imageView.setImageResource(R.drawable.checkbox_on_background);
			}
			super.bindView(view, context, cursor);
		}
	}
}