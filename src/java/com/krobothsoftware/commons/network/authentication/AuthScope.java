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

/**
 * Scope for {@link Authentication} in {@link AuthenticationManager}. Currently
 * only <code>Host</code> is checked.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public class AuthScope {
	final String realm;
	final String host;

	/**
	 * Creates new scope with realm and host.
	 * 
	 * @param realm
	 *            authentication realm to check
	 * @param host
	 *            connection host to check
	 * @since SNC 1.0
	 */
	public AuthScope(String realm, String host) {
		this.realm = realm;
		this.host = host;
	}

	/**
	 * Creates new scope with host only.
	 * 
	 * @param host
	 *            connection host to check
	 * @since SNC 1.0
	 */
	public AuthScope(String host) {
		realm = null;
		this.host = host;
	}

	/**
	 * Gets realm of Authentication.
	 * 
	 * @return realm
	 * @since SNC 1.0
	 */
	public String getRealm() {
		return realm;
	}

	/**
	 * Gets host of Authentication.
	 * 
	 * @return host
	 * @since SNC 1.0
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Computes hash from host and realm.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((realm == null) ? 0 : realm.hashCode());
		return result;
	}

	/**
	 * Checks host and realm, and regular check statements.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof AuthScope)) return false;
		AuthScope other = (AuthScope) obj;
		if (host == null) {
			if (other.host != null) return false;
		} else if (!host.equals(other.host)) return false;
		if (realm == null) {
			if (other.realm != null) return false;
		} else if (!realm.equals(other.realm)) return false;
		return true;
	}

}
