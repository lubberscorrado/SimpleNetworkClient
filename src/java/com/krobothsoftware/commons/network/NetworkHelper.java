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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krobothsoftware.commons.network.authentication.AuthenticationManager;
import com.krobothsoftware.commons.network.authentication.RequestBuilderAuthenticate;
import com.krobothsoftware.commons.network.value.NameValuePair;

/**
 * Small client for helping connections.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 * @see java.net.HttpURLConnection
 * @see com.krobothsoftware.commons.network.RequestBuilder
 */
public class NetworkHelper {

	/**
	 * Generated User Agent.
	 * 
	 * <pre>
	 * <b>Examples</b> 
	 * <code>NetworkHelper/1.0.0 (Windows 7 6.1; Java 1.7.0_17)</code>
	 * <code>NetworkHelper/1.0.0 (Linux 3.0.31-00001-gf84bc96; samsung SCH-I500; Android 4.1.1)</code>
	 * </pre>
	 * 
	 * @since SNC 1.0
	 */
	public static final String AGENT_DEFAULT;

	/**
	 * Null Connection Listener. Use this instead of <code>null</code> when
	 * setting listener.
	 * 
	 * @since SNC 1.0
	 */
	public static final ConnectionListener NULL_CONNECTION_LISTENER;

	/**
	 * Null Response Handler. Use this instead of <code>null</code> when setting
	 * handler.
	 * 
	 * @since SNC 1.0
	 */
	public static final ResponseHandler NULL_RESPONSE_HANDLER;

	/**
	 * Version of <code>SNC</code>. Used in User Agent.
	 */
	private static final String VERSION = "1.0.0";

	/**
	 * Max redirects for <code>RequestBuilderRedirect</code>. Default
	 * <code>http.maxRedirects</code> or 20.
	 */
	private static final int MAX_REDIRECTS;

	/**
	 * Cookie manager for connections. New manager can be set with
	 * {@link #setCookieManager(CookieManager)}.
	 * 
	 * @since SNC 1.0
	 */
	protected CookieManager cookieManager;

	/**
	 * Manager for authentications when {@link RequestBuilderAuthenticate} is
	 * used.
	 * 
	 * @since SNC 1.0
	 */
	protected final AuthenticationManager authManager;

	/**
	 * Main logger for sending connections.
	 * 
	 * @since SNC 1.0
	 */
	protected Logger log;

	Proxy proxy;
	ConnectionListener connListener;
	ResponseHandler responseHandler;
	final Map<String, String> headerMap;
	int maxRedirects;
	int connectTimeout;
	int readTimout;

	/**
	 * Instantiates a new network helper.
	 * 
	 * @since SNC 1.0
	 */
	public NetworkHelper() {
		log = LoggerFactory.getLogger(NetworkHelper.class);
		cookieManager = new CookieManager();
		authManager = new AuthenticationManager(this);
		headerMap = new HashMap<String, String>();
		proxy = Proxy.NO_PROXY;
		connListener = NULL_CONNECTION_LISTENER;
		responseHandler = NULL_RESPONSE_HANDLER;
		maxRedirects = MAX_REDIRECTS;
		setupHeaders();
	}

	/**
	 * Resets values. <b>Only</b> Timeouts, max redirects, headers, and proxy.
	 * 
	 * @since SNC 1.0
	 */
	public void reset() {
		connectTimeout = 0;
		readTimout = 0;
		maxRedirects = MAX_REDIRECTS;
		proxy = Proxy.NO_PROXY;
		headerMap.clear();
		setupHeaders();
	}

	/**
	 * Sets default headers.
	 * 
	 * <pre>
	 * <table border="1">
	 * <tr>
	 * <td>User Agent</td>
	 * <td>NetworkHelper.AGENT_DEFAULT</td>
	 * </tr>
	 * <tr>
	 * <td>Accept</td>
	 * <td>text/html,application/xhtml+xml,application/xml;q=0.9,*&#47;*;q=0.8</td>
	 * </tr>
	 * <tr>
	 * <td>Accept-Encoding</td>
	 * <td>gzip, deflate</td>
	 * </tr>
	 * <tr>
	 * <td>Accept-Charset</td>
	 * <td>UTF-8</td>
	 * </tr>
	 * </table>
	 * </pre>
	 * 
	 * @since SNC 1.0
	 */
	protected void setupHeaders() {
		headerMap.put("User-Agent", AGENT_DEFAULT);
		headerMap
				.put("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		headerMap.put("Accept-Encoding", "gzip, deflate");
		headerMap.put("Accept-Charset", "UTF-8");

		/*
		 * Close not needed anymore with unclosable streams. Plus it may still
		 * be cached after if used too.
		 */
		// headerMap.put("Connection", "close");
	}

	/**
	 * Sets the proxy used for opening connections. Default is
	 * {@link Proxy#NO_PROXY}.
	 * 
	 * @param proxy
	 *            the new proxy
	 * @throws IllegalArgumentException
	 *             if proxy is null
	 * @see #openConnection(URL)
	 * @since SNC 1.0
	 */
	public void setProxy(Proxy proxy) {
		if (proxy == null) throw new IllegalArgumentException(
				"Proxy may not be null");
		this.proxy = proxy;
	}

	/**
	 * Sets default header for connections. If the <code>RequestBuilder</code>
	 * has the same header, the default one will <b>not</b> be used.
	 * 
	 * @param name
	 *            header name
	 * @param value
	 *            header value, or null to remove
	 * @see com.krobothsoftware.commons.network.RequestBuilder
	 * @since SNC 1.0
	 */
	public void setHeader(String name, String value) {
		if (name == null) throw new IllegalArgumentException(
				"Header name may not be null");
		if (value == null) headerMap.remove(name);
		else
			headerMap.put(name, value);
	}

	/**
	 * Sets default connect timeout for connections. If the
	 * <code>RequestBuilder</code> already has a connect timeout set, the
	 * default one will <b>not</b> be used.
	 * 
	 * @param connectTimeout
	 *            new default connect timout
	 * @see com.krobothsoftware.commons.network.RequestBuilder
	 * @since SNC 1.0
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * Sets default read timeout for connections. If the
	 * <code>RequestBuilder</code> already has a connect read set, the default
	 * one will <b>not</b> be used.
	 * 
	 * @param readTimeout
	 *            new default read timeout
	 * @see com.krobothsoftware.commons.network.RequestBuilder
	 * @since SNC 1.0
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimout = readTimeout;
	}

	/**
	 * Sets max re-directs for connections. Used on
	 * {@link RequestBuilder#followRedirects(boolean)} option. Default is from
	 * property <i>http.maxRedirects</i> or 20.
	 * 
	 * @param redirects
	 *            new max re-directs
	 * @see com.krobothsoftware.commons.network.RequestBuilder
	 * @since SNC 1.0
	 */
	public void setMaxRedirects(int redirects) {
		this.maxRedirects = redirects;
	}

	/**
	 * Sets <code>ConnectionListener</code> for all <code>RequestBuilder</code>
	 * s.
	 * 
	 * @param connectionListener
	 * @throws IllegalArgumentException
	 *             if listener is null
	 * @see com.krobothsoftware.commons.network.RequestBuilder
	 * @see #NULL_CONNECTION_LISTENER
	 * @since SNC 1.0
	 */
	public void setConnectionListener(ConnectionListener connectionListener) {
		if (connectionListener == null) throw new IllegalArgumentException(
				"Connection Listener may not be null");
		this.connListener = connectionListener;
	}

	/**
	 * Sets <code>ResponseHandler</code> for all <code>RequestBuilder</code>s.
	 * 
	 * @param responseHandler
	 * @see com.krobothsoftware.commons.network.RequestBuilder
	 * @throws IllegalArgumentException
	 *             if handler is null
	 * @since SNC 1.0
	 */
	public void setResponseHandler(ResponseHandler responseHandler) {
		if (responseHandler == null) throw new IllegalArgumentException(
				"Response Handler may not be null");
		this.responseHandler = responseHandler;
	}

	/**
	 * Gets Authentication Manager for connections.
	 * 
	 * @return authentication manager
	 * @since SNC 1.0
	 */
	public AuthenticationManager getAuthorizationManager() {
		return authManager;
	}

	/**
	 * Gets Cookie Manager used for connections.
	 * 
	 * @return cookie manager
	 * @since SNC 1.0
	 */
	public CookieManager getCookieManager() {
		return cookieManager;
	}

	/**
	 * Sets new cookie manager.
	 * 
	 * @param manager
	 *            to set
	 * @since SNC 1.0
	 */
	public void setCookieManager(CookieManager manager) {
		this.cookieManager = manager;
	}

	/**
	 * Opens connection and sets proxy from <code>NetworkHelper</code>.
	 * 
	 * @param url
	 *            url for connection
	 * @return {@link java.net.HttpURLConnection}
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see #setProxy(Proxy)
	 * @since SNC 1.0
	 */
	public HttpURLConnection openConnection(URL url) throws IOException {
		return (HttpURLConnection) url.openConnection(proxy);
	}

	/**
	 * Opens connection with proxy.
	 * 
	 * @param url
	 *            url for connection
	 * @param proxy
	 *            proxy for connection, can't be null
	 * @return {@link java.net.HttpURLConnection}
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @since SNC 1.0
	 */
	public HttpURLConnection openConnection(URL url, Proxy proxy)
			throws IOException {
		return (HttpURLConnection) url.openConnection(proxy);
	}

	/**
	 * Helper method for building a {@link NameValuePair} list. Every two
	 * elements in the <code>nameValue</code> array adds a new
	 * <code>NameValuePair</code>.
	 * 
	 * @param nameValue
	 *            array of names and values.
	 * @return constructed list of pairs
	 * @throws IllegalArgumentException
	 *             if there are an odd number of elements
	 * @since SNC 1.0
	 */
	public static List<NameValuePair> getPairs(String... nameValue) {
		if (nameValue.length % 2 != 0) throw new IllegalArgumentException(
				"Odd number of elements");

		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>(
				nameValue.length / 2);
		for (int i = 0; i < nameValue.length; i += 2) {
			pairs.add(new NameValuePair(nameValue[i], nameValue[i + 1]));
		}

		return pairs;

	}

	/**
	 * Helper method for setting query part of URL.
	 * 
	 * @param url
	 * @param query
	 *            list of <code>NameValuePair</code>
	 * @return modified URL with query
	 * @throws UnsupportedEncodingException
	 *             if charset isn't found
	 * @since SNC 1.0
	 */
	public static String setQuery(String url, List<NameValuePair> query)
			throws UnsupportedEncodingException {
		if (query.isEmpty()) return url;
		StringBuilder builder = new StringBuilder();
		builder.append(url);
		builder.append('?');
		for (NameValuePair pair : query) {
			builder.append(pair.getEncodedPair("UTF-8"));
			builder.append('&');
		}

		return builder.substring(0, builder.length() - 1);
	}

	/**
	 * Parses query and puts it in a {@link NameValuePair} List.
	 * 
	 * @param query
	 *            query part
	 * @return query params
	 * @since SNC 1.0
	 */
	public static List<NameValuePair> getQueryList(String query) {
		if (query == null) return new ArrayList<NameValuePair>();
		String[] params = query.split("&");
		ArrayList<NameValuePair> listParams = new ArrayList<NameValuePair>(
				params.length);

		String[] value;
		for (String param : params) {
			value = param.split("=");
			listParams.add(new NameValuePair(value[0], value[1]));
		}

		return listParams;
	}

	/**
	 * Gets the charset from <code>urlConnection</code>. If none found, will
	 * return the default UTF-8.
	 * 
	 * @param connection
	 *            connection
	 * @return charset found charset or UTF-8
	 * @since SNC 1.0
	 */
	public static String getCharset(HttpURLConnection connection) {
		String contentType = connection.getContentType();

		if (contentType == null) return null;
		String[] values = contentType.split(";");

		String charset = null;

		for (String value : values) {
			value = value.trim();

			if (value.toLowerCase().startsWith("charset=")) {
				charset = value.substring("charset=".length());
				break;
			}
		}

		if (charset == null) {
			charset = "UTF-8";
		}

		return charset;

	}

	/**
	 * Gets the correct {@link java.io.InputStream} based on
	 * <code>urlConnection</code> encoding.
	 * 
	 * @param connection
	 *            connection
	 * @return inputstream from encoding. If not supported returns normal
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *             {@link java.io.InputStream}
	 * @since SNC 1.0
	 */
	public static InputStream getInputStream(HttpURLConnection connection)
			throws IOException {
		if (connection.getRequestMethod().equals("HEAD")) return null;
		String encoding = connection.getContentEncoding();
		if (encoding == null) return connection.getInputStream();
		else if (encoding.equalsIgnoreCase("gzip")) return new GZIPInputStream(
				connection.getInputStream());
		else if (encoding.equalsIgnoreCase("deflate")) return new InflaterInputStream(
				connection.getInputStream(), new Inflater(true));

		return null;

	}

	/**
	 * Gets the correct Error{@link java.io.InputStream} based on
	 * <code>urlConnection</code> encoding.
	 * 
	 * @param connection
	 *            connection
	 * @return errorstream from encoding. If not supported returns normal
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *             {@link java.io.InputStream}
	 * @since SNC 1.0
	 */
	public static InputStream getErrorStream(HttpURLConnection connection)
			throws IOException {
		if (connection.getRequestMethod().equals("HEAD")) return null;
		String encoding = connection.getContentEncoding();
		if (encoding == null) return connection.getErrorStream();
		else if (encoding.equalsIgnoreCase("gzip")) return new GZIPInputStream(
				connection.getErrorStream());
		else if (encoding.equalsIgnoreCase("deflate")) return new InflaterInputStream(
				connection.getErrorStream(), new Inflater(true));

		return null;
	}

	/**
	 * Fast way of getting domains from url host. <b>Must</b> be host and not
	 * full URL.
	 * 
	 * @param urlHost
	 *            host of url
	 * @return list of domains
	 * @since SNC 1.0
	 */
	public static List<String> getDomains(String urlHost) {
		List<String> hostList = new ArrayList<String>();
		hostList.add(urlHost);
		char[] ch = urlHost.toCharArray();
		int len = ch.length;
		int off = len - 1;
		while (ch[off] != '.')
			off--;

		// go past current dot
		off -= 1;
		for (; off > 0; off--) {
			if (ch[off] == '.') {
				hostList.add(new String(ch, off, len - off));
			}
		}

		return hostList;
	}

	static {
		MAX_REDIRECTS = 20;

		// Java SE
		AGENT_DEFAULT = String.format(
				"SimpleNetworkClient /%s (%s %s; Java %s)", VERSION,
				System.getProperty("os.name"),
				System.getProperty("os.version"),
				System.getProperty("java.version"));
		NULL_CONNECTION_LISTENER = new ConnectionListener() {

			@Override
			public void onRequest(HttpURLConnection connection,
					RequestBuilder builder) {

			}

			@Override
			public void onFinish(HttpURLConnection connection) {

			}

		};

		NULL_RESPONSE_HANDLER = new ResponseHandler() {

			@Override
			public Response getResponse(HttpURLConnection connection,
					UnclosableInputStream inputstream, int status,
					String charset) {
				return null;
			}
		};
	}
}
