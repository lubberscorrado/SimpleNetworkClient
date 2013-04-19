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

/**
 * Parser can init <code>parsers</code> before they are needed. <i>Usually</i>,
 * a Parser initiates parsing <code>components</code> when needed depending on
 * {@link Handler} type.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public interface ParserInitializable {

	/**
	 * Initializes parser components and <b>Ignores</b> any throwables. May log
	 * report. If there is an error, it will arise when parser tries to create
	 * it again when needed.
	 * 
	 * <p>
	 * Do not call this method if you don't need access to parsers, or
	 * pre-initiation. 
	 * </p>
	 * 
	 * @since SNC 1.0
	 */
	void init();

}
