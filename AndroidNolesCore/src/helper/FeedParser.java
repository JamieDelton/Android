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
package com.itnoles.shared.helper;

import org.xml.sax.*; // Attributes, SAXException, XMLReader and InputSource
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import java.io.InputStream;
import java.text.*; //SimpleDateFormat and ParseException
import java.util.*; //ArrayList and List

import javax.xml.parsers.SAXParserFactory;

import com.itnoles.shared.*; // News and Utilities

/**
 * FeedParser
 * class that parse Atom and RSS feeds
 * @author Jonathan Steele
 */

public class FeedParser
{
	private static final String TAG = "FeedParser";

	public static List<News> parse(String urlString)
	{
		RssHandler handler = new RssHandler();
		InputStream inputStream = null;
		try {
			// Get a SAXParser from the SAXPArserFactory.
			SAXParserFactory factory = SAXParserFactory.newInstance();

			// Get XMLParser from the SAXParser
			XMLReader xr = factory.newSAXParser().getXMLReader();
			xr.setContentHandler(handler);
			
			inputStream = Utilities.openStream(urlString);
			// Parse the xml-data from InputStream.
			xr.parse(new InputSource(inputStream));
		} catch (Exception e) {
			Log.e(TAG, "bad feed parsing", e);
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				Log.e(TAG, "can't close inputstream", e);
			}
		}
		// Parsing has finished.
		// Our handler now provides the parsed data to us.
		return handler.getMessages();
	}
	
	private static class RssHandler extends DefaultHandler {
		// names of the XML tags
		private static final String PUB_DATE = "pubDate";
		private static final String LINK = "link";
		private static final String TITLE = "title";
		private static final String ITEM = "item";
		private static final String ENTRY = "entry";
		private static final String PUBLISHED = "published";
		
		// Common Atom Format
		protected static final SimpleDateFormat ISO8601_DATE_FORMATS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		private static final List<News> messages = new ArrayList<News>();
		private News currentMessage;
		private StringBuilder builder;
		private String mHrefAttribute; // href attribute from link element in Atom format
	
		public List<News> getMessages()
		{
			return messages;
		}
		
		@Override
		public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
		{
			super.startElement(uri, localName, name, attributes);
			builder = new StringBuilder();
			if (localName.equalsIgnoreCase(ITEM) || localName.equalsIgnoreCase(ENTRY))
				currentMessage = new News();
			else if (localName.equalsIgnoreCase("enclosure")) {
				if (attributes != null)
					currentMessage.setImageURL(attributes.getValue("url"));
			} else if (localName.equalsIgnoreCase("link")) {
				// Get href attribute from link element for Atom format
				if (attributes != null)
					mHrefAttribute = attributes.getValue("href");
			}
		}

		@Override
		public void endElement(String uri, String localName, String name) throws SAXException
		{
			super.endElement(uri, localName, name);
			if (currentMessage != null)
			{
				if (localName.equalsIgnoreCase(TITLE))
					currentMessage.setTitle(builder.toString().trim());
				else if (localName.equalsIgnoreCase(LINK)) {
					if (mHrefAttribute != null)
						currentMessage.setLink(mHrefAttribute);
					else
						currentMessage.setLink(builder.toString().trim());
				}
				else if (localName.equalsIgnoreCase(PUB_DATE))
					currentMessage.setPubdate(builder.toString().trim());
				else if (localName.equalsIgnoreCase(PUBLISHED))
					setDate(ISO8601_DATE_FORMATS);
				else if (localName.equalsIgnoreCase(ITEM) || localName.equalsIgnoreCase(ENTRY))
					messages.add(currentMessage);
				
				// Reset the String Builder to Zero
				builder.setLength(0);
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			super.characters(ch, start, length);
			builder.append(ch, start, length);
		}
		
		private void setDate(SimpleDateFormat sdf)
		{
			try {
				currentMessage.setPubdate(sdf.parse(builder.toString().trim()).toString());
			} catch (ParseException e) {
				Log.e(TAG, "bad date format", e);
			}
		}
	}
}