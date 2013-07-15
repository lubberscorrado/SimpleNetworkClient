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

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

/**
 * Delegate DefaultHandler for {@link HandlerSax} implementations. SAXParsers
 * only allow <code>DefaultHandler</code> instances and <code>HandlerSAX</code>
 * implements the interfaces instead.
 *
 * @author Kyle Kroboth
 * @since COMMONS-PARSE-SAX-PARSE-SAX 1.0
 */
public final class DefaultHandlerDelegate extends DefaultHandler {
    private final HandlerSax handler;

    public DefaultHandlerDelegate(HandlerSax handler) {
        this.handler = handler;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws IOException, SAXException {
        return handler.resolveEntity(publicId, systemId);
    }

    @Override
    public void notationDecl(String name, String publicId, String systemId)
            throws SAXException {
        handler.notationDecl(name, publicId, systemId);
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId,
                                   String systemId, String notationName) throws SAXException {
        handler.unparsedEntityDecl(name, publicId, systemId, notationName);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        handler.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        handler.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        handler.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        handler.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        handler.endPrefixMapping(prefix);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        handler.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        handler.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        handler.characters(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        handler.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        handler.processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        handler.skippedEntity(name);
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        handler.warning(e);
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        handler.error(e);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        handler.fatalError(e);
    }

    @Override
    public String toString() {
        return handler.toString();
    }

}
