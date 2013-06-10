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

package com.krobothsoftware.commons.network.authentication;

import java.io.IOException;

import org.slf4j.Logger;

import com.krobothsoftware.commons.network.NetworkHelper;
import com.krobothsoftware.commons.network.Response;
import com.krobothsoftware.commons.network.ResponseAuthenticate;

/**
 * Authentication is the base class for authorizations.
 * <p>
 * There are two steps to an Authentication. Prior to executing the connection,
 * {@link #setup(RequestBuilderAuthenticate)} is called to set up credentials.
 * After the connection has sent and if the status code is 401(Unauthorized),
 * {@link #authenticate(RequestBuilderAuthenticate, ResponseAuthenticate)} is
 * then called.
 * </p>
 * <p>
 * Authenticating after 401 response may only be called if
 * {@link #authenticateSupported()} is true.
 * </p>
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 * @see com.krobothsoftware.commons.network.authentication.AuthenticationManager
 */
public abstract class Authentication {

	/**
	 * Constant header for Authorization
	 * 
	 * @since SNC 1.0.1
	 */
	static final String HEADER_AUTHORIZATION = "Authorization";

	/**
	 * Network Helper set from {@link AuthenticationManager}.
	 * 
	 * @since SNC 1.0
	 */
	protected NetworkHelper networkHelper;

	/**
	 * Username for authentication.
	 * 
	 * @since SNC 1.0
	 */
	protected String username;

	/**
	 * Password for authentication.
	 * 
	 * @since SNC 1.0
	 */
	protected char[] password;

	/**
	 * Logger for authentications.
	 * 
	 * @since SNC 1.0
	 */
	protected Logger log;

	/*
	 * Empty constructor
	 * @since SNC 1.0
	 */
	protected Authentication() {

	}

	/**
	 * Instantiates a new authorization with username and password.
	 * 
	 * @param username
	 * @param password
	 * @deprecated use {@link #Authentication(String, char[])}. This constructor
	 *             will convert String <code>password</code> to a char array.
	 * @since SNC 1.0
	 */
	@Deprecated
	public Authentication(String username, String password) {
		this(username, password.toCharArray());
	}

	/**
	 * Instantiates a new authorization with username and password.
	 * 
	 * @param username
	 * @param password
	 *            in char array
	 * @since SNC 1.0.1
	 */
	public Authentication(String username, char[] password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Called when connection needs to be set up for authentication.
	 * 
	 * @param request
	 *            for setup
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @since SNC 1.0
	 */
	public abstract void setup(RequestBuilderAuthenticate request)
			throws IOException;

	/**
	 * Authenticates response if status code is <b>401</b>.
	 * 
	 * @param request
	 *            for authenticating.
	 * @param response
	 *            for authenticating. Must close.
	 * @return response after authenticating
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see #authenticateSupported()
	 * @since SNC 1.0
	 */
	public abstract Response authenticate(RequestBuilderAuthenticate request,
			ResponseAuthenticate response) throws IOException;

	/**
	 * Resets any info in Authentication object, but <b>not</b> credentials.
	 * 
	 * @since SNC 1.0
	 */
	public abstract void reset();

	/**
	 * Checks if authenticating is supported. Some authentication's only require
	 * to set password and username.
	 * 
	 * @return true, if suppported
	 * @see #authenticate(RequestBuilderAuthenticate, ResponseAuthenticate)
	 * @since SNC 1.0
	 */
	public boolean authenticateSupported() {
		return false;
	}

	void setNetworkHelper(NetworkHelper networkHelper) {
		this.networkHelper = networkHelper;
	}

	void setLogger(Logger logger) {
		log = logger;
	}

}
