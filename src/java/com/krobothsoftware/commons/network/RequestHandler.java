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

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Request handler for getting RequestBuilder depending on status code.
 * 
 * <p>
 * Before <code>Response</code> is returned from
 * {@link RequestBuilder#execute(NetworkHelper)}, the connection may handle and
 * re-send a new <code>RequestBuilder</code> depending on status code.
 * </p>
 * 
 * <p>
 * Should not modify original <code>RequestBuilder</code>.
 * </p>
 * 
 * @author Kyle Kroboth
 * @since COMMONS 1.0
 * @see com.krobothsoftware.commons.network.RequestBuilder#setInternalHandler(int,
 *      RequestHandler)
 */
public interface RequestHandler {

	/**
	 * Handle request for status code. Must close connection.
	 * 
	 * @param status
	 *            response code
	 * @param builder
	 *            builder for handling
	 * @param connection
	 *            of builder, must close
	 * @return new request builder
	 * @throws IOException
	 * @since COMMONS 1.0
	 */
	RequestBuilder getRequest(int status, RequestBuilder builder,
			HttpURLConnection connection) throws IOException;

}
