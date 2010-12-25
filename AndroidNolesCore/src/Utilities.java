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
package com.itnoles.shared;

import android.app.*; // AlertDialog and ProgressDialog
import android.content.Context;
import android.os.Build;

import java.io.*; //InputStream and IOException
import java.net.*; //URL and URLConnection

/**
 * Helper class
 * @author Jonathan Steele
 */
public class Utilities {
	/**
	 * show a new alert dialog
	 * @param context Activity's Context
	 * @param title alert dialog for title
	 * @param message alert dialog for message
	 */
	public static void showAlertDialog(Context context, String title, String message)
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
		.setPositiveButton(android.R.string.ok, null);
		alertDialog.show();
	}
	
	/**
	 * show a new progress dialog
	 * @param activity Get a specific Activity
	 * @param title progress dialog for title
	 * @param message progress dialog for message
	 */
	public static ProgressDialog showProgressDialog(Context context, String message)
	{
		return ProgressDialog.show(context, "", message, true);
	}
	
	/**
	 * open a new input stream from URLConnection with User-Agent Header
	 * @param urlString string for URL address
	 * @return new inpit stream
	 * @throws IOException failed or interrupted I/O operations
	 */
	public static InputStream openStream(String urlString) throws IOException
	{
		URLConnection c = new URL(urlString).openConnection();
		c.setRequestProperty("User-Agent", "Android/" + Build.VERSION.RELEASE);
		return c.getInputStream();
	}
}