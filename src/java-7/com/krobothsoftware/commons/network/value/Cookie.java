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
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import com.krobothsoftware.commons.util.CommonUtils;
import com.krobothsoftware.commons.util.ThreadSafeDateUtil;

public class Cookie implements Serializable {
	private static final long serialVersionUID = -7856076968023905033L;

	private static final String[] DATE_FORMATS = {
			"EEE',' dd-MMM-yyyy HH:mm:ss 'GMT'",
			"EEE',' dd MMM yyyy HH:mm:ss 'GMT'" };
	private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

	private static final int[] WEEK_DATA = { 1, 1 };

	private final String name;
	private String value;
	private String domain;
	private String path;
	private long maxage;
	private final long created;
	private boolean secure;
	private boolean http;

	public Cookie(String name, String value) {
		this(null, name, value);
	}

	public Cookie(String domain, String name, String value) {
		this.name = name;
		this.value = value;
		this.domain = domain;
		path = null;
		maxage = -1;
		created = System.currentTimeMillis();
		secure = false;
		http = false;
	}

	Cookie(Builder builder) {
		name = builder.name;
		value = builder.value;
		domain = builder.domain;
		path = builder.path;
		maxage = builder.maxage;
		created = builder.created;
		secure = builder.secure;
		http = builder.http;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isExpired() {
		if (maxage == 0) return true;
		else if (maxage == -1) return false;
		else {
			long time = (System.currentTimeMillis() - created) / 1000;
			return time > maxage;
		}
	}

	public long getMaxage() {
		return maxage;
	}

	public void setMaxage(long expire) {
		this.maxage = expire;
	}

	public boolean isSession() {
		return maxage == -1;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public boolean isHttp() {
		return http;
	}

	public void setHttp(boolean http) {
		this.http = http;
	}

	public String getCookieString() {
		return name + "=" + value;
	}

	@Override
	public final String toString() {
		return String.format("%s %s", name, domain);
	}

	@Override
	public final int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + (domain == null ? 0 : domain.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (path == null ? 0 : path.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Cookie)) return false;
		Cookie other = (Cookie) obj;
		if (domain == null) {
			if (other.domain != null) return false;
		} else if (!domain.equals(other.domain)) return false;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		if (path == null) {
			if (other.path != null) return false;
		} else if (!path.equals(other.path)) return false;
		return true;
	}

	public static Cookie parseCookie(URL url, String cookieString) {
		Cookie.Builder builder = new Cookie.Builder();
		builder.setCreated(System.currentTimeMillis()).setMaxAge(-1);

		StringTokenizer tokenizer = new StringTokenizer(cookieString, ";");

		// get name and value
		String token = CommonUtils.trim(tokenizer.nextToken());
		int index = token.indexOf('=');
		if (index == -1) throw new IllegalArgumentException(
				"Invalid name value pair in cookie");
		builder.setName(token.substring(0, index));
		builder.setValue(token.substring(index + 1));

		while (tokenizer.hasMoreTokens()) {
			token = CommonUtils.trim(tokenizer.nextToken());

			// check if its a boolean
			index = token.indexOf('=');
			String name;
			String value = null;
			if (index != -1) {
				name = token.substring(0, index);
				value = token.substring(index + 1);
				if (value.length() == 0) value = null;
			} else {
				name = token;
			}
			setValue(builder, name, value);

		}

		if (builder.domain == null) builder.domain = url.getHost();

		if (builder.path == null) builder.path = "/";

		return builder.build();
	}

	private static void setValue(Builder builder, String name, String value) {
		switch (name.toLowerCase()) {
			case "domain":
				builder.setDomain(value);
				break;
			case "path":
				builder.setPath(value);
				break;
			case "expires":
				for (String format : DATE_FORMATS) {
					try {
						Date date = ThreadSafeDateUtil.parse(format, value,
								GMT, WEEK_DATA);
						builder.setMaxAge((date.getTime() - builder.created) / 1000);
						return;
					} catch (ParseException e) {
						// ignore. Will set max age to 0 if both formats don't
						// succeed.
					}
				}
				builder.setMaxAge(0);
				break;
			case "secure":
				builder.setSecure(true);
				break;
			case "httponly":
				builder.setHttp(true);
				break;
		}
	}

	public static class Builder {
		String name;
		String value;
		String domain;
		String path;
		long created;
		long maxage;
		boolean secure;
		boolean http;

		public Builder(String data) {
			name = data.substring(0, data.indexOf("="));
			value = data.substring(data.indexOf("=") + 1, data.length() - 1);
		}

		public Builder() {

		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setValue(String value) {
			this.value = value;
			return this;
		}

		public Builder setDomain(String domain) {
			this.domain = domain;
			return this;
		}

		public Builder setPath(String path) {
			this.path = path;
			return this;
		}

		public Builder setCreated(long created) {
			this.created = created;
			return this;
		}

		public Builder setMaxAge(long expire) {
			this.maxage = expire;
			return this;
		}

		public Builder setSecure(boolean isSecure) {
			this.secure = isSecure;
			return this;
		}

		public Builder setHttp(boolean isHttpOnly) {
			this.http = isHttpOnly;
			return this;
		}

		public Cookie build() {
			if (domain == null) throw new IllegalStateException(
					"'domain' may not be null");
			return new Cookie(this);
		}

	}

}
