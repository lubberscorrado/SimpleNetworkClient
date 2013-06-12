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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import com.krobothsoftware.commons.network.authentication.AuthenticationManager;
import com.krobothsoftware.commons.network.authentication.RequestBuilderAuthenticate;
import com.krobothsoftware.commons.network.value.NameValuePair;

/**
 * Helper for HTTP connections and holds default values for
 * {@link RequestBuilder} when executing.
 * 
 * <p>
 * Open HttpURLConnection with <code>openConnection()</code> methods. All
 * default values set will be used in <code>RequestBuilder</code> connections,
 * but the builder may override them. Calling {@link #reset()} will reset
 * timouts, max-redirects, proxy, and set default headers from
 * {@link #setupHeaders()}.
 * </p>
 * 
 * <pre>
 *  <code>
 *  networkHelper.setConnectTimeout(100);
 *  networkHelper.setHeader("User-Agent", NetworkHelper.AGENT_DEFAULT);
 *  
 *  RequestBuilder builder = new RequestBuilder(Method.GET, new URL(
 *  		"http://www.unatco.org/"))
 *  		.header("User-Agent", "JC Denton")
 *  		.connectTimeout(1000);
 *  Response response = builder.execute(networkHelper);
 *  </code>
 * </pre>
 * <p>
 * In the example above, connection timeout and User-Agent header set in
 * NetworkHelper will be ignored because <code>RequestBuilder</code> has them
 * set.
 * </p>
 * 
 * 
 * 
 * @author Kyle Kroboth
 * @since COMMONS 1.0
 * @see java.net.HttpURLConnection
 * @see com.krobothsoftware.commons.network.RequestBuilder
 */
public class NetworkHelper {

	/**
	 * Generated User Agent. Used in default headers.
	 * 
	 * <pre>
	 * <b>Examples</b> 
	 * <code>NetworkHelper/1.0.0 (Windows 7 6.1; Java 1.7.0_17)</code>
	 * <code>NtworkHelper/1.0.0 (Linux 3.0.31-00001-gf84bc96; samsung SCH-I500; Android 4.1.1)</code>
	 * </pre>
	 * 
	 * @since COMMONS 1.0
	 */
	public static final String AGENT_DEFAULT;

	/**
	 * Null Connection Listener. Use this instead of <code>null</code> when
	 * setting listener.
	 * 
	 * @deprecated Now allowed to be null
	 * @since COMMONS 1.0
	 */
	@Deprecated
	public static final ConnectionListener NULL_CONNECTION_LISTENER = null;

	/**
	 * Null Response Handler. Use this instead of <code>null</code> when setting
	 * handler.
	 * 
	 * @deprecated Now allowed to be null
	 * @since COMMONS 1.0
	 */
	@Deprecated
	public static final ResponseHandler NULL_RESPONSE_HANDLER = null;

	/**
	 * Version of <code>SNC</code>. Used in User Agent.
	 */
	private static final String VERSION = "1.1.0";

	/**
	 * Default Max redirects for <code>RequestBuilderRedirect</code>. Default
	 * <code>http.maxRedirects</code> or 20.
	 */
	private static final int MAX_REDIRECTS;

	/**
	 * Cookie manager for connections. New manager can be set with
	 * {@link #setCookieManager(CookieManager)}.
	 * 
	 * @since COMMONS 1.0
	 */
	protected CookieManager cookieManager;

	/**
	 * Manager for authentications when {@link RequestBuilderAuthenticate} is
	 * used.
	 * 
	 * @since COMMONS 1.0
	 */
	protected final AuthenticationManager authManager;

	// default values
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
	 * @since COMMONS 1.0
	 */
	public NetworkHelper() {
		cookieManager = new CookieManager();
		authManager = new AuthenticationManager(this);
		headerMap = new HashMap<String, String>();
		proxy = Proxy.NO_PROXY;
		maxRedirects = MAX_REDIRECTS;
		setupHeaders();
	}

	/**
	 * Resets values. <b>Only</b> Timeouts, max redirects, headers, and proxy.
	 * 
	 * @since COMMONS 1.0
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
	 * @since COMMONS 1.0
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
	 * @since COMMONS 1.0
	 */
	public void setProxy(Proxy proxy) {
		if (proxy == null) throw new NullPointerException(
				"Proxy may not be null");
		this.proxy = proxy;
	}

	/**
	 * Sets default header for connections. Will be ignored if
	 * <code>RequestBuilder</code> has same header set.
	 * 
	 * @param name
	 *            header name
	 * @param value
	 *            header value, or null to remove
	 * @see com.krobothsoftware.commons.network.RequestBuilder
	 * @since COMMONS 1.0
	 */
	public void setHeader(String name, String value) {
		if (name == null) throw new NullPointerException(
				"Header name may not be null");
		if (value == null) headerMap.remove(name);
		else
			headerMap.put(name, value);
	}

	/**
	 * Sets default connect timeout for connections. Will be ignored if
	 * <code>RequestBuilder</code> has one set.
	 * 
	 * @param connectTimeout
	 *            new default connect timeout
	 * @see com.krobothsoftware.commons.network.RequestBuilder
	 * @since COMMONS 1.0
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * Sets default read timeout for connections. Will be ignored if
	 * <code>RequestBuilder</code> has one set.
	 * 
	 * @param readTimeout
	 *            new default read timeout
	 * @see com.krobothsoftware.commons.network.RequestBuilder
	 * @since COMMONS 1.0
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
	 * @since COMMONS 1.0
	 */
	public void setMaxRedirects(int redirects) {
		this.maxRedirects = redirects;
	}

	/**
	 * Sets <code>ConnectionListener</code> for all <code>RequestBuilders</code>
	 * executed in <code>NetworkHelper</code>.
	 * 
	 * @param connectionListener
	 * @see com.krobothsoftware.commons.network.RequestBuilder
	 * @since COMMONS 1.0
	 */
	public void setConnectionListener(ConnectionListener connectionListener) {
		this.connListener = connectionListener;
	}

	/**
	 * Sets <code>ResponseHandler</code> for all <code>RequestBuilders</code>
	 * executed in <code>NetworkHelper</code>.
	 * 
	 * @param responseHandler
	 * @see com.krobothsoftware.commons.network.RequestBuilder
	 * @since COMMONS 1.0
	 */
	public void setResponseHandler(ResponseHandler responseHandler) {
		this.responseHandler = responseHandler;
	}

	/**
	 * Gets Authentication Manager.
	 * 
	 * @return authentication manager
	 * @since COMMONS 1.0
	 */
	public final AuthenticationManager getAuthorizationManager() {
		return authManager;
	}

	/**
	 * Gets Cookie Manager.
	 * 
	 * @return cookie manager
	 * @since COMMONS 1.0
	 */
	public final CookieManager getCookieManager() {
		return cookieManager;
	}

	/**
	 * Sets new cookie manager.
	 * 
	 * @param manager
	 *            to set in network helper
	 * @since COMMONS 1.0
	 */
	public final void setCookieManager(CookieManager manager) {
		this.cookieManager = manager;
	}

	/**
	 * Opens connection and sets proxy from <code>NetworkHelper</code>.
	 * 
	 * <pre>
	 * {@link URL#openConnection(Proxy)}
	 * </pre>
	 * 
	 * @param url
	 *            url for connection
	 * @return {@link java.net.HttpURLConnection}
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see #setProxy(Proxy)
	 * @since COMMONS 1.0
	 */
	public HttpURLConnection openConnection(URL url) throws IOException {
		return (HttpURLConnection) url.openConnection(proxy);
	}

	/**
	 * Converts <code>URI</code> to <code>URL</code> and opens connection with
	 * proxy from <code>NetworkHelper</code>.
	 * 
	 * <pre>
	 * {@link URI#toURL()}
	 * {@link URL#openConnection(Proxy)}
	 * </pre>
	 * 
	 * @param uri
	 *            to convert to URL and open connection
	 * @return {@link java.net.HttpURLConnection}
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @since COMMONS 1.1.0
	 */
	public HttpURLConnection openConnection(URI uri) throws IOException {
		return (HttpURLConnection) uri.toURL().openConnection(proxy);
	}

	/**
	 * Creates new <code>URL</code> and opens connection with proxy from
	 * <code>NetworkHelper</code>.
	 * 
	 * <pre>
	 * {@link URL#URL(String)}
	 * {@link URL#openConnection(Proxy)}
	 * </pre>
	 * 
	 * @param url
	 *            to create URL and open connection
	 * @return {@link java.net.HttpURLConnection}
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @since COMMONS 1.1.0
	 */
	public HttpURLConnection openConnection(String url) throws IOException {
		return (HttpURLConnection) new URL(url).openConnection(proxy);
	}

	/**
	 * Opens connection with proxy.
	 * 
	 * <p>
	 * {@link URL#openConnection(Proxy)}
	 * </p>
	 * 
	 * @param url
	 *            url for connection
	 * @param proxy
	 *            proxy for connection, can't be null
	 * @return {@link java.net.HttpURLConnection}
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @since COMMONS 1.0
	 */
	public static HttpURLConnection openConnection(URL url, Proxy proxy)
			throws IOException {
		return (HttpURLConnection) url.openConnection(proxy);
	}

	/**
	 * Converts <code>URI</code> to <code>URL</code> and opens connection with
	 * proxy.
	 * 
	 * <pre>
	 * {@link URI#toURL()}
	 * {@link URL#openConnection(Proxy)}
	 * </pre>
	 * 
	 * @param uri
	 *            to convert to URL and open connection
	 * @param proxy
	 *            proxy for connection, can't be null
	 * @return {@link java.net.HttpURLConnection}
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @since COMMONS 1.1.0
	 */
	public static HttpURLConnection openConnection(URI uri, Proxy proxy)
			throws IOException {
		return (HttpURLConnection) uri.toURL().openConnection(proxy);
	}

	/**
	 * Creates new <code>URL</code> and opens connection with proxy.
	 * 
	 * <pre>
	 * {@link URL#URL(String)}
	 * {@link URL#openConnection(Proxy)}
	 * </pre>
	 * 
	 * @param url
	 *            to create URL and open connection
	 * @param proxy
	 *            proxy for connection, can't be null
	 * @return {@link java.net.HttpURLConnection}
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @since COMMONS 1.1.0
	 */
	public static HttpURLConnection openConnection(String url, Proxy proxy)
			throws IOException {
		return (HttpURLConnection) new URL(url).openConnection(proxy);
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
	 * @since COMMONS 1.0
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
	 * @return modified URL with query, or same URL if query is empty.
	 * @throws UnsupportedEncodingException
	 *             if charset isn't found
	 * @since COMMONS 1.0
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
	 * @return query params or {@link Collections#emptyList()}
	 * @since COMMONS 1.0
	 */
	public static List<NameValuePair> getQueryList(String query) {
		if (query == null) return Collections.emptyList();
		String[] params = query.split("&");
		if (params.length == 0) return Collections.emptyList();
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
	 * @since COMMONS 1.0
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
	 * @since COMMONS 1.0
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
	 * <code>HttpURLConnection</code> encoding.
	 * 
	 * @param connection
	 *            connection
	 * @return errorstream from encoding. If not supported returns normal
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *             {@link java.io.InputStream}
	 * @since COMMONS 1.0
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
	 * @since COMMONS 1.0
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

		AGENT_DEFAULT = new StringBuilder("NetworkHelper /").append(VERSION)
				.append(' ').append('(').append(System.getProperty("os.name"))
				.append(' ').append(System.getProperty("os.version"))
				.append(';').append(" Java ")
				.append(System.getProperty("java.version")).append(')')
				.toString();

	}
}
