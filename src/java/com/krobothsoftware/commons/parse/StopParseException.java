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
 * Exception for stopping a <code>parser</code> from continuing. Some parsers do
 * not have a stop operation. Will be caught internally and logged.
 *
 * @author Kyle Kroboth
 * @since COMMONS 1.1.0
 */
public class StopParseException extends RuntimeException {
    private static final long serialVersionUID = 5964448482034581273L;

    /**
     * Returns current object so stack trace is not filled.
     *
     * @since COMMONS 1.1.0
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
