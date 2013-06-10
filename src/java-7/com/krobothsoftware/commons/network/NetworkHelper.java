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
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import com.krobothsoftware.commons.network.authentication.AuthenticationManager;
import com.krobothsoftware.commons.network.value.NameValuePair;

public class NetworkHelper {

	public static final String AGENT_DEFAULT;

	@Deprecated
	public static final ConnectionListener NULL_CONNECTION_LISTENER = null;

	@Deprecated
	public static final ResponseHandler NULL_RESPONSE_HANDLER = null;

	private static final String VERSION = "1.1.0";

	private static final int MAX_REDIRECTS;

	protected CookieManager cookieManager;

	protected final AuthenticationManager authManager;

	// default values
	Proxy proxy;
	ConnectionListener connListener;
	ResponseHandler responseHandler;
	final Map<String, String> headerMap;
	int maxRedirects;
	int connectTimeout;
	int readTimout;

	public NetworkHelper() {
		cookieManager = new CookieManager();
		authManager = new AuthenticationManager(this);
		headerMap = new HashMap<>();
		proxy = Proxy.NO_PROXY;
		maxRedirects = MAX_REDIRECTS;
		setupHeaders();
	}

	public void reset() {
		connectTimeout = 0;
		readTimout = 0;
		maxRedirects = MAX_REDIRECTS;
		proxy = Proxy.NO_PROXY;
		headerMap.clear();
		setupHeaders();
	}

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

	public void setProxy(Proxy proxy) {
		this.proxy = Objects.requireNonNull(proxy, "Proxy may not be null");
	}

	public void setHeader(String name, String value) {
		Objects.requireNonNull(name, "Header name may not be null");
		if (value == null) headerMap.remove(name);
		else
			headerMap.put(name, value);
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimout = readTimeout;
	}

	public void setMaxRedirects(int redirects) {
		this.maxRedirects = redirects;
	}

	public void setConnectionListener(ConnectionListener connectionListener) {
		this.connListener = connectionListener;
	}

	public void setResponseHandler(ResponseHandler responseHandler) {
		this.responseHandler = responseHandler;
	}

	public final AuthenticationManager getAuthorizationManager() {
		return authManager;
	}

	public final CookieManager getCookieManager() {
		return cookieManager;
	}

	public final void setCookieManager(CookieManager manager) {
		this.cookieManager = manager;
	}

	public HttpURLConnection openConnection(URL url) throws IOException {
		return (HttpURLConnection) url.openConnection(proxy);
	}

	public HttpURLConnection openConnection(URI uri) throws IOException {
		return (HttpURLConnection) uri.toURL().openConnection(proxy);
	}

	public HttpURLConnection openConnection(String url) throws IOException {
		return (HttpURLConnection) new URL(url).openConnection(proxy);
	}

	public static HttpURLConnection openConnection(URL url, Proxy proxy)
			throws IOException {
		return (HttpURLConnection) url.openConnection(proxy);
	}

	public static HttpURLConnection openConnection(URI uri, Proxy proxy)
			throws IOException {
		return (HttpURLConnection) uri.toURL().openConnection(proxy);
	}

	public static HttpURLConnection openConnection(String url, Proxy proxy)
			throws IOException {
		return (HttpURLConnection) new URL(url).openConnection(proxy);
	}

	public static List<NameValuePair> getPairs(String... nameValue) {
		if (nameValue.length % 2 != 0) throw new IllegalArgumentException(
				"Odd number of elements");

		ArrayList<NameValuePair> pairs = new ArrayList<>(nameValue.length / 2);
		for (int i = 0; i < nameValue.length; i += 2) {
			pairs.add(new NameValuePair(nameValue[i], nameValue[i + 1]));
		}

		return pairs;

	}

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

	public static List<NameValuePair> getQueryList(String query) {
		if (query == null) return Collections.emptyList();
		String[] params = query.split("&");
		if (params.length == 0) return Collections.emptyList();
		ArrayList<NameValuePair> listParams = new ArrayList<>(params.length);

		String[] value;
		for (String param : params) {
			value = param.split("=");
			listParams.add(new NameValuePair(value[0], value[1]));
		}

		return listParams;
	}

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

	public static List<String> getDomains(String urlHost) {
		List<String> hostList = new ArrayList<>();
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
		AGENT_DEFAULT = String.format("NetworkHelper /%s (%s %s; Java %s)",
				VERSION, System.getProperty("os.name"),
				System.getProperty("os.version"),
				System.getProperty("java.version"));
	}
}
