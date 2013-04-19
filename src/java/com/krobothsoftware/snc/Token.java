package com.krobothsoftware.snc;

import java.io.Serializable;

import com.krobothsoftware.commons.network.value.CookieMap;

/**
 * Access tokens for sessions that identifies user.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public class Token implements Serializable {
	private static final long serialVersionUID = -3419625975537239000L;

	private final CookieMap cookies;

	/**
	 * Set up Token with cookies.
	 * 
	 * @param cookies
	 * @since SNC 1.0
	 */
	public Token(CookieMap cookies) {
		this.cookies = cookies;
	}

	/**
	 * Set up token with empty cookies.
	 * 
	 * @since SNC 1.0
	 */
	public Token() {
		this.cookies = new CookieMap();
	}

	/**
	 * Get cookies.
	 * 
	 * @return cookies
	 * @since SNC 1.0
	 */
	public final CookieMap getCookies() {
		return cookies;
	}

}
