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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.util.SparseArray;

import com.krobothsoftware.commons.network.RequestBuilderRedirect.RedirectHandler;
import com.krobothsoftware.commons.network.value.Cookie;
import com.krobothsoftware.commons.network.value.CookieMap;
import com.krobothsoftware.commons.network.value.NameValuePair;

public class RequestBuilder {
	private static final SparseArray<RequestHandler> internalCodes;

	protected URL url;

	protected Method method;

	protected Logger log;

	protected Proxy proxy;

	protected final Set<Integer> ignoreCodes;

	protected int connectTimeout = -1;

	protected int readTimeout = -1;

	protected boolean followRedirects = false;

	protected boolean close;

	protected boolean cache = true;

	protected final Map<String, String> headerMap;

	protected List<Cookie> cookies;

	protected CookieMap useCookies;

	protected boolean storeCookies = true;

	protected boolean reqCookies = true;

	protected byte[] payload;

	public static void setInternalHandler(int responseCode,
			RequestHandler handler) {
		if (handler == null) internalCodes.remove(responseCode);
		else
			internalCodes.put(responseCode, handler);
	}

	public RequestBuilder(Method method, URL url) {
		this.method = method;
		this.url = url;
		headerMap = new HashMap<String, String>();
		ignoreCodes = new HashSet<Integer>();
		log = LoggerFactory.getLogger(RequestBuilder.class);
	}

	public RequestBuilder(RequestBuilder builder) {
		this(builder, builder.method, builder.url);
	}

	public RequestBuilder(RequestBuilder builder, Method method, URL url) {
		this.method = method;
		this.url = url;
		log = builder.log;
		proxy = builder.proxy;
		ignoreCodes = builder.ignoreCodes;
		connectTimeout = builder.connectTimeout;
		readTimeout = builder.readTimeout;
		followRedirects = builder.followRedirects;
		headerMap = builder.headerMap;
		cookies = builder.cookies;
		useCookies = builder.useCookies;
		payload = builder.payload;
	}

	public void reset() {
		proxy = null;
		ignoreCodes.clear();
		connectTimeout = -1;
		readTimeout = -1;
		followRedirects = false;
		close = false;
		cache = true;
		headerMap.clear();
		if (cookies != null) cookies.clear();
		useCookies = null;
		storeCookies = true;
		reqCookies = true;
		payload = null;
	}

	public URL getUrl() {
		return url;
	}

	public RequestBuilder url(URL url) {
		this.url = url;
		return this;
	}

	public Method getMethod() {
		return method;
	}

	public RequestBuilder method(Method method) {
		this.method = method;
		return this;
	}

	public RequestBuilder proxy(Proxy proxy) {
		this.proxy = proxy;
		return this;
	}

	public RequestBuilder ignoreCode(int responseCode) {
		ignoreCodes.add(Integer.valueOf(responseCode));
		return this;
	}

	public RequestBuilder followRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
		return this;
	}

	public RequestBuilder cache(boolean useCache) {
		this.cache = useCache;
		return this;
	}

	public RequestBuilder close(boolean close) {
		this.close = close;
		return this;
	}

	public RequestBuilder connectTimeout(int connectionTimeout) {
		connectTimeout = connectionTimeout;
		return this;
	}

	public RequestBuilder readTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public RequestBuilder put(Cookie cookie) {
		if (cookies == null) cookies = new ArrayList<Cookie>();
		cookies.add(cookie);
		return this;
	}

	public RequestBuilder put(List<Cookie> cookies) {
		if (this.cookies == null) this.cookies = new ArrayList<Cookie>();
		this.cookies.addAll(cookies);
		return this;
	}

	public RequestBuilder use(CookieMap cookies) {
		this.useCookies = cookies;
		return this;
	}

	public RequestBuilder storeCookies(boolean store) {
		this.storeCookies = store;
		return this;
	}

	public RequestBuilder requestCookies(boolean request) {
		this.reqCookies = request;
		return this;
	}

	public RequestBuilder payload(List<NameValuePair> params, String charset)
			throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		for (NameValuePair pair : params) {
			builder.append('&').append(pair.getEncodedPair(charset));
		}
		payload = builder.substring(1).getBytes(charset);
		return this;
	}

	public RequestBuilder payload(byte[] payload) {
		this.payload = payload;
		return this;
	}

	public RequestBuilder header(String name, String value) {
		headerMap.put(name, value);
		return this;
	}

	@SuppressWarnings("resource")
	public Response execute(NetworkHelper networkHelper) throws IOException {
		HttpURLConnection connection;

		// Doesn't log query part for secruity reasons
		log.info("Request {}:{}://{}{}", method, url.getProtocol(),
				url.getAuthority(), url.getPath());

		if (proxy != null) connection = NetworkHelper
				.openConnection(url, proxy);
		else
			connection = networkHelper.openConnection(url);

		connection.setRequestMethod(method.name());
		connection.setConnectTimeout(connectTimeout > -1 ? connectTimeout
				: networkHelper.connectTimeout);
		connection.setReadTimeout(readTimeout > -1 ? readTimeout
				: networkHelper.readTimout);

		/*
		 * Always false because redirects are handled internally.
		 */
		connection.setInstanceFollowRedirects(false);

		connection.setUseCaches(cache);

		if (useCookies != null) CookieManager.setupCookies(connection,
				useCookies);
		if (cookies != null) CookieManager.setCookies(connection, cookies);
		if (reqCookies) networkHelper.cookieManager.setupCookies(connection);

		/*
		 * Adds requests headers to override default ones
		 */
		Map<String, String> headers = new HashMap<String, String>(
				networkHelper.headerMap);
		headers.putAll(headerMap);
		setupHeaders(connection, headers);

		// must send onRequest before outputstream
		if (networkHelper.connListener != null) networkHelper.connListener
				.onRequest(connection, this);
		switch (method) {
			case POST:
			case PUT:
				if (payload == null) break;
				if (connection.getRequestProperty("Content-Type") == null) {
					connection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
				}
				connection.setDoOutput(true);
				connection.setFixedLengthStreamingMode(payload.length);
				OutputStream output = connection.getOutputStream();
				output.write(payload);
				output.close();
				break;
			default:
				break;
		}

		InputStream inputStream = null;
		int statuscode = 0;
		try {
			connection.connect();
			inputStream = NetworkHelper.getInputStream(connection);
			statuscode = connection.getResponseCode();
		} catch (IOException e) {
			statuscode = connection.getResponseCode();

			/*
			 * Get error stream only if status code is 400 or greater, AND
			 * ignore codes doesn't match it
			 */
			if (!(ignoreCodes.contains(Integer.valueOf(statuscode)))
					&& statuscode >= 400) {
				inputStream = NetworkHelper.getErrorStream(connection);
			} else
				throw e;
		}

		log.info("Response {}", connection.getResponseMessage());

		/*
		 * Check internally requests handlers and process them, if can
		 */
		if (internalCodes.indexOfKey(statuscode) >= 0
				&& !ignoreCodes.contains(Integer.valueOf(statuscode))) {
			RequestBuilder newBuilder = internalCodes.get(statuscode)
					.getRequest(statuscode, this, connection);
			if (newBuilder != null) return newBuilder.execute(networkHelper);
		}

		if (useCookies != null) useCookies.putCookieList(
				CookieManager.getCookies(connection), true);
		else if (storeCookies) networkHelper.cookieManager.putCookieList(
				CookieManager.getCookies(connection), true);

		if (networkHelper.connListener != null) networkHelper.connListener
				.onFinish(connection);

		Response response = getResponse(networkHelper.responseHandler,
				connection, inputStream, statuscode);
		if (close) response.close();
		return response;

	}

	@Override
	public String toString() {
		return method + " : " + url.toString();
	}

	private static void setupHeaders(final HttpURLConnection connection,
			final Map<String, String> headers) {
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			connection.setRequestProperty(entry.getKey(), entry.getValue());
		}
	}

	@SuppressWarnings("resource")
	private static Response getResponse(ResponseHandler handler,
			HttpURLConnection connection, InputStream inputStream,
			int statuscode) {
		UnclosableInputStream stream = new UnclosableInputStream(inputStream);
		String charset = NetworkHelper.getCharset(connection);
		Response found = null;
		if (handler != null) found = handler.getResponse(connection, stream,
				statuscode, charset);
		if (found != null) return found;

		if (statuscode / 100 == 3) return new ResponseRedirect(connection,
				stream, statuscode, charset);
		switch (statuscode) {
			case HttpURLConnection.HTTP_UNAUTHORIZED:
				return new ResponseAuthenticate(connection, stream, statuscode,
						charset);
			default:
				return new Response(connection, stream, statuscode, charset);
		}
	}

	static {
		internalCodes = new SparseArray<RequestHandler>(2);
		RedirectHandler redirectHandler = new RedirectHandler();
		internalCodes.put(301, redirectHandler);
		internalCodes.put(302, redirectHandler);
	}
}
