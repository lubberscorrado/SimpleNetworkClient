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
	private final Logger log;

	/**
	 * Creates new Html Cleaner parser.
	 * 
	 * @since SNC-EXT-HTMLCLEANER 1.0
	 */
	public ParserHtmlCleaner() {
		log = LoggerFactory.getLogger(ParserHtmlCleaner.class);
	}

	/**
	 * Creates Html Cleaner parser. No throwables to worry about.
	 * 
	 * @since SNC-EXT-HTMLCLEANER 1.0
	 */
	@Override
	public void init() {
		// no exceptions or errors to worry about
		cleaner = new HtmlCleaner();
	}

	/**
	 * Gets Html Cleaner inside of parser.
	 * 
	 * @return html cleaner or null if not initiated
	 * @since SNC-EXT-HTMLCLEANER 1.0
	 */
	public HtmlCleaner getHtmlCleaner() {
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
		if (cleaner == null) cleaner = new HtmlCleaner();
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
	 *            raw handler
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
		parse(inputStream, realHandler, charset);
	}
}
