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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.krobothsoftware.commons.util.CommonUtils;

/**
 * Json Parser part of <code>COMMONS-EXT-JSON</code> which parses
 * {@link HandlerJson} handlers. The Json library used to parse is Jackson JSON
 * Parse. Not to be confused with the class name <i>JsonParser</i>.
 * 
 * @author Kyle Kroboth
 * @since COMMONS-EXT-JSON 1.0
 */
public final class ParserJson implements ParserInitializable {
	private JsonFactory factory;
	private final Logger log;

	/**
	 * Creates new Parser Json.
	 * 
	 * @since COMMONS-EXT-JSON 1.0
	 */
	public ParserJson() {
		log = LoggerFactory.getLogger(ParserJson.class);
	}

	/**
	 * Creates Json Factory. No throwables to worry about.
	 * 
	 * @since COMMONS-EXT-JSON 1.0
	 */
	@Override
	public void init() {
		// no errors or exceptions to worry about
		factory = new JsonFactory();
	}

	/**
	 * Gets Factory for json created inside the parser.
	 * 
	 * @return json factory or null if not initiated
	 * @since COMMONS-EXT-JSON 1.0
	 */
	public JsonFactory getFactory() {
		return factory;
	}

	/**
	 * Parses Json Inputstream for handler. Stream is passed to Jackson parser
	 * and should close after use.
	 * 
	 * @param inputStream
	 * @param handler
	 * @throws ParseException
	 * @since COMMONS-EXT-JSON 1.0
	 */
	@SuppressWarnings("resource")
	public void parse(InputStream inputStream, HandlerJson handler)
			throws ParseException {
		if (factory == null) factory = new JsonFactory();
		JsonParser parser = null;
		try {
			parser = factory.createParser(inputStream);
			handler.setLogger(log);
			handler.setJsonParser(parser);
			handler.parse();
		} catch (JsonParseException e) {
			throw new ParseException(e);
		} catch (IOException e) {
			throw new ParseException(e);
		} finally {
			CommonUtils.closeQuietly(parser);
		}
	}

	/**
	 * Parses Json Inputstream from
	 * {@link ParserHandler#parseHandler(InputStream, Handler, Handler, String) }
	 * . Charset isn't used here because it isn't needed.
	 * 
	 * @param inputStream
	 * @param handler
	 *            raw handler. Not needed.
	 * @param realHandler
	 *            real handler
	 * @throws ParseException
	 * @since COMMONS-EXT-JSON 1.0
	 */
	public void parse(InputStream inputStream, Handler handler,
			HandlerJson realHandler) throws ParseException {
		// handler not used
		parse(inputStream, realHandler);
	}

}
