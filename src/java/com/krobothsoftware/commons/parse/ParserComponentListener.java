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

/**
 * Listener for when the parsing component is being used or created.
 *
 * @param <C> parsing component of same type for {@link Parser}
 * @author Kyle Kroboth
 * @since COMMONS 1.1.0
 */
public interface ParserComponentListener<C> {

    /**
     * Called when {@link Parser} component is created.
     *
     * @param component non-null parsing component from {@link Parser}
     * @return modified, new, or same instance
     * @throws ParseException {@inheritDoc}
     * @since COMMONS 1.1.0
     */
    C componentInitialization(C component) throws ParseException;

    /**
     * Called before {@link Parser} component is being used to parse a {@link Handler} object.
     *
     * @param component parsing component from {@link Parser}
     * @throws ParseException {@inheritDoc}
     * @since COMMONS 1.1.0
     */
    void beforeParse(C component) throws ParseException;

    /**
     * Called after {@link Parser} component parsed a {@link Handler} object.
     *
     * @param component parsing component from {@link Parser}
     * @throws ParseException {@inheritDoc}
     * @since COMMONS 1.1.0
     */
    void afterParse(C component) throws ParseException;

}
