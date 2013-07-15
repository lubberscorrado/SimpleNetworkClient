/*
 * Copyright 2013 Kroboth Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.krobothsoftware.commons.parse.sax;

import java.io.IOException;

import com.krobothsoftware.commons.parse.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import android.os.Build;

import com.krobothsoftware.commons.progress.ProgressMonitor;

public abstract class HandlerSAX extends Handler implements EntityResolver,
		DTDHandler, ContentHandler, ErrorHandler {
	private static final boolean ALT;
	protected String startTag;
	protected String endTag;
	protected boolean calledStartElement;
	protected boolean buildChars;
	final StringBuilder sb = new StringBuilder();

	public HandlerSAX(ProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public HandlerSAX() {
		super();
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		// no op
	}

	@Override
	public void startDocument() throws SAXException {
		// no op
	}

	@Override
	public void endDocument() throws SAXException {
		// no op
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// no op
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// no op
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// no op
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		// no op
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		// no op
	}

	@Override
	public void notationDecl(String name, String publicId, String systemId)
			throws SAXException {
		// no op
	}

	@Override
	public void unparsedEntityDecl(String name, String publicId,
			String systemId, String notationName) throws SAXException {
		// no op
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {

		return null;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		startTag = qLocal(qName, localName);
		calledStartElement = true;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		calledStartElement = false;
		if (buildChars) sb.append(ch, start, length);
	}

	public void buildCharacters(String content) {
		// no op
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		endTag = qLocal(qName, localName);
		if (buildChars) {
			buildCharacters(sb.toString());
			sb.setLength(0);
		}
	}

    @Override
    public void error(SAXParseException e) throws SAXException {
        log.error("Handler error [{}]", e.toString());
        throw e;
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        log.warn("Handler warning [{}]", e.toString());
        throw e;
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        log.error("Handler fatal error [{}]", e.toString());
        throw e;
    }

	protected final static String qLocal(String qName, String localname) {
		if (ALT) return localname;
		return qName;
	}

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

	static {
		int build = Build.VERSION.SDK_INT;
		// android 2.1
		if (build <= Build.VERSION_CODES.ECLAIR_MR1) ALT = true;
		else
			ALT = false;
	}
}
