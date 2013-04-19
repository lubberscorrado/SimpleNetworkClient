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

import java.util.ArrayList;

/**
 * Limited XPath expression for SAX handler implementations. Each expression
 * consists of nodes separated by a forward slash. Features include tag,
 * attribute name and value, and index. All strings are interned.
 * 
 * <pre>
 * <table border="1">
 * <tr>
 * <td>
 * /div
 * </td>
 * <td>
 * Tag
 * </td>
 * </tr>
 * <tr>
 * <td>
 * /div[@class]
 * </td>
 * <td>
 * Attribute
 * </td>
 * <tr>
 * <td>
 * /div[@class='exampleClass']
 * </td>
 * <td>
 * Attribute value
 * </td>
 * <tr>
 * <td>
 * /div[1]
 * </td>
 * <td>
 * Index. Starts at 1
 * </td>
 * </tr>
 * </table>
 * </pre>
 * 
 * Special tags
 * 
 * <pre>
 * <table border="1">
 * <tr>
 * <td>
 * /&&
 * </td>
 * <td>
 * Separates multiple paths
 * </td>
 * </tr>
 * </table>
 * </pre>
 * 
 * Example
 * 
 * <pre>
 * Expression expr = Expression
 * 		.parse(&quot;/div[@class='exampleClass'][2]/img[@alt]/&amp;&amp;/table&quot;);
 * </pre>
 * 
 * @see ExpressionFilter
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public final class Expression {
	private final ArrayList<Node> stack;
	private int index;

	/**
	 * Parses expression using limited xpath syntax. Will <b>not</b> check for
	 * syntax errors.
	 * 
	 * @param expr
	 *            formatted path
	 * @return the expression
	 * @since SNC 1.0
	 */
	public static Expression parse(String expr) {
		return new Expression(expr);
	}

	private Expression(String expr) {
		stack = new ArrayList<Node>();
		char[] ch = expr.toCharArray();
		int start = 0;
		do
			start = nextNode(ch, start);
		while (start != -1);
		stack.trimToSize();
	}

	Node popNode() {
		if (index >= stack.size()) return null;
		return stack.get(index++);
	}

	void reset() {
		index = 0;
	}

	private int nextNode(char[] ch, int start) {
		Node node = new Node();
		int old = start;
		start++;
		for (; start < ch.length; start++) {
			if (ch[start] == '[') {
				if (node.tag == null) node.tag = new String(ch, old + 1, start
						- old - 1).intern();
				old = start;
				do
					start++;
				while (ch[start] != '=' && ch[start] != ']');

				if (ch[old + 1] == '@') {
					node.attrib = new String(ch, old + 2, start - old - 2)
							.intern();
					if (ch[start] == '=') {
						old = ++start;
						do
							start++;
						while (ch[start] != '\'');
						node.attribV = new String(ch, old + 1, start - old - 1)
								.intern();
					}
				} else {
					node.index = Integer.parseInt(new String(ch, old + 1, start
							- old - 1));
				}
				continue;
			} else if (ch[start] == '/') {
				if (node.tag == null) node.tag = new String(ch, old + 1, start
						- old - 1).intern();
				stack.add(node);
				return start;
			}
		}

		if (start == ch.length) {
			if (node.tag == null) node.tag = new String(ch, old + 1, start
					- old - 1).intern();
			stack.add(node);
		}

		return -1;
	}

	static class Node {
		String tag;
		String attrib;
		String attribV;
		int index = 1;
	}
}
