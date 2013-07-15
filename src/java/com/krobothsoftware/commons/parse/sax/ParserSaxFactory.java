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
import com.krobothsoftware.commons.parse.Parser;
import com.krobothsoftware.commons.parse.ParserInitializable;
import com.krobothsoftware.commons.parse.StopParseException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * SAX parser which creates a new instance of {@link SAXParser} every time a
 * HandlerSax is parsed.
 * <p/>
 * <p>
 * {@link com.krobothsoftware.commons.parse.ParserComponentListener#afterParse(Object)}
 * component argument will be null because {@link SAXParser} is no longer used.
 * <p/>
 * </p>
 *
 * @author Kyle Kroboth
 * @since COMMONS-PARSE-SAX 1.1.0
 */
public class ParserSaxFactory extends Parser<HandlerSax, SAXParser> implements
        ParserInitializable<SAXParserFactory> {
    private final String factoryClass;
    private SAXParserFactory factory;

    /**
     * Creates new parser for factory class.
     *
     * @param factoryClass class for implied {@link SAXParserFactory}
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    public ParserSaxFactory(String factoryClass) {
        super(ParserSaxFactory.class.getName());
        this.factoryClass = factoryClass;
    }

    /**
     * Creates {@link SAXParserFactory} for <code>FactoryClass</code> if null.
     *
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    @Override
    public SAXParserFactory getComponent() throws ParseException {
        if (factory == null) {
            try {
                factory = SAXParserFactory.newInstance(
                        factoryClass, ParserSax.class.getClassLoader());
            } catch (FactoryConfigurationError e) {
                throw new ParseException(e);
            }
        }

        return factory;
    }

    /**
     * Same as {@link ParserSax#getHandler(HandlerSax)}.
     *
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    @Override
    protected HandlerSax getHandler(HandlerSax handler) {
        handler = super.getHandler(handler);
        if (handler instanceof ExpressionBuilderFilter) handler = new HandlerExpressionBuilder(
                handler);
        else if (handler instanceof ExpressionFilter) handler = new HandlerExpression(
                handler);

        return handler;
    }

    /**
     * Same as {@link ParserSax#parse(java.io.InputStream, HandlerSax, String)} but a new instance
     * of {@link SAXParser} is created from factory.
     *
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    @Override
    public void parse(InputStream stream, HandlerSax handler, String charset)
            throws ParseException {
        log.debug("Parsing {}", handler.getClass().getSimpleName());

        try {
            if (factory == null) factory = getComponent();
            SAXParser parser = initializeComponent(factory.newSAXParser());
            beforeParse(parser);
            handler.setLogger(log);
            DefaultHandlerDelegate delegate = new DefaultHandlerDelegate(
                    getHandler(handler));
            InputSource inputSource = new InputSource(stream);
            inputSource.setEncoding(charset);

            parser.parse(inputSource, delegate);
        } catch (StopParseException e) {
            log.debug("Caught Stop Exception - {}", handler.getClass()
                    .getSimpleName());
        } catch (SAXException e) {
            throw new ParseException(e);
        } catch (IOException e) {
            throw new ParseException(e);
        } catch (ParserConfigurationException e) {
            throw new ParseException(e);
        } finally {
            afterParse(null);
        }

    }

}
