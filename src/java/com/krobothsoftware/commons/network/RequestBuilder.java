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
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krobothsoftware.commons.network.value.Cookie;
import com.krobothsoftware.commons.network.value.CookieMap;
import com.krobothsoftware.commons.network.value.NameValuePair;

/**
 * Builder for requesting HTTP connections.
 * 
 * <p>
 * Internally handled responses for response code </br>
 * <table border="1">
 * <tr>
 * <td>301</td>
 * <td>Resends request with new Location</td>
 * </tr>
 * <tr>
 * <td>302</td>
 * <td>Resends request with new Location. Only if <i>followRedirects</i> is true
 * </td>
 * </tr>
 * </table>
 * </br> Use {@link #setInternalHandler(int, RequestHandler)} to set an internal
 * handler </br> To ignore handling, use {@link #ignoreCode(int)}
 * </p>
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 * @see com.krobothsoftware.commons.network.NetworkHelper
 */
public class RequestBuilder {
	private static final Map<Integer, RequestHandler> internalCodes;

	/**
	 * URL for connection.
	 * 
	 * @since SNC 1.0
	 */
	protected URL url;

	/**
	 * Method for connection.
	 * 
	 * @since SNC 1.0
	 */
	protected Method method;

	/**
	 * Logger for Builder.
	 * 
	 * @since SNC 1.0
	 */
	protected Logger log;

	/**
	 * May be null if not set.
	 * 
	 * @since SNC 1.0
	 */
	protected Proxy proxy;

	/**
	 * Status code to ignore while executing response.
	 * 
	 * @since SNC 1.0
	 */
	protected final Set<Integer> ignoreCodes;

	/**
	 * Uses timout if greator than 0. Default is -1.
	 * 
	 * @since SNC 1.0
	 */
	protected int connectTimeout = -1;

	/**
	 * Uses timout if greator than 0. Default is -1.
	 * 
	 * @since SNC 1.0
	 */
	protected int readTimeout = -1;

	/**
	 * Checks whether to use internal redirect builders.
	 * 
	 * @since SNC 1.0
	 */
	protected boolean followRedirects = false;

	/**
	 * Once response is found, close it if true.
	 * 
	 * @since SNC 1.0
	 */
	protected boolean close;

	/**
	 * See {@link URLConnection#setUseCaches(boolean)}. Default is true.
	 * 
	 * @since SNC 1.0
	 */
	protected boolean cache = true;

	/**
	 * Headers for connection.
	 * 
	 * @since SNC 1.0
	 */
	protected final Map<String, String> headerMap;

	/**
	 * Cookies to set to connection.
	 * 
	 * @since SNC 1.0
	 */
	protected List<Cookie> cookies;

	/**
	 * Cookies to setup for connection and process after is sent.
	 * 
	 * @since SNC 1.0
	 */
	protected CookieMap useCookies;

	/**
	 * Store cookies in <code>CookieManager</code>. If {@link #useCookies} is
	 * not null, connection will not store.
	 * 
	 * @since SNC 1.0
	 */
	protected boolean storeCookies = true;

	/**
	 * Request cookies in <code>CookieManager</code>.
	 * 
	 * @since SNC 1.0
	 */
	protected boolean reqCookies = true;

	/**
	 * Payload for outputStream of connection.
	 * 
	 * @since SNC 1.0
	 */
	protected byte[] payload;

	/**
	 * Sets internal request handler for specific response code. To remove, set
	 * <code>handler</code> null.
	 * 
	 * @param responseCode
	 *            status code to check
	 * @param handler
	 *            handler to process if status code matched connection's
	 * @see com.krobothsoftware.commons.network.RequestHandler
	 * @since SNC 1.0
	 */
	public static void setInternalHandler(int responseCode,
			RequestHandler handler) {
		Integer integer = Integer.valueOf(responseCode);
		if (handler == null) internalCodes.remove(integer);
		else
			internalCodes.put(integer, handler);
	}

	/**
	 * Instantiates a new request builder with method and url.
	 * 
	 * @param method
	 *            HTTP method
	 * @param url
	 *            URL to request
	 * @see com.krobothsoftware.commons.network.Method
	 * @since SNC 1.0
	 */
	public RequestBuilder(Method method, URL url) {
		this.method = method;
		this.url = url;
		headerMap = new HashMap<String, String>();
		ignoreCodes = new HashSet<Integer>();
		log = LoggerFactory.getLogger(RequestBuilder.class);
	}

	/**
	 * Instantiates a new request builder from another.
	 * 
	 * @param builder
	 * @since SNC 1.0
	 */
	public RequestBuilder(RequestBuilder builder) {
		this(builder, builder.method, builder.url);
	}

	/**
	 * Instantiates a new request builder from another.
	 * 
	 * @param builder
	 * @param method
	 *            new method
	 * @param url
	 *            new url
	 * @since SNC 1.0
	 */
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

	/**
	 * Gets the url.
	 * 
	 * @return url
	 * @since SNC 1.0
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * Sets the url
	 * 
	 * @param url
	 *            new url
	 * @since SNC 1.0.1
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * Gets the method.
	 * 
	 * @return method
	 * @since SNC 1.0
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * Sets the method
	 * 
	 * @param method
	 *            new method
	 * @since SNC 1.0.1
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * Sets proxy for request.
	 * 
	 * @param proxy
	 *            the proxy
	 * @return request builder
	 * @since SNC 1.0
	 */
	public RequestBuilder proxy(Proxy proxy) {
		this.proxy = proxy;
		return this;
	}

	/**
	 * Sets response code to ignore when connecting. Internally, RequestBuilder
	 * may handle status codes.
	 * 
	 * @param responseCode
	 *            status code to ignore
	 * @return request builder
	 * @see RequestBuilder
	 * @since SNC 1.0
	 */
	public RequestBuilder ignoreCode(int responseCode) {
		ignoreCodes.add(Integer.valueOf(responseCode));
		return this;
	}

	/**
	 * Follow redirects on 302 responses internally. Will copy same request over
	 * and re-send with new URL.
	 * 
	 * @param followRedirects
	 *            the follow redirects
	 * @return request builder
	 * @since SNC 1.0
	 */
	public RequestBuilder followRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
		return this;
	}

	/**
	 * Cache connection. Sets {@link URLConnection#setUseCaches(boolean)}.
	 * Default value is true.
	 * 
	 * @param useCache
	 *            to use cache.
	 * @return request builder
	 */
	public RequestBuilder cache(boolean useCache) {
		this.cache = useCache;
		return this;
	}

	/**
	 * After request is executed, close connection. Default is false.
	 * 
	 * @param close
	 *            close connection
	 * @return request builder
	 * @see com.krobothsoftware.commons.network.Response#close()
	 * @since SNC 1.0
	 */
	public RequestBuilder close(boolean close) {
		this.close = close;
		return this;
	}

	/**
	 * Sets connection timeout. Will override <code>NetworkHelper</code> default
	 * timeout.
	 * 
	 * @param connectionTimeout
	 *            connection timeout
	 * @return request builder
	 * @since SNC 1.0
	 */
	public RequestBuilder connectTimeout(int connectionTimeout) {
		connectTimeout = connectionTimeout;
		return this;
	}

	/**
	 * Sets read timout. Will override <code>NetworkHelper</code> default
	 * timeout.
	 * 
	 * @param readTimeout
	 *            read timeout
	 * @return request builder
	 * @since SNC 1.0
	 */
	public RequestBuilder readTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	/**
	 * Adds cookie for request.
	 * 
	 * @param cookie
	 *            to request
	 * @return request builder
	 * @since SNC 1.0
	 */
	public RequestBuilder put(Cookie cookie) {
		if (cookies == null) cookies = new ArrayList<Cookie>();
		cookies.add(cookie);
		return this;
	}

	/**
	 * Adds list of cookies for request.
	 * 
	 * @param cookies
	 *            to request
	 * @return request builder
	 * @since SNC 1.0
	 */
	public RequestBuilder put(List<Cookie> cookies) {
		if (this.cookies == null) this.cookies = new ArrayList<Cookie>();
		this.cookies.addAll(cookies);
		return this;
	}

	/**
	 * Uses the cookie map for setting up connection and setting after
	 * completed. If used, Cookies will not be added to the
	 * {@link RequestBuilder}.
	 * 
	 * @param cookies
	 *            to and after request
	 * @return request builder
	 * @since SNC 1.0
	 */
	public RequestBuilder use(CookieMap cookies) {
		this.useCookies = cookies;
		return this;
	}

	/**
	 * Store cookies in <code>NetworkHelper</code> {@link RequestBuilder} after
	 * connection is made. True by default.
	 * 
	 * @param store
	 * @return request builder
	 * @since SNC 1.0
	 */
	public RequestBuilder storeCookies(boolean store) {
		this.storeCookies = store;
		return this;
	}

	/**
	 * Request cookies in <code>NetworkHelper</code> {@link RequestBuilder} when
	 * setting up connection. True by default.
	 * 
	 * @param request
	 * @return request builder
	 * @since SNC 1.0
	 */
	public RequestBuilder requestCookies(boolean request) {
		this.reqCookies = request;
		return this;
	}

	/**
	 * Sets the payload for POST and PUT {@link Method}.
	 * 
	 * @param params
	 *            post params
	 * @param charset
	 *            the charset
	 * @return request builder
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 * @since SNC 1.0
	 */
	public RequestBuilder payload(List<NameValuePair> params, String charset)
			throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		for (NameValuePair pair : params) {
			builder.append('&').append(pair.getEncodedPair("UTF-8"));
		}
		payload = builder.substring(1).getBytes(charset);
		return this;
	}

	/**
	 * Sets the payload for POST and PUT {@link Method}.
	 * 
	 * @param payload
	 *            raw bytes
	 * @return request builder
	 * @since SNC 1.0
	 */
	public RequestBuilder payload(byte[] payload) {
		this.payload = payload;
		return this;
	}

	/**
	 * Sets header for request. Will override <code>NetworkHelper</code> defualt
	 * header.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @return request builder
	 * @since SNC 1.0
	 */
	public RequestBuilder header(String name, String value) {
		headerMap.put(name, value);
		return this;
	}

	/**
	 * Sends HTTP request based on request builder. <b>Connection is not
	 * closed</b>.
	 * <p>
	 * Execute process
	 * <ul>
	 * <li>Call
	 * {@link ConnectionListener#onRequest(HttpURLConnection, RequestBuilder)}</li>
	 * <li>Open connection with proxy</li>
	 * <li>Set HTTP Method, connection and read timouts, and follow redirects</li>
	 * <li>Setup Cookies, Headers, and OutputStream</li>
	 * <li>Open connection and get InputStream</li>
	 * <li>Check ignored response codes, handle {@link RequestHandler}'s</li>
	 * <li>Store Cookies</li>
	 * <li>Call {@link ConnectionListener#onFinish(HttpURLConnection)}</li>
	 * </ul>
	 * </p
	 * 
	 * <p>
	 * Responses in current package based on status code </br>First checks
	 * {@link ResponseHandler#getResponse(HttpURLConnection, UnclosableInputStream, int, String)}
	 * <table border="1">
	 * <tr>
	 * <td>3xx</td>
	 * <td>ResponseRedirect.class</td>
	 * </tr>
	 * <tr>
	 * <td>401</td>
	 * <td>ResponseAuthenticate.class</td>
	 * </tr>
	 * <tr>
	 * <td>OTHER</td>
	 * <td>Response.class</td>
	 * </tr>
	 * </table>
	 * 
	 * @param networkHelper
	 *            network helper for connection
	 * @return response depends on status code of connection
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @since SNC 1.0
	 */
	@SuppressWarnings({ "resource", "boxing" })
	public Response execute(NetworkHelper networkHelper) throws IOException {
		HttpURLConnection connection;

		networkHelper.log.info("Request {}:{}://{}{}", method,
				url.getProtocol(), url.getAuthority(), url.getPath());

		if (proxy != null) connection = networkHelper
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
		networkHelper.connListener.onRequest(connection, this);
		switch (method) {
			case POST:
			case PUT:
				if (connection.getRequestProperty("Content-Type") == null) {
					connection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
				}
				if (payload == null) break;
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
			if (!(ignoreCodes.contains(statuscode)) && statuscode >= 400) {
				inputStream = NetworkHelper.getErrorStream(connection);
			} else
				throw e;
		}

		networkHelper.log.info("Response {}", connection.getResponseMessage());

		/*
		 * Check internally requests handlers and process them, if can
		 */
		if (internalCodes.containsKey(statuscode)
				&& !ignoreCodes.contains(statuscode)) {
			RequestBuilder newBuilder = internalCodes.get(statuscode)
					.getRequest(this, connection);
			if (newBuilder != null) return newBuilder.execute(networkHelper);
		}

		if (useCookies != null) useCookies.putCookieList(
				CookieManager.getCookies(connection), true);
		else if (storeCookies) networkHelper.cookieManager.putCookieList(
				CookieManager.getCookies(connection), true);

		networkHelper.connListener.onFinish(connection);

		Response response = getResponse(networkHelper.responseHandler,
				connection, inputStream, statuscode);
		if (close) response.close();
		return response;

	}

	/**
	 * Returns string in format "[method] : [url]".
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public String toString() {
		return method + " : " + url.toString();
	}

	private void setupHeaders(final HttpURLConnection connection,
			final Map<String, String> headers) {
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			connection.setRequestProperty(entry.getKey(), entry.getValue());
		}
	}

	@SuppressWarnings("resource")
	private Response getResponse(ResponseHandler handler,
			HttpURLConnection connection, InputStream inputStream,
			int statuscode) {
		UnclosableInputStream stream = new UnclosableInputStream(inputStream);
		String charset = NetworkHelper.getCharset(connection);
		Response found = handler.getResponse(connection, stream, statuscode,
				charset);
		if (found != null) return found;

		if (statuscode >= 300 && statuscode < 400) return new ResponseRedirect(
				connection, stream, statuscode, charset);
		switch (statuscode) {
			case HttpURLConnection.HTTP_UNAUTHORIZED:
				return new ResponseAuthenticate(connection, stream, statuscode,
						charset);
			default:
				return new Response(connection, stream, statuscode, charset);
		}
	}

	private static class InternalRedirectHandler implements RequestHandler {
		private final int code;

		InternalRedirectHandler(int code) {
			this.code = code;
		}

		InternalRedirectHandler() {
			this.code = -1;
		}

		@Override
		public RequestBuilder getRequest(RequestBuilder builder,
				HttpURLConnection connection) throws IOException {
			// respect follow redirects option
			if (code == 302 && !builder.followRedirects) return null;

			builder.log.debug("Internally handled redirect");
			String location = connection.getHeaderField("Location");
			connection.disconnect();

			// only use one instance of redirect builder
			RequestBuilder newBuilder;
			if (builder instanceof RequestBuilderRedirect) {
				newBuilder = builder;
				newBuilder.setUrl(new URL(location));
			} else {
				newBuilder = new RequestBuilderRedirect(builder, location);
			}
			return newBuilder;
		}

	}

	static {
		internalCodes = new HashMap<Integer, RequestHandler>(2);
		internalCodes.put(Integer.valueOf(301), new InternalRedirectHandler());
		internalCodes.put(Integer.valueOf(302),
				new InternalRedirectHandler(302));
	}
}
