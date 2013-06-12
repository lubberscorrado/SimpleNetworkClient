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

import com.krobothsoftware.commons.progress.ProgressMonitor;

/**
 * Base Handler for XML data.
 * 
 * @author Kyle Kroboth
 * @since COMMONS 1.0
 */
public abstract class HandlerXml extends HandlerSAX {

	/**
	 * Creates new Xml Handler with progress.
	 * 
	 * @param monitor
	 *            for progress
	 * @since COMMONS 1.0
	 */
	public HandlerXml(final ProgressMonitor monitor) {
		super(monitor);
	}

	/**
	 * Creates new Html handler with no progress.
	 * 
	 * @since COMMONS 1.0
	 */
	public HandlerXml() {
		super();
	}

}
