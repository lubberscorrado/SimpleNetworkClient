package com.krobothsoftware.commons.network.value;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HashMap holding Cookies with set of domains.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public class CookieMap extends AbstractMap<String, CookieList> implements
		Map<String, CookieList>, Cloneable, Serializable {
	private static final long serialVersionUID = 5833376510852965354L;
	private final HashMap<String, CookieList> delegate;
	private final Set<String> domainSet = new HashSet<String>();

	/**
	 * {@link HashMap#HashMap(int, float)}.
	 * 
	 * @param initialCapacity
	 * @param loadFactor
	 * @since SNC 1.0
	 */
	public CookieMap(int initialCapacity, float loadFactor) {
		delegate = new HashMap<String, CookieList>(initialCapacity, loadFactor);
	}

	/**
	 * {@link HashMap#HashMap(int)}.
	 * 
	 * @param initialCapacity
	 * @since SNC 1.0
	 */
	public CookieMap(int initialCapacity) {
		delegate = new HashMap<String, CookieList>(initialCapacity);
	}

	/**
	 * {@link HashMap#HashMap()}.
	 * 
	 * @since SNC 1.0
	 */
	public CookieMap() {
		delegate = new HashMap<String, CookieList>();
	}

	/**
	 * {@link HashMap#HashMap(Map)}.
	 * 
	 * @param m
	 * @since SNC 1.0
	 */
	public CookieMap(Map<? extends String, ? extends CookieList> m) {
		delegate = new HashMap<String, CookieList>(m);
	}

	/**
	 * Gets set of domains of every cookie.
	 * 
	 * @return unmodifiable set of domains
	 * @since SNC 1.0
	 */
	public Set<String> getDomains() {
		return Collections.unmodifiableSet(domainSet);
	}

	/**
	 * Removes all expired cookies.
	 * 
	 * @param session
	 *            removes session cookies if true
	 * @since SNC 1.0
	 */
	public void purgeExpired(boolean session) {
		for (String domain : domainSet) {
			delegate.get(domain).purgeExpired(session);
		}
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public int size() {
		return delegate.size();
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public CookieList get(Object key) {
		return delegate.get(key);
	}

	/**
	 * Gets cookie from domain and name.
	 * 
	 * @param domain
	 *            domain of cookie
	 * @param name
	 *            name of cookie
	 * @return found cookie, or null
	 */
	public Cookie getCookie(String domain, String name) {
		CookieList list = get(domain);
		if (list != null) return list.get(name);
		return null;
	}

	/**
	 * Adds to delegate and domain set
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public CookieList put(String key, CookieList value) {
		domainSet.add(key);
		return delegate.put(key, value);
	}

	/**
	 * Puts Cookie correct domain.
	 * 
	 * @param cookie
	 *            to add
	 * @param overwrite
	 *            if true, will overwrite in cookie list
	 * @since SNC 1.0
	 */
	public void putCookie(Cookie cookie, boolean overwrite) {
		String domain = cookie.getDomain();
		CookieList list = get(domain);

		if (list == null) {
			list = new CookieList();
			put(domain, list);
		}

		if (overwrite) list.put(cookie);
		else
			list.add(cookie);
	}

	/**
	 * Puts list of cookies their corresponding domains.
	 * 
	 * @param cookies
	 *            to add
	 * @param overwrite
	 *            if true, will overwrite
	 * @since SNC 1.0
	 */
	public void putCookieList(List<Cookie> cookies, boolean overwrite) {
		for (Cookie cookie : cookies) {
			putCookie(cookie, overwrite);
		}
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public CookieList remove(Object key) {
		return delegate.remove(key);
	}

	/**
	 * Removes cookie from list of domain.
	 * 
	 * @param domain
	 *            domain of cookie
	 * @param name
	 *            name of cookie
	 * @return true, if deleted
	 * @since SNC 1.0
	 */
	public boolean removeCookie(String domain, String name) {
		CookieList list = get(domain);
		if (list != null) {
			return list.remove(name);
		}
		return false;
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public void putAll(Map<? extends String, ? extends CookieList> m) {
		delegate.putAll(m);
	}

	/**
	 * Clears delegate and domain set.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public void clear() {
		domainSet.clear();
		delegate.clear();
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public Set<String> keySet() {
		return delegate.keySet();
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public Collection<CookieList> values() {
		return delegate.values();
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public Set<java.util.Map.Entry<String, CookieList>> entrySet() {
		return delegate.entrySet();
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public boolean equals(Object o) {
		return delegate.equals(o);
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public String toString() {
		return delegate.toString();
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return delegate.clone();
	}

}
