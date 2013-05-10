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

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.krobothsoftware.commons.network.value.Cookie;
import com.krobothsoftware.commons.network.value.CookieList;
import com.krobothsoftware.commons.network.value.CookieMap;
import com.krobothsoftware.commons.util.CommonUtils;

/**
 * Stores and sends HTTP Cookies through {@link java.net.HttpURLConnection}.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 * @see com.krobothsoftware.commons.network.value.CookieList
 */
public class CookieManager implements Serializable {
	private static final long serialVersionUID = -8278470310322638902L;
	private final CookieMap cookieMap;

	/**
	 * Instantiates a new cookie manager.
	 * 
	 * @since SNC 1.0
	 */
	public CookieManager() {
		cookieMap = new CookieMap();
	}

	/**
	 * Gets cookie map.
	 * 
	 * @return unmodifiable map of cookies
	 * @since SNC 1.0
	 */
	public Map<String, CookieList> getCookieMap() {
		return Collections.unmodifiableMap(cookieMap);
	}

	/**
	 * Gets the {@link CookieList} for given domain.
	 * 
	 * 
	 * @param domain
	 *            URL domain
	 * @return an immutable list of cookies, null if not found
	 * @since SNC 1.0
	 * @see com.krobothsoftware.commons.network.value.CookieList
	 */
	public List<Cookie> getCookieList(String domain) {
		return Collections.unmodifiableList(cookieMap.get(domain));
	}

	/**
	 * Get the cookie for domain and name.
	 * 
	 * @param domain
	 *            domain of cookie
	 * @param name
	 *            name of cookie
	 * @return found cookie, or null
	 * @since SNC 1.0
	 */
	public Cookie getCookie(String domain, String name) {
		return cookieMap.getCookie(domain, name);
	}

	/**
	 * Removes cookie in <code>domain</code> for given <code>name</code>.
	 * 
	 * @param name
	 *            the name
	 * @return true, if successful
	 * @since SNC 1.0
	 */
	public boolean removeCookie(String domain, String name) {
		return cookieMap.removeCookie(domain, name);
	}

	/**
	 * Clear cookie map.
	 * 
	 * @since SNC 1.0
	 */
	public void clear() {
		cookieMap.clear();
	}

	/**
	 * Removes expired cookies.
	 * 
	 * @param session
	 *            removes session cookies if true
	 * @since SNC 1.0
	 */
	public void purgeExpired(boolean session) {
		cookieMap.purgeExpired(session);
	}

	/**
	 * Stores given cookies in list.
	 * 
	 * @param cookieList
	 *            {@link Cookie} list
	 * @param overwrite
	 *            if true, will delete previous cookie if present
	 * @since SNC 1.0
	 */
	public void putCookieList(List<Cookie> cookieList, boolean overwrite) {
		cookieMap.putCookieList(cookieList, overwrite);
	}

	/**
	 * Stores <code>cookie</code> in list for given domain.
	 * 
	 * @param cookie
	 *            {@link Cookie}
	 * @since SNC 1.0
	 */
	public void putCookie(Cookie cookie, boolean overwrite) {
		cookieMap.putCookie(cookie, overwrite);
	}

	/**
	 * Sets Cookie request header for <code>connection</code> based on URL.
	 * domain.
	 * 
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * <b>Header</b> Cookie: [cookies]
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * 
	 * @param connection
	 *            connection for cookies
	 * @since SNC 1.0
	 * 
	 */
	public void setupCookies(HttpURLConnection connection) {
		// check for domains
		String host = connection.getURL().getHost();
		List<String> hostList = new ArrayList<String>(cookieMap.getDomains());
		hostList.retainAll(NetworkHelper.getDomains(host));
		setupCookies(connection, cookieMap, hostList);
	}

	private static void setupCookies(HttpURLConnection connection,
			CookieMap map, List<String> domains) {
		if (domains.isEmpty()) return;

		StringBuilder builder;

		if (connection.getRequestProperty("Cookie") != null) {
			builder = new StringBuilder(connection.getRequestProperty("Cookie"));
		} else {
			builder = new StringBuilder();
		}

		for (String domain : domains) {
			List<Cookie> list = map.get(domain);
			Iterator<Cookie> itr = map.get(domain).iterator();

			synchronized (list) {
				while (itr.hasNext()) {
					Cookie cookie = itr.next();
					if (cookie.isExpired()) {
						itr.remove();
						continue;
					}
					
					// Make sure not to continue if both are true
					if (!(cookie.isHttp() && cookie.isSecure())) {
						if (connection instanceof HttpsURLConnection) {
							if (cookie.isHttp()) continue;
						} else {
							if (cookie.isSecure()) continue;
						}

					}

					builder.append(';').append(' ')
							.append(cookie.getCookieString());

				}
			}
		}

		if (builder.length() == 0) return;
		String cookieString;
		if (connection.getRequestProperty("Cookie") == null) cookieString = builder
				.substring(1);
		else
			cookieString = builder.toString();
		cookieString = CommonUtils.trim(cookieString);

		connection.setRequestProperty("Cookie", cookieString);
	}

	/**
	 * Gets List of cookies from {@link HttpURLConnection} response headers.
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * <b>Header</b> Set-Cookie:
	 * ...
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param connection
	 *            url connection
	 * @return cookies built from connection, or empty list
	 * @since SNC 1.0
	 */
	public static List<Cookie> getCookies(HttpURLConnection connection) {
		ArrayList<Cookie> cookieList = new ArrayList<Cookie>();

		int i = 1;
		String header;
		while ((header = connection.getHeaderFieldKey(i)) != null) {
			if (header.equals("Set-Cookie")) {
				cookieList.add(Cookie.parseCookie(connection.getURL(),
						connection.getHeaderField(i)));

			}
			i++;
		}

		return cookieList;
	}

	/**
	 * Sets Cookie request header for <code>connection</code> on list of
	 * cookies.
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * <b>Header</b> Cookie: [cookies]
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * 
	 * @param connection
	 *            being set up
	 * @param cookies
	 *            to set up
	 * @since SNC 1.0
	 */
	public static void setCookies(HttpURLConnection connection,
			List<Cookie> cookies) {
		if (cookies.isEmpty()) return;
		StringBuilder builder = new StringBuilder();
		for (Cookie cookie : cookies) {
			builder.append(';').append(' ').append(cookie.getCookieString());
		}
		connection.setRequestProperty("Cookie",
				CommonUtils.trim(builder.substring(1)));
	}

	/**
	 * Sets Cookie request header for <code>connection</code> on map of cookies.
	 * Only sets cookies with appropriate domain of connection.
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * <b>Header</b> Cookie: [cookies]
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param connection
	 *            being set up
	 * @param cookies
	 *            to set up
	 * @since SNC 1.0
	 */
	public static void setupCookies(HttpURLConnection connection,
			CookieMap cookies) {
		String host = connection.getURL().getHost();
		List<String> hostList = new ArrayList<String>(cookies.getDomains());
		hostList.retainAll(NetworkHelper.getDomains(host));
		setupCookies(connection, cookies, hostList);
	}

}
