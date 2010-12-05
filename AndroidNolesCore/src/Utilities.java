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
import android.content.*; //Context and DialogInterface
import android.net.*; // ConnectivityManager and NetworkInfo

/**
 * Helper class
 * @author Jonathan Steele
 */
public class Utilities {
	/**
	 * NetworkCheck
	 * Checking to see Network is connected
	 * @param context Activity's Context
	 * @return true or false
	 */
	public static Boolean NetworkCheck(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null) ? (info.getState() == NetworkInfo.State.CONNECTED) : false;
	}
	
	/**
	 * show a new alert dialog
	 * @param context Activity's Context
	 * @param title alert dialog for title
	 * @param message alert dialog for message
	 */
	public static void showAlertDialog(Context context, String title, String message)
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		alertDialog.show();
	}
	
	/**
	 * show a new alert dialog
	 * @param context Activity's Context
	 * @param title alert dialog for title
	 * @param message Resource ID for alert dialog message
	 */
	
	public static void showAlertDialog(Context context, String title, int message)
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		alertDialog.show();
	}
	
	/**
	 * show a new progress dialog
	 * 
	 * @param activity Get a specific Activity
	 * @param title progress dialog for title
	 * @param message progress dialog for message
	 */
	public static ProgressDialog showProgressDialog(Context context, String message)
	{
		return ProgressDialog.show(context, "", message, true);
	}
}