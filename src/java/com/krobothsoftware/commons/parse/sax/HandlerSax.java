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

import com.krobothsoftware.commons.parse.Handler;
import org.xml.sax.*;

import java.io.IOException;

/**
 * SAX handler for {@link ParserSax}. Each handler must specify SAX parser type.
 *
 * @author Kyle Kroboth
 * @since COMMONS-PARSE-SAX 1.0
 */
public abstract class HandlerSax extends Handler implements EntityResolver,
        DTDHandler, ContentHandler, ErrorHandler {

    /**
     * Tag element name for each
     * {@link #startElement(String, String, String, Attributes)}.
     *
     * @since COMMONS-PARSE-SAX 1.0
     */
    protected String startTag;

    /**
     * Tag element for each {@link #endElement(String, String, String)}.
     *
     * @since COMMONS-PARSE-SAX 1.0.1
     */
    protected String endTag;

    /**
     * Used when retrieving characters to tell if start element was called.
     *
     * @since COMMONS-PARSE-SAX 1.0
     */
    protected boolean calledStartElement;

    /**
     * If true, will build all characters in
     * {@link #characters(char[], int, int)} and call
     * {@link #buildCharacters(String)} when element is closed. Used when
     * handling multiple chunk calls.
     *
     * @since COMMONS-PARSE-SAX 1.0
     */
    protected boolean buildChars;

    final StringBuilder sb = new StringBuilder();

    /**
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public void setDocumentLocator(Locator locator) {
        // no op
    }

    /**
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public void startDocument() throws SAXException {
        // no op
    }

    /**
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public void endDocument() throws SAXException {
        // no op
    }

    /**
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        // no op
    }

    /**
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        // no op
    }

    /**
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        // no op
    }

    /**
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        // no op
    }

    /**
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public void skippedEntity(String name) throws SAXException {
        // no op
    }

    /**
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public void notationDecl(String name, String publicId, String systemId)
            throws SAXException {
        // no op
    }

    /**
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public void unparsedEntityDecl(String name, String publicId,
                                   String systemId, String notationName) throws SAXException {
        // no op
    }

    /**
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {

        return null;
    }

    /**
     * Sets start tag and <code>calledStartElement</code> to true.
     *
     * @since COMMONS-PARSE-SAX 1.0
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
     * @since COMMONS-PARSE-SAX 1.0
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
     * @see #buildChars
     * @since COMMONS-PARSE-SAX 1.0
     */
    public void buildCharacters(String content) {
        // no op
    }

    /**
     * Sets end tag and If <code>buildChars</code> is true, call
     * {@link #buildCharacters(String)} with built characters.
     *
     * @since COMMONS-PARSE-SAX 1.0
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
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public void error(SAXParseException e) throws SAXException {
        log.error("Handler error [{}]", e.toString());
        throw e;
    }

    /**
     * Logs warning and throw SAXException.
     *
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public void warning(SAXParseException e) throws SAXException {
        log.warn("Handler warning [{}]", e.toString());
        throw e;
    }

    /**
     * Logs fatal error and throw SAXException.
     *
     * @since COMMONS-PARSE-SAX 1.0
     */
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        log.error("Handler fatal error [{}]", e.toString());
        throw e;
    }

    /**
     * Gets correct qlocal from XML Handler. No effect on Java SE, but needed
     * for Android implementation.
     * <p/>
     * <p>
     * <b>Java SE:</b> Returns <code>qName</code>.
     * </p>
     * <p/>
     * <p>
     * <b>Android:</b> If Build version is below 2.1(7), <code>localName</code>
     * is returned, otherwise <code>qName</code>.
     * </p>
     *
     * @param qName     qname
     * @param localName localName
     * @return correct qLocal
     * @since COMMONS-PARSE-SAX 1.0
     */
    protected final static String qLocal(String qName, String localName) {
        // TODO FIX on all implementation
        // always qName
        return qName;
    }

    /**
     * Removes XML comments from string.
     * <p/>
     * <pre>
     * &lt;!-- comment --&gt;
     * </pre>
     *
     * @param content string with comments
     * @return comment free string
     * @since COMMONS-PARSE-SAX 1.0
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
