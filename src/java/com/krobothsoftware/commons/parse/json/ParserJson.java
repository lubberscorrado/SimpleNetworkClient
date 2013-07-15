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

package com.krobothsoftware.commons.parse.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.krobothsoftware.commons.parse.ParseException;
import com.krobothsoftware.commons.parse.Parser;
import com.krobothsoftware.commons.parse.ParserInitializable;
import com.krobothsoftware.commons.util.CommonUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Json parser for {@link HandlerJson} types. Uses {@link JsonParser} from
 * Jackson Json library for parser component.
 *
 * @author Kyle Kroboth
 * @since COMMONS-PARSE-JSON 1.1.0
 */
public class ParserJson extends Parser<HandlerJson, JsonParser> implements
        ParserInitializable<JsonFactory> {
    private JsonFactory factory;

    /**
     * Creates new parser.
     *
     * @since COMMONS-PARSE-JSON 1.1.0
     */
    public ParserJson() {
        super(ParserJson.class.getName());
    }

    /**
     * If {@link JsonFactory} is null, will create it.
     *
     * @return Json Factory instance
     * @since COMMONS-PARSE-JSON 1.1.0
     */
    @Override
    public JsonFactory getComponent() throws ParseException {
        if (factory == null) factory = new JsonFactory();
        return factory;
    }


    /**
     * Creates new {@link JsonParser} object from Factory and uses it on Handler.
     *
     * @since COMMONS-PARSE-JSON 1.1.0
     */
    @Override
    public void parse(InputStream stream, HandlerJson handler, String charset)
            throws ParseException {
        if (factory == null) factory = new JsonFactory();
        JsonParser parser = null;
        try {
            parser = initializeComponent(factory.createJsonParser(stream));
            handler.setLogger(log);
            handler.setJsonParser(parser);
            getHandler(handler).parse();
        } catch (IOException e) {
            throw new ParseException(e);
        } finally {
            CommonUtils.closeQuietly(parser);
        }

    }

}
