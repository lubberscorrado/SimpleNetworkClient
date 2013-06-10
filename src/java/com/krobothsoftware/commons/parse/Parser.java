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

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parser is used to parse XML and HTML data. Xml is parsed by SAX and Html by
 * TagSoup(SAX). Supports more formats through {@link ParserHandler}.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public final class Parser implements ParserInitializable {
	private static final String SAXPARSER_TAGSOUP = "org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl";

	/**
	 * Null Parser Handler. Use this instead of <code>null</code> when setting
	 * handler.
	 * 
	 * @deprecated Now allowed to be null
	 * @since SNC 1.0
	 */
	@Deprecated
	public static final ParserHandler NULL_PARSER_HANDLER = null;

	final Logger log;
	ParserHandler listener;
	private SAXParser xmlParser;
	private SAXParser htmlParser;

	/**
	 * Instantiates a new parser.
	 * 
	 * @since SNC 1.0
	 */
	public Parser() {
		log = LoggerFactory.getLogger(Parser.class);
	}

	/**
	 * Tries to create Xml and Html parsers. Catches and logs following,
	 * 
	 * <ul>
	 * <li>FactoryConfigurationError</li>
	 * <li>ParserConfigurationException</li>
	 * <li>SAXException</li>
	 * </ul>
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public void init() {
		try {
			xmlParser = SAXParserFactory.newInstance().newSAXParser();
			htmlParser = SAXParserFactory.newInstance(
					"org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl",
					Parser.class.getClassLoader()).newSAXParser();
		} catch (FactoryConfigurationError e) {
			log.error("Init - " + e.getMessage());
		} catch (ParserConfigurationException e) {
			log.error("Init - " + e.getMessage());
		} catch (SAXException e) {
			log.error("Init - " + e.getMessage());
		}
	}

	/**
	 * Xml SAX parser.
	 * 
	 * @return xml parser or null if not initiated
	 * @since SNC 1.0
	 */
	public SAXParser getXmlParser() {
		if (xmlParser == null) log
				.warn("XmlParser has not been initiated, Call init() beforehand");
		return xmlParser;
	}

	/**
	 * Html SAX parser.
	 * 
	 * @return html parser or null if not initiated
	 * @since SNC 1.0
	 */
	public SAXParser getHtmlParser() {
		if (xmlParser == null) log
				.warn("HtmlParser  has not been initiated, Call init() beforehand");
		return htmlParser;
	}

	/**
	 * Sets parser handler for parsing handlers.
	 * 
	 * @param parserHandler
	 * @throws IllegalArgumentException
	 *             if listener is null
	 * @since SNC 1.0
	 */
	public void setParserHandler(ParserHandler parserHandler) {
		this.listener = parserHandler;
	}

	/**
	 * Parses inputstream for {@link Handler}. Stream is handled inside
	 * SAXParsers and should close after use.
	 * 
	 * @param inputStream
	 *            inputstream to be parsed
	 * @param handler
	 * @param charset
	 * @throws ParseException
	 * @since SNC 1.0
	 */
	public void parse(InputStream inputStream, Handler handler, String charset)
			throws ParseException {
		log.debug("Parsing {}", handler.getClass().getSimpleName());
		Handler realHandler = getHandler(handler);

		try {
			// SAX handler
			if (handler instanceof HandlerSAX) {
				SAXParser parser = getParser(handler);
				if (parser != null) {
					DefaultHandlerDelegate delegate = new DefaultHandlerDelegate(
							(HandlerSAX) realHandler);
					InputSource inputSource = new InputSource(inputStream);
					inputSource.setEncoding(charset);
					realHandler.setParser(this);
					realHandler.setLogger(log);
					parser.parse(inputSource, delegate);
					return;
				}
			}

			/**
			 * Pass unsupported handler to ParserHandler. If returns false,
			 * throw exception.
			 */
			boolean processed = false;
			if (listener != null) {
				processed = listener.parseHandler(inputStream, handler,
						realHandler, charset);
			}
			if (!processed) throw new ParseException(String.format(
					"Unsupported Handler [%s]", handler.getClass()));

		} catch (StopException e) {
			log.debug("Caught Stop Exception - {}", handler.getClass()
					.getSimpleName());
		} catch (SAXException e) {
			throw new ParseException(e);
		} catch (IOException e) {
			throw new ParseException(e);
		}
	}

	private SAXParser getParser(Handler handler) throws ParseException {
		try {
			if (handler instanceof HandlerXml) {
				if (xmlParser == null) {
					xmlParser = SAXParserFactory.newInstance().newSAXParser();
				}
				return xmlParser;
			} else if (handler instanceof HandlerHtml) {
				if (htmlParser == null) {
					htmlParser = SAXParserFactory.newInstance(
							SAXPARSER_TAGSOUP, Parser.class.getClassLoader())
							.newSAXParser();
				}
				return htmlParser;
			}
		} catch (ParserConfigurationException e) {
			throw new ParseException(e);
		} catch (SAXException e) {
			throw new ParseException(e);
		}

		return null;
	}

	private Handler getHandler(Handler handler) {
		Handler found = null;
		if (listener != null) found = listener.getHandler(handler);
		if (found == null) {
			if (handler instanceof HandlerSAX) {
				if (handler instanceof ExpressionBuilderFilter) found = new HandlerExpressionBuilder(
						(HandlerSAX) handler);
				else if (handler instanceof ExpressionFilter) found = new HandlerExpression(
						(HandlerSAX) handler);
				else
					found = handler;
			}
		}

		return found;
	}

}
