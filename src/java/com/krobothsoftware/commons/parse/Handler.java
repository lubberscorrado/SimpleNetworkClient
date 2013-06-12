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

import org.slf4j.Logger;

import com.krobothsoftware.commons.progress.NullProgressMonitor;
import com.krobothsoftware.commons.progress.ProgressMonitor;

/**
 * Handler to parse data.
 * 
 * @author Kyle Kroboth
 * @since COMMONS 1.0
 * @see Parser#parse(java.io.InputStream, Handler, String)
 */
public abstract class Handler {

	/**
	 * Monitor for progress. Will never be <code>null</code>.
	 * 
	 * @since COMMONS 1.0
	 */
	protected ProgressMonitor monitor;

	/**
	 * Parser which parsed the handler.
	 * 
	 * @since COMMONS 1.0
	 */
	protected Parser parser;

	/**
	 * Logger of {@link #parser}.
	 * 
	 * @since COMMONS 1.0
	 */
	protected Logger log;

	/**
	 * Creates new Handler with progress.
	 * 
	 * @param monitor
	 *            for progress
	 * @since COMMONS 1.0
	 */
	public Handler(ProgressMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * Creates new handler with no progress. Sets monitor as
	 * {@link NullProgressMonitor}.
	 * 
	 * @since COMMONS 1.0
	 */
	public Handler() {
		monitor = new NullProgressMonitor();
	}

	void setParser(Parser defaultParser) {
		parser = defaultParser;
	}

	void setLogger(Logger log) {
		this.log = log;
	}

}
