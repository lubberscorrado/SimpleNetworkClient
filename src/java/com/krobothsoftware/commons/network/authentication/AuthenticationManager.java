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

import java.net.URL;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krobothsoftware.commons.network.NetworkHelper;
import com.krobothsoftware.commons.network.RequestBuilder;

/**
 * Manager for authenticating connections with {@link AuthScope}.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 * @see com.krobothsoftware.commons.network.authentication.Authentication
 */
public class AuthenticationManager {
	protected final HashMap<AuthScope, Authentication> authMap;
	protected final NetworkHelper networkHelper;
	protected final Logger log;

	/**
	 * Instantiates a new authentication manager.
	 * 
	 * @param networkHelper
	 * @since SNC 1.0
	 */
	public AuthenticationManager(NetworkHelper networkHelper) {
		log = LoggerFactory.getLogger(AuthenticationManager.class);
		this.networkHelper = networkHelper;
		authMap = new HashMap<AuthScope, Authentication>();
	}

	/**
	 * Adds new authentication to manager. Will overwrite. Set <code>auth</code>
	 * null to remove authentication check from map.
	 * 
	 * @param scope
	 *            to check on connections.
	 * @param auth
	 *            authentication to set if scope matches
	 * @since SNC 1.0
	 */
	public void addAuthentication(AuthScope scope, Authentication auth) {
		if (scope == null) throw new IllegalArgumentException(
				"Scope may not be null");
		if (auth == null) {
			authMap.remove(scope);
			return;
		}
		auth.setNetworkHelper(networkHelper);
		auth.setLogger(log);
		authMap.put(scope, auth);
	}

	/**
	 * Gets Authentication by checking {@link AuthScope} with
	 * {@link RequestBuilder} URL.
	 * 
	 * @param request
	 *            url to check with scopes
	 * @return found Authentication or null
	 * @since SNC 1.0
	 */
	public Authentication getAuthentication(RequestBuilder request) {
		return getAuthentication(request.getUrl());
	}

	/**
	 * Gets Authentication by checking {@link AuthScope} with URL.
	 * 
	 * @param url
	 *            url to check with scopes
	 * @return found Authentication or null
	 * @since 1.1.0
	 */
	public Authentication getAuthentication(URL url) {
		// find scope by host
		String urlHost = url.getHost();
		for (AuthScope scope : authMap.keySet()) {
			if (scope.host.equalsIgnoreCase(urlHost)) {
				Authentication auth = authMap.get(scope);
				auth.setNetworkHelper(networkHelper);
				auth.setLogger(log);
				return auth;
			}
		}

		return null;
	}

	/**
	 * Clears map holding authentications.
	 * 
	 * @since SNC 1.0
	 */
	public void clear() {
		authMap.clear();
	}
}
