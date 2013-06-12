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

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.krobothsoftware.commons.progress.ProgressMonitor;

/**
 * Handler for Html Cleaner types. Part of <code>COMMONS-EXT-HTMLCLEANER</code>.
 * 
 * @author Kyle Kroboth
 * @since COMMONS-EXT-HTMLCLEANER 1.0
 * @see com.krobothsoftware.commons.parse.ParserHtmlCleaner
 */
public abstract class HandlerHtmlCleaner extends Handler {

	/**
	 * html cleaner from parser.
	 * 
	 * @since COMMONS-EXT-HTMLCLEANER 1.0
	 */
	protected HtmlCleaner cleaner;

	/**
	 * Creates Html Cleaner handler with monitor.
	 * 
	 * @param monitor
	 * @since COMMONS-EXT-HTMLCLEANER 1.0
	 */
	public HandlerHtmlCleaner(ProgressMonitor monitor) {
		super(monitor);
	}

	/**
	 * Creates Html Cleaner handler with no progress.
	 * 
	 * @since COMMONS-EXT-HTMLCLEANER 1.0
	 */
	public HandlerHtmlCleaner() {
		super();
	}

	/**
	 * Parse html data starting with root tag node.
	 * 
	 * @param rootNode
	 *            top-level tagnode
	 * @throws ParseException
	 * @since COMMONS-EXT-HTMLCLEANER 1.0
	 */
	public abstract void parse(TagNode rootNode) throws ParseException;

	void setHtmlCleaner(HtmlCleaner cleaner) {
		this.cleaner = cleaner;
	}

}
