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

package com.krobothsoftware.commons.parse.sax.tagsoup;

import com.krobothsoftware.commons.parse.ParseException;
import com.krobothsoftware.commons.parse.ParserComponentListener;
import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;

/**
 * Sets {@link XMLReader} <code>schemaProperty</code> value to static reference
 * {@link HTMLSchema}.
 * <p/>
 * https://groups.google.com/d/msg/tagsoup-friends/SF05wZ6sMf0/rETV4pee8GwJ
 *
 * @author Kyle Kroboth
 * @since COMMONS-PARSE-SAX-TAGSOUP 1.1.0
 */
public class TagSoupComponentHandler implements ParserComponentListener<SAXParser> {
    private static final Schema HTML_SCHEMA = new HTMLSchema();
    private ParserComponentListener<SAXParser> delegate;

    /**
     * Creates TagSoup component handler.
     *
     * @param componentListener optional delegate listener'
     * @since COMMONS-PARSE-SAX-TAGSOUP 1.1.0
     */
    public TagSoupComponentHandler(ParserComponentListener<SAXParser> componentListener) {
        this.delegate = componentListener;
    }

    /**
     * Creates TagSoup component handler.
     *
     * @since COMMONS-PARSE-SAX-TAGSOUP 1.1.0
     */
    public TagSoupComponentHandler() {

    }

    /**
     * Calls delegate listener first, then applies <code>schemaProperty</code>
     * property.
     *
     * @since COMMONS-PARSE-SAX-TAGSOUP 1.1.0
     */
    @Override
    public SAXParser componentInitialization(SAXParser component)
            throws ParseException {
        if (delegate != null)
            component = delegate.componentInitialization(component);
        try {
            XMLReader xmlReader = component.getXMLReader();
            xmlReader.setProperty(Parser.schemaProperty, HTML_SCHEMA);
            xmlReader.setFeature(Parser.bogonsEmptyFeature, true);
        } catch (SAXException e) {
            throw new ParseException(e);
        }

        return component;
    }

    /**
     * @since COMMONS-PARSE-SAX-TAGSOUP 1.1.0
     */
    @Override
    public void beforeParse(SAXParser component) throws ParseException {
        if (delegate != null) delegate.beforeParse(component);
        // no op
    }

    /**
     * @since COMMONS-PARSE-SAX-TAGSOUP 1.1.0
     */
    @Override
    public void afterParse(SAXParser component) throws ParseException {
        if (delegate != null) delegate.afterParse(component);
        // no op
    }

}
