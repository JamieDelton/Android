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

public class News {
	private String mTitle;
	private String mLink;
	private String mPubdate;
	
	// getters and setters omitted for brevity
	public String getTitle() {
		return this.mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getLink() {
		return this.mLink;
	}

	public void setLink(String link) {
		this.mLink = link;
	}

	public void setPubdate(String pubdate) {
		this.mPubdate = pubdate;
	}
	
	public String getPubdate() {
		return this.mPubdate;
	}
}