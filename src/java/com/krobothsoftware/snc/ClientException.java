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
 * Signals an exception with client.
 *
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public class ClientException extends Exception {
    private static final long serialVersionUID = 6769353379782248202L;

    /**
     * @param message
     * @since SNC 1.0
     */
    public ClientException(String message) {
        super(message);
    }

    /**
     * @param cause
     * @since SNC 1.0
     */
    public ClientException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     * @since SNC 1.0
     */
    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
