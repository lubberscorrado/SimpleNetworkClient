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

import com.krobothsoftware.commons.network.http.cookie.CookieMap;

import java.io.Serializable;

/**
 * Access tokens for sessions that identifies user.
 *
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public class HttpToken implements Serializable {
    private static final long serialVersionUID = -3419625975537239000L;

    private final CookieMap cookies;

    /**
     * Set up HttpToken with cookies.
     *
     * @param cookies
     * @since SNC 1.0
     */
    public HttpToken(CookieMap cookies) {
        this.cookies = cookies;
    }

    /**
     * Set up token with empty cookies.
     *
     * @since SNC 1.0
     */
    public HttpToken() {
        this.cookies = new CookieMap();
    }

    /**
     * Get cookies.
     *
     * @return cookies
     * @since SNC 1.0
     */
    public final CookieMap getCookies() {
        return cookies;
    }

}
