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
package com.itnoles.nolesfootball;

import com.itnoles.shared.activity.AbstractScheduleActivity;

public class NolesScheduleActivity extends AbstractScheduleActivity
{
	public NolesScheduleActivity()
	{
		super("http://jonathan.theoks.net/appstuff/noles_schedule.json");
	}
}