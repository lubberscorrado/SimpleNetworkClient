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

/**
 *
 */
package com.krobothsoftware.commons.parse.sax;

/**
 * Filter with {@link HandlerSax#buildCharacters(String)} applied to
 * {@link HandlerSax} for {@link Expression} evaluation.
 *
 * @author Kyle Kroboth
 * @since COMMONS-PARSE-SAX 1.0.1
 */
public interface ExpressionBuilderFilter extends ExpressionFilter {

    /**
     * Called when the expression's path is reached. If there is more than on
     * path, <code>expr</code> will represent the path's index.
     *
     * @param expr    current path; starts at 0
     * @param content
     * @see HandlerSax#buildCharacters(String)
     * @since COMMONS-PARSE-SAX 1.0.1
     */
    public void buildCharacters(int expr, String content);

}
