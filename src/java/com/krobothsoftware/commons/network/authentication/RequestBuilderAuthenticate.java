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
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krobothsoftware.commons.network.Method;
import com.krobothsoftware.commons.network.NetworkHelper;
import com.krobothsoftware.commons.network.RequestBuilder;
import com.krobothsoftware.commons.network.Response;
import com.krobothsoftware.commons.network.ResponseAuthenticate;

/**
 * Builder for authorization HTTP connections.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 * @see com.krobothsoftware.commons.network.RequestBuilder
 * 
 */
public class RequestBuilderAuthenticate extends RequestBuilder {
	final Authentication auth;
	final String realm;
	int retry = 1;
	Logger authLog;

	/**
	 * Instantiates a new builder with Authorization.
	 * 
	 * @param method
	 *            HTTP method
	 * @param url
	 *            URL to request
	 * @param auth
	 *            to use for connection
	 * @since SNC 1.0
	 */
	public RequestBuilderAuthenticate(Method method, URL url,
			Authentication auth) {
		super(method, url);
		this.auth = auth;
		this.realm = null;
		authLog = LoggerFactory.getLogger(RequestBuilderAuthenticate.class);
		auth.setLogger(authLog);
	}

	/**
	 * Instantiates a new builder and uses <code>NetworkHelper</code>
	 * {@link AuthenticationManager}.
	 * 
	 * @param method
	 *            HTTP method
	 * @param url
	 *            URL to request
	 * @param realm
	 *            to check with scopes
	 * @since SNC 1.0
	 */
	public RequestBuilderAuthenticate(Method method, URL url, String realm) {
		super(method, url);
		auth = null;
		this.realm = realm;
		authLog = LoggerFactory.getLogger(RequestBuilderAuthenticate.class);
	}

	/**
	 * Instantiates a new builder and uses <code>NetworkHelper</code>
	 * {@link AuthenticationManager}
	 * 
	 * @param method
	 * @param url
	 * @since SNC 1.0
	 */
	public RequestBuilderAuthenticate(Method method, URL url) {
		this(method, url, (String) null);
	}

	/**
	 * Instantiates a new request builder from another.
	 * 
	 * @param builder
	 * @since SNC 1.0
	 */
	public RequestBuilderAuthenticate(RequestBuilderAuthenticate builder) {
		super(builder);
		auth = builder.auth;
		realm = builder.realm;
	}

	/**
	 * Limit for retrying connection if 401 status code is returned and
	 * Authentication supports authenticate. Resets Authentication if needed.
	 * Default retry limit is 1.
	 * 
	 * @param count
	 *            number of retries
	 * @return request builder
	 * @throws IllegalArgumentException
	 *             if count is less than zero
	 * @see com.krobothsoftware.commons.network.authentication.Authentication#reset()
	 * @see com.krobothsoftware.commons.network.authentication.Authentication#authenticateSupported()
	 * @since SNC 1.0
	 */
	public RequestBuilderAuthenticate retryLimit(int count) {
		if (count < 0) throw new IllegalArgumentException(
				"Count must be above zero");
		this.retry = count;
		return this;
	}

	/**
	 * Sends HTTP request based on request builder with authorization.
	 * Connection is not closed.
	 * 
	 * @see com.krobothsoftware.commons.network.RequestBuilder#execute(NetworkHelper)
	 * @since SNC 1.0
	 */
	@SuppressWarnings("resource")
	@Override
	public Response execute(NetworkHelper networkHelper) throws IOException {
		Authentication auth = this.auth;
		if (auth == null) auth = networkHelper.getAuthorizationManager()
				.getAuthentication(this);
		if (auth == null) {
			authLog.warn("No authentication found for {}", this);
			return super.execute(networkHelper);
		}

		// setup authentication
		try {
			auth.setNetworkHelper(networkHelper);
			auth.setup(this);
			Response response = super.execute(networkHelper);
			if (response instanceof ResponseAuthenticate
					&& auth.authenticateSupported()) {
				// authenticate connection
				// index starts at negative one for normal connection
				for (int i = -1; i < retry; i++) {
					response = auth.authenticate(this,
							(ResponseAuthenticate) response);
					if (!(response instanceof ResponseAuthenticate)) break;
					auth.reset();
				}
			}

			return response;
		} finally {
			auth.setNetworkHelper(null);
		}

	}

}
