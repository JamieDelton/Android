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

import android.content.Context;
import android.database.*;
import android.database.sqlite.*;

/**
 * Simple class database access helper class. Defines the basic CRUD operations
 * for the college classes, and gives the ability to list all courses and program as well as
 * retrieve or modify a specific courses and program.
 */
public class ClassDbAdapter
{
	public static final String KEY_ROWID = "_id";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private static final String PROGRAM_CREATE = "create table program (_id integer primary key autoincrement, program_name text not null, program_code text not null, program_credit integer);";
	private static final String COURSES_CREATE = "create table course (_id integer primary key autoincrement, name text not null, code text not null, passed boolean, program_id integer, course_credit integer);";
	private static final String DATABASE_NAME = "data.db";
	private static final int DATABASE_VERSION = 1;
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(PROGRAM_CREATE);
			db.execSQL(COURSES_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
		}
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
	public ClassDbAdapter(Context ctx)
	{
		mDbHelper = new DatabaseHelper(ctx);
	}
	
	/**
	* Open the program and courses database. If it cannot be opened, try to create a new
	* instance of the database. If it cannot be created, throw an exception to
	* signal the failure
	* 
	* @return this (self reference, allowing this to be chained in an
	*         initialization call)
	* @throws SQLException if the database could be neither opened or created
	*/
	public ClassDbAdapter open() throws SQLException
	{
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close()
	{
		mDbHelper.close();
	}
	
	public void executeQuery (String query) {
		mDb.execSQL(query);
	}
	
	public Cursor rawQuery (String query) {
		Cursor c = mDb.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
	
	public SQLiteDatabase getDataBase() {
		return mDb;
	}
}