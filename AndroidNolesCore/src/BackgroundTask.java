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

import android.os.AsyncTask;

import com.itnoles.shared.helper.AsyncTaskCompleteListener;

/**
 * BackgroundTask
 * it is the class has a delegate for AsyncTask class.
 * @author Jonathan Steele
 */

public class BackgroundTask extends AsyncTask<Void, Void, Void> {
	private AsyncTaskCompleteListener callback;

	// Constructor
	public BackgroundTask(AsyncTaskCompleteListener cb) {
		this.callback = cb;
	}
	
	// Runs on the UI thread after doInBackground
	@Override
	protected void onPostExecute(Void result) {
		callback.onTaskComplete();
	}
	
	// perform a computation on a background thread
	@Override
	protected Void doInBackground(Void... arg0) {
		callback.readData();
		return null;
	}
}