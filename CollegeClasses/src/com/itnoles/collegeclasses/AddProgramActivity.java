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

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.*;
import android.text.InputFilter.LengthFilter;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.*;

public class AddProgramActivity extends Activity
{
	private ClassDbAdapter mDbHelper;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addprogram);
		mDbHelper = new ClassDbAdapter(this);
		mDbHelper.open();
		
		TextView mNameText = (TextView) findViewById(R.id.nametext);
		mNameText.setText("Program Name");
		
		TextView mCodeText = (TextView) findViewById(R.id.codetext);
		mCodeText.setText("Program Code");
		
		TextView mCreditText = (TextView) findViewById(R.id.credittext);
		mCreditText.setText("Program Credit");
		
		final EditText mNameEdit = (EditText) findViewById(R.id.nameedit);
		final EditText mCodeEdit = (EditText) findViewById(R.id.codeedit);
		// All Program Code Text for Edit Text has a max length is Four
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(4);
		mCodeEdit.setFilters(FilterArray);
		// All Program Code Text for Edit Text to all caps.
		mCodeEdit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
		
		// Numbers only in Program Credit for Edit Text
		final EditText mCreditEdit = (EditText) findViewById(R.id.creditedit);
		mCreditEdit.setKeyListener(new NumberKeyListener(){
			@Override
			public int getInputType() {
				return InputType.TYPE_NUMBER_FLAG_SIGNED;
			}

		   @Override
		   protected char[] getAcceptedChars() {
		      return "0123456789".toCharArray();
		   }
		});
		
		Bundle extras = getIntent().getExtras();            
		final Long mRowId = extras != null ? extras.getLong(ClassDbAdapter.KEY_ROWID) : null;
		if (mRowId != null)
		{
			Cursor note = mDbHelper.rawQuery("SELECT * FROM program WHERE " + ClassDbAdapter.KEY_ROWID + "= '" + mRowId + "'");
			startManagingCursor(note);
			mNameEdit.setText(note.getString(note.getColumnIndex("program_name")));
			mCodeEdit.setText(note.getString(note.getColumnIndex("program_code")));
			mCreditEdit.setText(new Integer(note.getInt(note.getColumnIndex("program_credit"))).toString());
		}
		
		final Button saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				String name = mNameEdit.getText().toString();
				String code = mCodeEdit.getText().toString();
				int credit = new Integer(mCreditEdit.getText().toString());

				if (mRowId == null)
				{
					mDbHelper.executeQuery("INSERT INTO program (program_name, program_code, program_credit) VALUES ('"+ name + "', '"+ code + "', '" + credit + "')");
				} else {
					mDbHelper.executeQuery("UPDATE program SET program_name='"+name+"', program_code='"+code+"', program_credit='"+credit+"' WHERE " + ClassDbAdapter.KEY_ROWID + "='" + mRowId + "'");
				}
				setResult(RESULT_OK);
				finish();
			}
		});
		
		final Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}
}