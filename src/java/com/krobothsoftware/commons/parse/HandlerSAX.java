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

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.krobothsoftware.commons.progress.ProgressMonitor;

/**
 * SAX handler for Simple Api for Xml.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public abstract class HandlerSAX extends Handler implements EntityResolver,
		DTDHandler, ContentHandler, ErrorHandler {

	/**
	 * Tag element name for each
	 * {@link #startElement(String, String, String, Attributes)}.
	 * 
	 * @since SNC 1.0
	 */
	protected String startTag;

	/**
	 * Tag element for each {@link #endElement(String, String, String)}.
	 * 
	 * @since SNC 1.0.1
	 */
	protected String endTag;

	/**
	 * Used when retrieving characters to tell if start element was called.
	 * 
	 * @since SNC 1.0
	 */
	protected boolean calledStartElement;

	/**
	 * If true, will build all characters in
	 * {@link #characters(char[], int, int)} and call
	 * {@link #buildCharacters(String)} when element is closed. Used when
	 * handling multiple chunk calls.
	 * 
	 * @since SNC 1.0
	 */
	protected boolean buildChars;

	final StringBuilder sb = new StringBuilder();

	/**
	 * Creates new SAX Handler with progress.
	 * 
	 * @param monitor
	 *            for progress
	 * @since SNC 1.0
	 */
	public HandlerSAX(ProgressMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * Creates new SAX handler with no progress.
	 * 
	 * @since SNC 1.0
	 */
	public HandlerSAX() {
		super();
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public void setDocumentLocator(Locator locator) {
		// no op
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public void startDocument() throws SAXException {
		// no op
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public void endDocument() throws SAXException {
		// no op
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// no op
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// no op
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// no op
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		// no op
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public void skippedEntity(String name) throws SAXException {
		// no op
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public void notationDecl(String name, String publicId, String systemId)
			throws SAXException {
		// no op
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public void unparsedEntityDecl(String name, String publicId,
			String systemId, String notationName) throws SAXException {
		// no op
	}

	/**
	 * @since SNC 1.0
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {

		return null;
	}

	/**
	 * Sets start tag and <code>calledStartElement</code> to true.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		startTag = qName;
		calledStartElement = true;
	}

	/**
	 * Sets <code>calledStartElement</code> to false and if
	 * <code>buildChars</code> is true, append text.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		calledStartElement = false;
		if (buildChars) sb.append(ch, start, length);
	}

	/**
	 * Gets built characters from multiple chunks. Only if {@link #buildChars}
	 * is true.
	 * 
	 * @param content
	 * @since SNC 1.0
	 * @see #buildChars
	 * @since SNC 1.0
	 */
	public void buildCharacters(String content) {
		// no op
	}

	/**
	 * Sets end tag and If <code>buildChars</code> is true, call
	 * {@link #buildCharacters(String)} with built characters.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		endTag = qName;
		if (buildChars) {
			buildCharacters(sb.toString());
			sb.setLength(0);
		}
	}

	/**
	 * Logs error and throw SAXException.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public void error(SAXParseException e) throws SAXException {
		parser.log.error("Handler error [{}]", e.toString());
		throw e;
	}

	/**
	 * Logs warning and throw SAXException.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public void warning(SAXParseException e) throws SAXException {
		parser.log.warn("Handler warning [{}]", e.toString());
		throw e;
	}

	/**
	 * Logs fatal error and throw SAXException.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		parser.log.error("Handler warning [{}]", e.toString());
		throw e;
	}

	/**
	 * Gets correct qlocal from XML Handler. No effect on Java SE, but needed
	 * for Android implementation.
	 * 
	 * <p>
	 * <b>Java SE:</b> Returns <code>qName</code>.
	 * </p>
	 * 
	 * <p>
	 * <b>Android:</b> If Build version is below 2.1(7), <code>localName</code>
	 * is returned, otherwise <code>qName</code>.
	 * </p>
	 * 
	 * @param qName
	 *            qname
	 * @param localName
	 *            localName
	 * @return correct qLocal
	 * @since SNC 1.0
	 */
	protected final static String qLocal(String qName, String localName) {
		// TODO FIX on all implementation
		// always qName
		return qName;
	}

	/**
	 * Removes XML comments from string.
	 * 
	 * <pre>
	 * &lt;!-- comment --&gt;
	 * </pre>
	 * 
	 * @param content
	 *            string with comments
	 * @return comment free string
	 * @since SNC 1.0
	 */
	public static String removeComments(String content) {
		char[] ch = content.toCharArray();
		int offset = 0;
		int len = ch.length;
		char[] chars = new char[ch.length];
		for (int i = 0; i < len; i++) {
			if (ch[i] == '<' && (i + 1 < len && ch[i + 1] == '!')) {
				do
					i++;
				while (ch[i] != '>');
				continue; // skip '>'
			}
			chars[offset++] = ch[i];
		}

		return new String(chars, 0, offset);
	}
}
