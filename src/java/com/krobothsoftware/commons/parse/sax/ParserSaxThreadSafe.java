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

import com.krobothsoftware.commons.parse.ParseException;
import com.krobothsoftware.commons.parse.RuntimeParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

/**
 * Thread safe implementation of {@link ParserSax}. Uses ThreadLocal of
 * {@link SAXParser}.
 *
 * @author Kyle Kroboth
 * @since COMMONS-PARSE-SAX 1.1.0
 */
public class ParserSaxThreadSafe extends ParserSax {
    private ThreadLocal<SAXParser> threadLocalParser;

    /**
     * Creates new parser for factory class.
     *
     * @param factoryClass class for implied {@link SAXParserFactory}
     * @param resettable   if SAX parser component can be resettable.
     *                     {@link SAXParser#reset()}.
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    public ParserSaxThreadSafe(String factoryClass, boolean resettable) {
        super(factoryClass, resettable);
    }

    /**
     * Creates new parser for factory class with resettable as true.
     *
     * @param factoryClass class for implied {@link SAXParserFactory}
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    public ParserSaxThreadSafe(String factoryClass) {
        super(factoryClass);
    }

    /**
     * Use with caution because if it may not be the same instance if
     * called on another thread.
     *
     * @return thread local SAXParser
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    @Override
    public SAXParser getComponent() {
        if (threadLocalParser != null) return threadLocalParser.get();
        return null;
    }

    /**
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    @Override
    public void parse(InputStream stream, HandlerSax handler, String charset)
            throws ParseException {
        log.debug("Parsing {}", handler.getClass().getSimpleName());

        SAXParser parser = null;
        try {
            parser = getThreadLocalParser();
            parse(parser, stream, handler, charset);
        } finally {
            if (resettable) parser.reset();
        }
    }

    private SAXParser getThreadLocalParser() throws ParseException {
        SAXParser parser;
        if (threadLocalParser == null) threadLocalParser = new ThreadLocalParser();

        try {
            parser = threadLocalParser.get();
        } catch (RuntimeParseException e) {
            throw new ParseException(e);
        }

        return parser;
    }

    class ThreadLocalParser extends ThreadLocal<SAXParser> {

        @Override
        protected SAXParser initialValue() {
            try {
                return initializeComponent(SAXParserFactory.newInstance(factoryClass,
                        ParserSax.class.getClassLoader()).newSAXParser());
            } catch (ParserConfigurationException e) {
                throw new RuntimeParseException(e);
            } catch (SAXException e) {
                throw new RuntimeParseException(e);
            } catch (ParseException e) {
                throw new RuntimeParseException(e);
            }
        }

    }

}
