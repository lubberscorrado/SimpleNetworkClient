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
 * Listener for <code>Handler</code> parser events.
 *
 * @author Kyle Kroboth
 * @since COMMONS 1.1.0
 */
public interface ParserHandlerListener<T extends Handler> {

    /**
     * Process and return handler.
     * <p/>
     * <p>
     * Example, internally, any {@link com.krobothsoftware.commons.parse.sax.HandlerSax} that implements
     * {@link com.krobothsoftware.commons.parse.sax.ExpressionFilter} is wrapped inside a
     * <code>HandlerExpression</code>.
     * </p>
     *
     * @param handler handler to be processed
     * @return new handler or same instance
     * @since COMMONS 1.0
     */
    T getHandler(T handler);

}
