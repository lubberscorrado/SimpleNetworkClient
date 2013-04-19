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

package com.krobothsoftware.commons.network.value;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Class for holding Name-Value pairs.k
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 * @see com.krobothsoftware.commons.network.NetworkHelper#getPairs(String...)
 */
public class NameValuePair implements Serializable {
	private static final long serialVersionUID = -1049649389075000405L;
	private final String name;
	private final String value;

	/**
	 * Creates new Pair with name and value.
	 * 
	 * @param name
	 * @param value
	 * @since SNC 1.0
	 */
	public NameValuePair(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets pair in format <code>Name=Value</code>.
	 * 
	 * @return pair in format
	 * @since SNC 1.0
	 */
	public String getPair() {
		return name + "=" + value;
	}

	/**
	 * Gets the encoded pair from specified charset. <code>Name=Value</code>
	 * Only Value is encoded.
	 * 
	 * @param charSet
	 *            for encoding
	 * @return encoded pair
	 * @throws UnsupportedEncodingException
	 *             if charset isn't found
	 * @since SNC 1.0
	 */
	public String getEncodedPair(String charSet)
			throws UnsupportedEncodingException {
		return name + "=" + URLEncoder.encode(value, charSet);
	}

	/**
	 * Gets pair where value is in quotes. <code>Name="Value"</code>
	 * 
	 * @return quoted pair
	 * @since SNC 1.0
	 */
	public String getQuotedPair() {
		return name + "=\"" + value + "\"";
	}

	/**
	 * Gets pair name.
	 * 
	 * @return name
	 * @since SNC 1.0
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets pair value.
	 * 
	 * @return value
	 * @since SNC 1.0
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns string in format "NameValuePair [name='name'], value='value']".
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public final String toString() {
		return "NameValuePair [name=" + name + ", value=" + value + "]";
	}

	/**
	 * Computes hash from name and value.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public final int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (value == null ? 0 : value.hashCode());
		return result;
	}

	/**
	 * Checks name, value, and regular check statements.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof NameValuePair)) return false;
		NameValuePair other = (NameValuePair) obj;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		if (value == null) {
			if (other.value != null) return false;
		} else if (!value.equals(other.value)) return false;
		return true;
	}

}
