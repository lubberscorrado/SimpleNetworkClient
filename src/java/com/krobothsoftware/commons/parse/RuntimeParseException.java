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
 * Runtime parse exception for when a checked exception can not be used.
 *
 * @author Kyle Kroboth
 * @since COMMONS 1.1.0
 */
// TODO create some type of wrapper of stacktrace elements
public class RuntimeParseException extends RuntimeException {
    private static final long serialVersionUID = 5188583158442242621L;

    /**
     * @param error
     * @since COMMONS 1.1.0
     */
    public RuntimeParseException(String error) {
        super(error);
    }

    /**
     * @param throwable
     * @since COMMONS 1.1.0
     */
    public RuntimeParseException(Throwable throwable) {
        super(throwable);
    }

    /**
     * @param message
     * @param throwable
     * @since COMMONS 1.1.0
     */
    public RuntimeParseException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
