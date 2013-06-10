/* ===================================================
 * Copyright 2013 Kroboth Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================== 
 */

package com.krobothsoftware.commons.parse;

import java.io.IOException;
import java.io.InputStream;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Html parser part of <code>SNC-EXT-HTMLCLEANER</code> which parses
 * {@link HandlerHtmlCleaner} handlers. The library used is Html-Cleaner.
 * 
 * @author Kyle Kroboth
 * @since SNC-EXT-HTMLCLEANER 1.0
 */
public final class ParserHtmlCleaner implements ParserInitializable {
	private HtmlCleaner cleaner;
	private final CleanerProperties properties;
	private final Logger log;

	/**
	 * Creates new Html Cleaner Parser and will use properties.
	 * 
	 * @param properties
	 *            for <code>HtmlCleaner</code> component
	 * @since SNC-EXT-HTMLCLEANER 1.0.1
	 */
	public ParserHtmlCleaner(CleanerProperties properties) {
		this.properties = properties;
		log = LoggerFactory.getLogger(ParserHtmlCleaner.class);
	}

	/**
	 * Creates new Html Cleaner parser.
	 * 
	 * @since SNC-EXT-HTMLCLEANER 1.0
	 */
	public ParserHtmlCleaner() {
		this(null);
	}

	/**
	 * Creates Html Cleaner parser. No exceptions to worry about.
	 * 
	 * @since SNC-EXT-HTMLCLEANER 1.0
	 */
	@Override
	public void init() {
		// no exceptions or errors to worry about
		cleaner = new HtmlCleaner(properties);
	}

	/**
	 * Gets Html Cleaner inside of parser.
	 * 
	 * @return html cleaner or null if not initiated
	 * @since SNC-EXT-HTMLCLEANER 1.0
	 */
	public HtmlCleaner getHtmlCleaner() {
		if (cleaner == null) log
				.warn("HtmlCleaner has not been initiated, Call init() beforehand");
		return cleaner;
	}

	/**
	 * Parses Html InputStream for handler. Inputstream is passed to Html
	 * Cleaner and should close after use.
	 * 
	 * @param inputStream
	 * @param handler
	 *            html cleaner hadnler
	 * @param charset
	 *            charset of data
	 * @throws ParseException
	 * @since SNC-EXT-HTMLCLEANER 1.0
	 */
	public void parse(InputStream inputStream, HandlerHtmlCleaner handler,
			String charset) throws ParseException {
		if (cleaner == null) cleaner = new HtmlCleaner(properties);
		try {
			handler.setLogger(log);
			handler.setHtmlCleaner(cleaner);
			handler.parse(cleaner.clean(inputStream, charset));
		} catch (IOException e) {
			throw new ParseException(e);
		}
	}

	/**
	 * Parses Html InputStream from
	 * {@link ParserHandler#parseHandler(InputStream, Handler, Handler, String) }
	 * .
	 * 
	 * @param inputStream
	 * @param handler
	 *            raw handler. Not needed.
	 * @param realHandler
	 *            real handler
	 * @param charset
	 *            charset of data
	 * @throws ParseException
	 * @since SNC-EXT-HTMLCLEANER 1.0
	 */
	public void parse(InputStream inputStream, Handler handler,
			HandlerHtmlCleaner realHandler, String charset)
			throws ParseException {
		// handler not used
		parse(inputStream, realHandler, charset);
	}
}
