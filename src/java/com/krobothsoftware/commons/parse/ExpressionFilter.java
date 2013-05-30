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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Filter applied to {@link HandlerSAX} for {@link Expression} evaluation.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 * @see ExpressionBuilderFilter
 */
public interface ExpressionFilter {

	/**
	 * Gets expression to evaluate.
	 * 
	 * @return expression
	 * @since SNC 1.0
	 */
	Expression getExpression();

	/**
	 * Called when the expression's path is reached. If there is more than one
	 * path, <code>expr</code> will represent the path's index.
	 * 
	 * @param expr
	 *            current path; starts at 0
	 * @param uri
	 *            the uri
	 * @param localName
	 * @param qName
	 * @param attributes
	 * @throws SAXException
	 * @see HandlerSAX#startElement(String, String, String, Attributes)
	 * @since SNC 1.0
	 */
	void startElement(int expr, String uri, String localName, String qName,
			Attributes attributes) throws SAXException;

	/**
	 * Called when the expression's path is reached. If there is more than one
	 * path, <code>expr</code> will represent the path's index.
	 * 
	 * @param expr
	 *            current path; starts at 0
	 * @param ch
	 * @param start
	 * @param length
	 * @throws SAXException
	 * @see HandlerSAX#characters(char[], int, int)
	 * @since SNC 1.0
	 * 
	 */
	void characters(int expr, char[] ch, int start, int length)
			throws SAXException;

	/**
	 * Called when the expression's path is reached. If there is more than one
	 * path, <code>expr</code> will represent the path's index.
	 * 
	 * @param expr
	 *            current path; starts at 0
	 * @param uri
	 * @param localName
	 * @param qName
	 * @return true, to go to next node in expression
	 * @throws SAXException
	 * @see HandlerSAX#endElement(String, String, String)
	 * @since SNC 1.0
	 * 
	 */
	boolean endElement(int expr, String uri, String localName, String qName)
			throws SAXException;

}
