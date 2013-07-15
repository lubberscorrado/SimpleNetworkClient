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

import com.krobothsoftware.commons.parse.Handler;
import com.krobothsoftware.commons.parse.ParseException;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * Handler for Html Cleaner types.
 *
 * @author Kyle Kroboth
 * @see com.krobothsoftware.commons.parse.html.ParserHtml
 * @since COMMONS-PARSE-HTML 1.0
 */
public abstract class HandlerHtml extends Handler {

    /**
     * html cleaner from parser.
     *
     * @since COMMONS-PARSE-HTML 1.0
     */
    protected HtmlCleaner cleaner;

    /**
     * Parse html data starting with root tag node.
     *
     * @param rootNode top-level tagnode
     * @throws ParseException
     * @since COMMONS-PARSE-HTML 1.0
     */
    public abstract void parse(TagNode rootNode) throws ParseException;

    void setHtmlCleaner(HtmlCleaner cleaner) {
        this.cleaner = cleaner;
    }

}
