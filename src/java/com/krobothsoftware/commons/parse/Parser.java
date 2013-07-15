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

package com.krobothsoftware.commons.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Class for parsing components that parse for giving <code>T</code>
 * {@link Handler} type. Only one parser per top-level handler.
 * This class is not intended do any logic. Actual
 * <code>parsing components</code> do the work.
 *
 * @param <T> handler type
 * @param <C> Parsing component used directly with <tt>T</tt> Handler
 * @author Kyle Kroboth
 * @since COMMONS 1.1.0
 */
public abstract class Parser<T extends Handler, C> {

    /**
     * Logger for parser.
     *
     * @since COMMONS 1.1.0
     */
    protected final Logger log;

    /**
     * Handler listener. Needs null checks.
     *
     * @since COMMONS 1.1.0
     */
    protected ParserHandlerListener<T> handlerListener;

    /**
     * Component listener. Needs null checks.
     *
     * @since COMMONS 1.1.0
     */
    protected ParserComponentListener<C> componentListener;

    /**
     * Creates new parser with logger.
     *
     * @param loggerName logger name for parser
     * @since COMMONS 1.1.0
     */
    protected Parser(String loggerName) {
        log = LoggerFactory.getLogger(loggerName);
    }

    /**
     * Sets handler listener for parser.
     *
     * @param handlerListener listener or null to remove
     * @since COMMONS 1.1.0
     */
    public void setHandlerListener(ParserHandlerListener<T> handlerListener) {
        this.handlerListener = handlerListener;
    }

    /**
     * Sets component listener for parser.
     *
     * @param componentListener listener or null to remove
     * @since COMMONS 1.1.0
     */
    public void setComponentListener(
            ParserComponentListener<C> componentListener) {
        this.componentListener = componentListener;
    }

    /**
     * If {@link ParserHandlerListener} is not null, calls
     * {@link ParserHandlerListener#getHandler(Handler)} to retrieve handler.
     *
     * @param handler handler
     * @return new handler or original
     * @since COMMONS 1.1.0
     */
    protected T getHandler(T handler) {
        if (handlerListener != null) return handlerListener.getHandler(handler);
        return handler;
    }

    /**
     * Helper method for
     * {@link ParserComponentListener#componentInitialization(Object)}.
     *
     * @param component
     * @return component
     * @throws ParseException {@inheritDoc}
     * @since COMMONS 1.1.0
     */
    protected C initializeComponent(C component) throws ParseException {
        if (componentListener != null) return componentListener
                .componentInitialization(component);
        return component;
    }

    /**
     * Helper method for {@link ParserComponentListener#beforeParse(Object)}.
     *
     * @param component
     * @throws ParseException {@inheritDoc}
     * @since COMMONS 1.1.0
     */
    protected void beforeParse(C component) throws ParseException {
        if (componentListener != null) componentListener.beforeParse(component);
    }

    /**
     * Helper method for {@link ParserComponentListener#afterParse(Object)}.
     *
     * @param component
     * @throws ParseException {@inheritDoc}
     * @since COMMONS 1.1.0
     */
    protected void afterParse(C component) throws ParseException {
        if (componentListener != null) componentListener.afterParse(component);
    }

    /**
     * {@link Parser} parses <code>inputStream</code> for giving Handler type.
     * Calls {@link ParserComponentListener#beforeParse(Object)}, parses data,
     * then calls {@link ParserComponentListener#afterParse(Object)}.
     *
     * @param stream  inputstream to be parsed
     * @param handler Handler type for parser
     * @param charset charset for parsing components
     * @throws ParseException {@inheritDoc}
     * @since COMMONS 1.1.0
     */
    public abstract void parse(InputStream stream, T handler, String charset)
            throws ParseException;

}
