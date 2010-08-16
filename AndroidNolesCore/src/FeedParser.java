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

import java.net.*;
import java.text.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;

public class FeedParser {
	// RSS Format
	protected static final SimpleDateFormat RFC822_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
	
	// Common Atom Format
	protected static final SimpleDateFormat ISO8601_DATE_FORMATS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	public List<News> parse(String urlString) {
		try {
			// Get a SAXParser from the SAXPArserFactory.
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// Get a XMLParser from the SAXParser
			XMLReader xr = factory.newSAXParser().getXMLReader();
			RssHandler handler = new RssHandler();
			xr.setContentHandler(handler);
			// Parse the xml-data from our URL.
			xr.parse(new InputSource(new URL(urlString).openStream()));
			// Parsing has finished.
			// Our handler now provides the parsed data to us.
			return handler.getMessages();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static class RssHandler extends DefaultHandler {
		// names of the XML tags
		private static final String PUB_DATE = "pubDate";
		private static final String LINK = "link";
		private static final String TITLE = "title";
		private static final String ITEM = "item";
		private static final String ENTRY = "entry";
		private static final String PUBLISHED = "published";

		private List<News> messages;
		private News currentMessage;
		private StringBuilder builder;
		private String mHrefAttribute; // href attribute from link element in Atom format
	
		public List<News> getMessages(){
			return this.messages;
		}

		public void startDocument() throws SAXException {
			super.startDocument();
			messages = new ArrayList<News>();
		}
		
		public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
			super.startElement(uri, localName, name, attributes);
			builder = new StringBuilder();
			if (localName.equalsIgnoreCase(ITEM) || localName.equalsIgnoreCase(ENTRY)) {
				this.currentMessage = new News();
			} else if (localName.equalsIgnoreCase("link")) {
				// Get href attribute from link element for Atom format
				if (attributes != null)
					mHrefAttribute = attributes.getValue("href");
			}
		}

		public void endElement(String uri, String localName, String name) throws SAXException {
			super.endElement(uri, localName, name);
			if (this.currentMessage != null){
				if (localName.equalsIgnoreCase(TITLE)){
					currentMessage.setTitle(builder.toString().trim());
				} else if (localName.equalsIgnoreCase(LINK)){
					if (mHrefAttribute != null) {
						currentMessage.setLink(mHrefAttribute);
					} else {
						currentMessage.setLink(builder.toString().trim());
					}
				} else if (localName.equalsIgnoreCase(PUB_DATE)){
					try {
						currentMessage.setPubdate(RFC822_DATE_FORMAT.parse(builder.toString().trim()).toString());
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
				} else if (localName.equalsIgnoreCase(PUBLISHED)) {
					try {
						currentMessage.setPubdate(ISO8601_DATE_FORMATS.parse(builder.toString().trim()).toString());
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
				} else if (localName.equalsIgnoreCase(ITEM) || localName.equalsIgnoreCase(ENTRY)){
					messages.add(currentMessage);
				}
				builder.setLength(0);
			}
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			super.characters(ch, start, length);
			builder.append(ch, start, length);
		}
	}
}