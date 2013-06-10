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
import java.net.URLConnection;

import com.krobothsoftware.commons.util.CommonUtils;

public class Response implements AutoCloseable {
	protected final HttpURLConnection conn;
	protected final UnclosableInputStream stream;
	protected final int status;
	protected final String charset;

	public Response(HttpURLConnection connection, UnclosableInputStream input,
			int status, String charset) {
		this.conn = connection;
		this.stream = input;
		this.status = status;
		this.charset = charset;
	}

	public HttpURLConnection getConnection() {
		return conn;
	}

	public UnclosableInputStream getStream() {
		return stream;
	}

	public int getStatusCode() {
		return status;
	}

	public boolean isSuccessful() {
		return status / 100 == 2;
	}

	public boolean isRedirection() {
		return status / 100 == 3;
	}

	public boolean isClientError() {
		return status / 100 == 4;
	}

	public boolean isServerError() {
		return status / 100 == 5;
	}

	public String getCharset() {
		return charset;
	}

	public int getContentLength() {
		return conn.getContentLength();
	}

	public long getContentLengthLong() {
		return Long.parseLong(conn.getHeaderField("Content-Length"));
	}

	public String getHeader(String name) {
		return conn.getHeaderField(name);
	}

	@Override
	public void close() throws IOException {
		conn.disconnect();
		stream.forceClose();
	}

	@Override
	public String toString() {
		return String.format("Response %s : %s", conn.getURL().toString(),
				String.valueOf(status));
	}

	public static String toString(Response response) throws IOException {
		return CommonUtils
				.toString(response.getStream(), response.getCharset());
	}
}
