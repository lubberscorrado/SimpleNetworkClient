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
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Delegate DefualtHandler for {@link HandlerSAX} implementations. SAXParsers
 * only allow <code>DefaultHandler</code> instances and <code>HandlerSAX</code>
 * implements the interfaces instead.
 * 
 * @author Kyle Kroboth
 * @since COMMONS 1.0
 */
public final class DefaultHandlerDelegate extends DefaultHandler {
	private final HandlerSAX handler;

	/**
	 * Create delegate for handler.
	 * 
	 * @param handler
	 *            delegate handler
	 * @since COMMONS 1.0
	 */
	public DefaultHandlerDelegate(HandlerSAX handler) {
		this.handler = handler;
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException, SAXException {
		return handler.resolveEntity(publicId, systemId);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void notationDecl(String name, String publicId, String systemId)
			throws SAXException {
		handler.notationDecl(name, publicId, systemId);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void unparsedEntityDecl(String name, String publicId,
			String systemId, String notationName) throws SAXException {
		handler.unparsedEntityDecl(name, publicId, systemId, notationName);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void setDocumentLocator(Locator locator) {
		handler.setDocumentLocator(locator);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void startDocument() throws SAXException {
		handler.startDocument();
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void endDocument() throws SAXException {
		handler.endDocument();
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		handler.startPrefixMapping(prefix, uri);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		handler.endPrefixMapping(prefix);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		handler.startElement(uri, localName, qName, attributes);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		handler.endElement(uri, localName, qName);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		handler.characters(ch, start, length);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		handler.ignorableWhitespace(ch, start, length);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		handler.processingInstruction(target, data);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void skippedEntity(String name) throws SAXException {
		handler.skippedEntity(name);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void warning(SAXParseException e) throws SAXException {
		handler.warning(e);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void error(SAXParseException e) throws SAXException {
		handler.error(e);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		handler.fatalError(e);
	}

	/**
	 * @since COMMONS 1.0
	 */
	@Override
	public String toString() {
		return handler.toString();
	}

}
