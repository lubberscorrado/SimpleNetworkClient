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

import static com.fasterxml.jackson.core.JsonToken.END_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.END_OBJECT;
import static com.fasterxml.jackson.core.JsonToken.START_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.START_OBJECT;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.krobothsoftware.commons.progress.ProgressMonitor;

/**
 * Handler for Json types. Part of <code>SNC-EXT-JSON</code>.
 * 
 * @author Kyle Kroboth
 * @since SNC-EXT-JSON 1.0
 * @see com.krobothsoftware.commons.parse.ParserJson
 */
public abstract class HandlerJson extends Handler {

	/**
	 * Current token being processed.
	 * 
	 * @since SNC-EXT-JSON 1.0
	 */
	protected JsonToken currentToken;

	/**
	 * Json Parser from Jackson's JSON library. Not to be confused with
	 * {@link ParserJson}.
	 * 
	 * @since SNC-EXT-JSON 1.0
	 */
	protected JsonParser jsonParser;

	/**
	 * Current name of token being processed.
	 * 
	 * @since SNC-EXT-JSON 1.0
	 */
	private String currentName;

	/**
	 * Creates new Json handler with monitor.
	 * 
	 * @param monitor
	 * @since SNC-EXT-JSON 1.0
	 */
	public HandlerJson(ProgressMonitor monitor) {
		super(monitor);
	}

	/**
	 * Creates new Json handler with no progress.
	 * 
	 * @since SNC-EXT-JSON 1.0
	 */
	public HandlerJson() {

	}

	/**
	 * Called when handler is parsed. Iterates through each {@link JsonToken}
	 * and handles them accordingly. If needed, override this method and handle
	 * tokens for handler.
	 * 
	 * <table border="1">
	 * <tr>
	 * <td>Start Object</td>
	 * <td>{@link #startObject(String)}</td>
	 * </tr>
	 * <tr>
	 * <td>End Object</td>
	 * <td>{@link #endObject(String)}</td>
	 * </tr>
	 * <tr>
	 * <td>Start Array</td>
	 * <td>{@link #startArray(String)}</td>
	 * </tr>
	 * <tr>
	 * <td>End Array</td>
	 * <td>{@link #endArray(String)}</td>
	 * </tr>
	 * <tr>
	 * <td>Token</td>
	 * <td>{@link #token(String)}</td>
	 * </tr>
	 * <tr>
	 * <td>Token Text</td>
	 * <td>{@link #tokenText(String)}. Only if token has text</td>
	 * </tr>
	 * </table>
	 * 
	 * @throws JsonParseException
	 * @throws IOException
	 * @throws ParseException
	 * @since SNC-EXT-JSON 1.0
	 */
	public void parse() throws JsonParseException, IOException, ParseException {
		while ((currentToken = jsonParser.nextToken()) != null) {
			if (currentToken == START_OBJECT) {
				startObject(jsonParser.getCurrentName());
				continue;
			} else if (currentToken == END_OBJECT) {
				endObject(jsonParser.getCurrentName());
				continue;
			} else if (currentToken == START_ARRAY) {
				startArray(jsonParser.getCurrentName());
				continue;
			} else if (currentToken == END_ARRAY) {
				endArray(jsonParser.getCurrentName());
				continue;
			}

			String name = jsonParser.getCurrentName();

			if (currentName != null && name != null && name.equals(currentName)) {
				tokenText(name);
			} else {
				token(jsonParser.getCurrentName());
			}
		}
	}

	/**
	 * Called when {@link JsonToken#START_OBJECT} is found.
	 * 
	 * @param name
	 *            name of token
	 * @throws ParseException
	 * @throws JsonParseException
	 * @throws IOException
	 * @since SNC-EXT-JSON 1.0
	 */
	protected void startObject(String name) throws ParseException,
			JsonParseException, IOException {

	}

	/**
	 * Called when {@link JsonToken#END_OBJECT} is found.
	 * 
	 * @param name
	 *            name of token
	 * @throws ParseException
	 * @throws JsonParseException
	 * @throws IOException
	 * @since SNC-EXT-JSON 1.0
	 */
	protected void endObject(String name) throws ParseException,
			JsonParseException, IOException {

	}

	/**
	 * Called when {@link JsonToken#START_ARRAY} is found.
	 * 
	 * @param name
	 *            name of token
	 * @throws ParseException
	 * @throws JsonParseException
	 * @throws IOException
	 * @since SNC-EXT-JSON 1.0
	 */
	protected void startArray(String name) {

	}

	/**
	 * Called when {@link JsonToken#END_ARRAY} is found.
	 * 
	 * @param name
	 *            name of token
	 * @throws ParseException
	 * @throws JsonParseException
	 * @throws IOException
	 * @since SNC-EXT-JSON 1.0
	 */
	protected void endArray(String name) {

	}

	/**
	 * Called when any other {@link JsonToken} is found.
	 * 
	 * @param name
	 * @throws ParseException
	 * @throws JsonParseException
	 * @throws IOException
	 * @since SNC-EXT-JSON 1.0
	 */
	protected void token(String name) throws ParseException, JsonParseException,
			IOException {
		currentName = name;
	}

	/**
	 * Only called when token has text with it.
	 * 
	 * @param name
	 * @throws ParseException
	 * @throws JsonParseException
	 * @throws IOException
	 * @since SNC-EXT-JSON 1.0
	 */
	protected void tokenText(String name) throws ParseException,
			JsonParseException, IOException {
		currentName = null;
	}

	void setJsonParser(JsonParser jsonParser) {
		this.jsonParser = jsonParser;
	}

}
