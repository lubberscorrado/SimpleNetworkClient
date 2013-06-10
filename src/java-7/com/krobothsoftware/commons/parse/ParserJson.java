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
import com.fasterxml.jackson.core.JsonParser;

public final class ParserJson implements ParserInitializable {
	private JsonFactory factory;
	private final Logger log;

	public ParserJson() {
		log = LoggerFactory.getLogger(ParserJson.class);
	}

	@Override
	public void init() {
		// no errors or exceptions to worry about
		factory = new JsonFactory();
	}

	public JsonFactory getFactory() {
		return factory;
	}

	public void parse(InputStream inputStream, HandlerJson handler)
			throws ParseException {
		if (factory == null) factory = new JsonFactory();
		try (JsonParser parser = factory.createParser(inputStream)) {
			handler.setLogger(log);
			handler.setJsonParser(parser);
			handler.parse();
		} catch (IOException e) {
			throw new ParseException(e);
		}
	}

	public void parse(InputStream inputStream, Handler handler,
			HandlerJson realHandler) throws ParseException {
		// handler not used
		parse(inputStream, realHandler);
	}

}
