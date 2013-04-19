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

package com.krobothsoftware.snc.sen.psn.us.internal;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.krobothsoftware.commons.parse.Expression;
import com.krobothsoftware.commons.parse.ExpressionFilter;
import com.krobothsoftware.commons.parse.HandlerHtml;
import com.krobothsoftware.commons.parse.StopException;

/**
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN-US 1.0
 */
public class HandlerHtmlFriend extends HandlerHtml implements ExpressionFilter {
	private static final Expression expr = Expression
			.parse("/div[@class='myfriendscontent']/div");
	private final List<String> list;

	public HandlerHtmlFriend() {
		list = new ArrayList<String>();
	}

	public List<String> getFriendList() {
		return list;
	}

	@Override
	public Expression getExpression() {
		return expr;
	}

	@Override
	public void startElement(int expr, String uri, String localName,
			String qName, Attributes attributes) throws SAXException {

		String str;
		if (startTag.equals("div") && attributes.getLength() == 2) {
			str = attributes.getValue("class");
			if (str == null) return;
			if (str.startsWith("recentitems")) {
				list.add(attributes.getValue("id"));
			}
		} else if (startTag.equals("script")) {
			throw new StopException();
		}
	}

	@Override
	public void characters(int expr, char[] ch, int start, int length)
			throws SAXException {

	}

	@Override
	public boolean endElement(int expr, String uri, String localName,
			String qName) throws SAXException {
		return false;
	}

}
