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
 * SAX Parser for {@link HandlerSax} type. Uses {@link SAXParser} as the parsing
 * component. Use {@link ParserSaxThreadSafe} for thread safe implementation or
 * {@link ParserSaxFactory} for a new <code>SAXParser</code> instance every time
 * a <code>Handler</code> is parsed.
 * <p/>
 * <p>
 * An optional Simple XPATH {@link Expression} will be evaluated when HandlerSax
 * implements {@link ExpressionFilter} or {@link ExpressionBuilderFilter}.
 * </p>
 *
 * @author Kyle Kroboth
 * @since COMMONS-PARSE-SAX 1.1.0
 */
public class ParserSax extends Parser<HandlerSax, SAXParser> implements
        ParserInitializable<SAXParser> {

    /**
     * SAX Factory for Java's XML parser.
     * <p/>
     * <p>
     * <code>
     * com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl
     * </code>
     * </p>
     *
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    public static final String FACTORY_JAVAX_XML = "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl";

    /**
     * SAX Factory for TagSoup's HTML Parser.
     * <p/>
     * <p>
     * <code>
     * org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
     * </code>
     * </p>
     *
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    public static final String FACTORY_TAGSOUP = "org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl";

    /**
     * Java's XML Parser resettable constant. <code>True</code>.
     *
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    public static final boolean RESETTABLE_JAVAX_XML = true;

    /**
     * TagSoup's HTML Parser resettable constant. <code>False</code>.
     *
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    public static final boolean RESETTABLE_TAGSOUP = false;

    final String factoryClass;
    final boolean resettable;
    private SAXParser parser;

    /**
     * Creates new parser for factory class.
     *
     * @param factoryClass class for implied {@link SAXParserFactory}
     * @param resettable   if SAX parser component can be resettable.
     *                     {@link SAXParser#reset()}.
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    public ParserSax(String factoryClass, boolean resettable) {
        super(ParserSax.class.getName());
        this.factoryClass = factoryClass;
        this.resettable = resettable;
    }

    /**
     * Creates new parser for factory class with resettable as true.
     *
     * @param factoryClass class for implied {@link SAXParserFactory}
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    public ParserSax(String factoryClass) {
        this(factoryClass, true);
    }

    /**
     * Create {@link SAXParser} for <code>FactoryClass</code> only if null.
     *
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    @Override
    public SAXParser getComponent() throws ParseException {
        if (parser == null) {
            try {
                parser = initializeComponent(SAXParserFactory.newInstance(
                        factoryClass, ParserSax.class.getClassLoader())
                        .newSAXParser());
            } catch (FactoryConfigurationError e) {
                throw new ParseException(e);
            } catch (ParserConfigurationException e) {
                throw new ParseException(e);
            } catch (SAXException e) {
                throw new ParseException(e);
            }
        }

        return parser;

    }

    /**
     * Calls super method and wraps HandlerSax into a new HandlerExpression if
     * it implements {@link ExpressionFilter} or {@link ExpressionBuilderFilter}.
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
     * Wraps HandlerSAX in {@link DefaultHandlerDelegate} and calls
     * {@link SAXParser#parse(InputSource, org.xml.sax.helpers.DefaultHandler)}.
     * If resettable is true, will call {@link SAXParser#reset()}. Will catch
     * {@link StopParseException}.
     *
     * @since COMMONS-PARSE-SAX 1.1.0
     */
    @Override
    public void parse(InputStream stream, HandlerSax handler, String charset)
            throws ParseException {
        log.debug("Parsing {}", handler.getClass().getSimpleName());
        if (parser == null) parser = getComponent();
        parse(parser, stream, handler, charset);
    }

    void parse(SAXParser parser, InputStream stream, HandlerSax handler, String charset)
            throws ParseException {
        try {
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
        } finally {
            if (resettable) parser.reset();
            afterParse(parser);
        }
    }

}
