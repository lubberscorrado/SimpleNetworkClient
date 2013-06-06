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

package com.krobothsoftware.commons.parse;

import org.xml.sax.helpers.DefaultHandler;

/**
 * Exception for stopping parsing in {@link DefaultHandler} which is caught
 * internally. As there is no way of stopping a SAX parser, throwing an
 * exception is the <i>only</i> way. Does not fill in stack trace.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public final class StopException extends RuntimeException {
	private static final long serialVersionUID = 5964448482034581273L;

	/**
	 * Returns current object so stack trace isn't filled.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

}
