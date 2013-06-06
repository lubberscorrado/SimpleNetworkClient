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

package com.krobothsoftware.commons.network;

import java.net.HttpURLConnection;

/**
 * Response holder from {@link RequestBuilder#execute(NetworkHelper)} if
 * response code is 401(Unauthorized).
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public class ResponseAuthenticate extends Response {
	private final String authenticate;

	/**
	 * Instantiates a new response with results from connection and retrieves
	 * header <code>WWW-Authenticate</code>.
	 * 
	 * @param connection
	 * @param input
	 *            non-null stream
	 * @param status
	 *            response code
	 * @param charset
	 *            charset of connection
	 * @since SNC 1.0
	 * 
	 */
	public ResponseAuthenticate(HttpURLConnection connection,
			UnclosableInputStream input, int status, String charset) {
		super(connection, input, status, charset);
		authenticate = connection.getHeaderField("WWW-Authenticate");
	}

	/**
	 * Gets header <code>WWW-Authenticate</code>.
	 * 
	 * @return authenticate header, or null if not found
	 * @since SNC 1.0
	 */
	public String getAuthentication() {
		return authenticate;
	}

}
