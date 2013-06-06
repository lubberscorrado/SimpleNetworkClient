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

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import com.krobothsoftware.commons.util.CommonUtils;

/**
 * Response holder from {@link RequestBuilder#execute(NetworkHelper)}. Make sure
 * to call {@link #close()}.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 * @see com.krobothsoftware.commons.network.RequestBuilder#execute(NetworkHelper)
 */
public class Response implements Closeable {
	private final HttpURLConnection conn;
	private final UnclosableInputStream stream;
	private final int status;
	private final String charset;

	/**
	 * Instantiates a new response with results from connection.
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
	public Response(HttpURLConnection connection, UnclosableInputStream input,
			int status, String charset) {
		this.conn = connection;
		this.stream = input;
		this.status = status;
		this.charset = charset;
	}

	/**
	 * Get response connection.
	 * 
	 * @return connection
	 * @since SNC 1.0
	 */
	public HttpURLConnection getConnection() {
		return conn;
	}

	/**
	 * Get response stream. May be InputStream or ErrorStream. Will never be
	 * null.
	 * 
	 * @return stream from connection
	 * @see UnclosableInputStream#forceClose()
	 * @since SNC 1.0
	 */
	public UnclosableInputStream getStream() {
		return stream;
	}

	/**
	 * Get response status code.
	 * 
	 * @return response code
	 * @since SNC 1.0
	 */
	public int getStatusCode() {
		return status;
	}

	/**
	 * Checks if status code is successful.
	 * 
	 * <p>
	 * <code>status >= 200 and status < 300</code>
	 * </p>
	 * 
	 * @return if status code is considered successful
	 * @deprecated changed method to {@link #isSuccessful()}
	 * @since SNC 1.0
	 */
	@Deprecated
	public boolean isOk() {
		return status / 100 == 2;
	}

	/**
	 * Checks if status code is successful.
	 * 
	 * <p>
	 * <code>status >= 200 and status < 300</code>
	 * </p>
	 * 
	 * @return true, if status code is considered successful
	 * @since SNC 1.0.2
	 */
	public boolean isSuccessful() {
		return status / 100 == 2;
	}

	/**
	 * 
	 * Checks if status code is a redirection.
	 * 
	 * <p>
	 * <code>status >= 300 and status < 400</code>
	 * </p>
	 * 
	 * @return true, if status code is considered a redirection
	 * @since SNC 1.0.2
	 */
	public boolean isRedirection() {
		return status / 100 == 3;
	}

	/**
	 * 
	 * Checks if status code is a client error.
	 * 
	 * <p>
	 * <code>status >= 400 and status < 500</code>
	 * </p>
	 * 
	 * @return true, if status code is considered a client error
	 * @since SNC 1.0.2
	 */
	public boolean isClientError() {
		return status / 100 == 4;
	}

	/**
	 * 
	 * Checks if status code is a server error.
	 * 
	 * <p>
	 * <code>status >= 500 and status < 600</code>
	 * </p>
	 * 
	 * @return true, if status code is considered a server error
	 * @since SNC 1.0.2
	 */
	public boolean isServerError() {
		return status / 100 == 5;
	}

	/**
	 * Get response charset.
	 * 
	 * @return charset from connection
	 * @since SNC 1.0
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * Get connection content Length. Same as calling
	 * {@link URLConnection#getContentLength()}.
	 * 
	 * @return connection content length
	 * @since SNC 1.0
	 */
	public int getContentLength() {
		return conn.getContentLength();
	}

	/**
	 * Helper method for getting {@link URLConnection} headers.
	 * 
	 * @param name
	 *            name of header
	 * @return header value, or null
	 * @since SNC 1.0
	 */
	public String getHeader(String name) {
		return conn.getHeaderField(name);
	}

	/**
	 * Disconnects connection and force closes inputstream in that order.
	 * {@link UnclosableInputStream#forceClose()}.
	 * 
	 * @throws IOException
	 * @since SNC 1.0
	 */
	@Override
	public void close() throws IOException {
		conn.disconnect();
		stream.forceClose();
	}

	/**
	 * Returns string in format "[url] : [status-code]".
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public String toString() {
		return String.format("%s : %s", conn.getURL().toString(),
				String.valueOf(status));
	}

	/**
	 * Helper method for getting string from Response.
	 * 
	 * @param response
	 * @return String from stream
	 * @throws IOException
	 * @see CommonUtils#toString(java.io.InputStream, String)
	 * @since SNC 1.0
	 */
	public static String toString(Response response) throws IOException {
		return CommonUtils
				.toString(response.getStream(), response.getCharset());
	}

	/**
	 * Quietly close response and ignores any exceptions.
	 * 
	 * @param response
	 *            to be closed
	 * @deprecated Use {@link CommonUtils#closeQuietly(Closeable)}.
	 *             <code>Response</code> implements <code>Closeable</code>.
	 * @since SNC 1.0
	 */
	@Deprecated
	public static void closeQuietly(Response response) {
		CommonUtils.closeQuietly(response);
	}
}
