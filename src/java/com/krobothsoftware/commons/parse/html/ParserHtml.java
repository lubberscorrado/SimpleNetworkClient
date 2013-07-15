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

package com.krobothsoftware.commons.parse.html;

import com.krobothsoftware.commons.parse.ParseException;
import com.krobothsoftware.commons.parse.Parser;
import com.krobothsoftware.commons.parse.ParserInitializable;
import org.htmlcleaner.HtmlCleaner;

import java.io.IOException;
import java.io.InputStream;


/**
 * DOM style HTML parser for {@link HandlerHtml} type. Uses {@link HtmlCleaner}
 * from Html Cleaner lib for parsing component.
 *
 * @author Kyle Kroboth
 * @since COMMONS-PARSE-HTML 1.1.0
 */
public class ParserHtml extends Parser<HandlerHtml, HtmlCleaner> implements
        ParserInitializable<HtmlCleaner> {
    private HtmlCleaner cleaner;

    /**
     * Creates new parser.
     *
     * @since COMMONS-PARSE-HTML 1.1.0
     */
    public ParserHtml() {
        super(ParserHtml.class.getName());
    }

    /**
     * @since COMMONS-PARSE-HTML 1.1.0
     */
    @Override
    public HtmlCleaner getComponent() throws ParseException {
        if (cleaner == null) cleaner = initializeComponent(new HtmlCleaner());
        return cleaner;
    }

    /**
     * @since COMMONS-PARSE-HTML 1.1.0
     */
    @Override
    public void parse(InputStream stream, HandlerHtml handler, String charset)
            throws ParseException {
        if (cleaner == null) cleaner = getComponent();
        try {
            handler.setLogger(log);
            handler.setHtmlCleaner(cleaner);
            handler.parse(cleaner.clean(stream, charset));
        } catch (IOException e) {
            throw new ParseException(e);
        }

    }

}
