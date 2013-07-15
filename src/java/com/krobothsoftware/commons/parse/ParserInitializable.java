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
 * A <code>Parser</code> may not initiate <code>Parsing Components</code> until
 * needed. The interface parsing component doesn't have to be {@link Parser} Component type.
 * An example is having a factory class. {@link com.krobothsoftware.commons.parse.sax.ParserSaxFactory} does this.
 *
 * @param <C> parser component to be initialized
 * @author Kyle Kroboth
 * @since COMMONS 1.1.0
 */
public interface ParserInitializable<C> {

    /**
     * Gets parser component and initiates it if has not been created.
     *
     * @return parser component
     * @throws ParseException {@inheritDoc}
     * @since COMMONS 1.1.0
     */
    C getComponent() throws ParseException;

}
