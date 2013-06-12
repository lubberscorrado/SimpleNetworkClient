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

import java.io.InputStream;

/**
 * Parser Listener for handling, and parsing unsupported {@link Handler}'s.
 * 
 * @author Kyle Kroboth
 * @since COMMONS 1.0
 */
public interface ParserHandler {

	/**
	 * Process and return new handler. Example, internally, any
	 * {@link HandlerSAX} which implements {@link ExpressionFilter} is wrapped
	 * inside a <code>HandlerExpression</code>. The wrapped handler is the real
	 * handler.
	 * 
	 * @param handler
	 * @return real handler or null if not processed
	 * @since COMMONS 1.0
	 */
	Handler getHandler(Handler handler);

	/**
	 * Any unsupported handler is passed here to parse.
	 * 
	 * @param inputStream
	 * @param handler
	 *            raw handler
	 * @param realHandler
	 *            real handler
	 * @param charset
	 * @return true, if handler was processed.
	 * @throws ParseException
	 * @since COMMONS 1.0
	 */
	boolean parseHandler(InputStream inputStream, Handler handler,
			Handler realHandler, String charset) throws ParseException;

}
