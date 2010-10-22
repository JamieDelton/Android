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

import android.content.Context;
import android.net.*;
import android.widget.Toast;

/**
 * Helper class
 * @author Jonathan Steele
 */

public final class Utilities {
	/**
	 * NetworkCheck
	 * Checking to see Network is connected
	 * @param context Activity's Context
	 * @return true or false
	 */
	public static Boolean NetworkCheck(Context context)
	{
		final ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null) ? (info.getState() == NetworkInfo.State.CONNECTED) : false;
	}
	
	/**
	 * showToast 
	 * it is showing Toast notification with a long peroid of time
	 * @param context Activity's Context
	 * @param message string that show up in the box
	 */
	public static void showToast(Context context, String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * showToast 
	 * it is showing Toast notification with a long peroid of time
	 * @param context Activity's Context
	 * @param message resources string that show up in the box
	 */
	public static void showToast(Context context, int message)
	{
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
}