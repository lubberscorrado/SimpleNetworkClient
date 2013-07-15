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

package com.krobothsoftware.snc;

/**
 * When a HttpToken encounters an error like token expired.
 *
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public class TokenException extends Exception {
    private static final long serialVersionUID = 8266203313746429100L;

    /**
     * Create new exception with text.
     * <p/>
     * <pre>
     * HttpToken expired
     * </pre>
     *
     * @since SNC 1.0
     */
    public TokenException() {
        super("HttpToken expired");
    }

    /**
     * @param message
     * @since SNC 1.0
     */
    public TokenException(String message) {
        super(message);
    }

    /**
     * @param cause
     * @since SNC 1.0
     */
    public TokenException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     * @since SNC 1.0
     */
    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }

}
