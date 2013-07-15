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

package com.krobothsoftware.commons.network.http.auth;

import com.krobothsoftware.commons.network.http.HttpHelper;
import com.krobothsoftware.commons.network.http.HttpResponseAuthenticate;

import java.io.IOException;

/**
 * Authentication is the base class for authorizations.
 * <p>
 * There are two steps to an Authentication. Prior to executing the connection,
 * {@link #setup(HttpRequestAuthenticate)} is called to set up credentials.
 * After the connection has sent and if the status code is 401(Unauthorized),
 * {@link #authenticate(HttpRequestAuthenticate, HttpResponseAuthenticate)} is
 * then called.
 * </p>
 * <p>
 * Authenticating after 401 response may only be called if
 * {@link #authenticateSupported()} is true.
 * </p>
 *
 * @author Kyle Kroboth
 * @see AuthenticationManager
 * @since COMMONS 1.0
 */
public abstract class Authentication {

    /**
     * Constant header for Authorization
     *
     * @since COMMONS 1.0.1
     */
    static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * Username for auth.
     *
     * @since COMMONS 1.0
     */
    protected String username;

    /**
     * Password for auth.
     *
     * @since COMMONS 1.0
     */
    protected char[] password;

    /*
     * Empty constructor
     * @since COMMONS 1.0
     */
    protected Authentication() {

    }

    /**
     * Instantiates a new authorization with username and password.
     *
     * @param username
     * @param password
     * @since COMMONS 1.0
     * @deprecated use {@link #Authentication(String, char[])}. This constructor
     *             will convert String <code>password</code> to a char array.
     */
    @Deprecated
    public Authentication(String username, String password) {
        this(username, password.toCharArray());
    }

    /**
     * Instantiates a new authorization with username and password.
     *
     * @param username
     * @param password in char array
     * @since COMMONS 1.0.1
     */
    public Authentication(String username, char[] password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Called when connection needs to be set up for auth.
     *
     * @param request for setup
     * @throws java.io.IOException Signals that an I/O exception has occurred.
     * @since COMMONS 1.0
     */
    public abstract void setup(HttpRequestAuthenticate request)
            throws IOException;

    /**
     * Authenticates response if status code is <b>401</b>.
     *
     * @param request  for authenticating.
     * @param response for authenticating. Must close.
     * @return response after authenticating
     * @throws IOException Signals that an I/O exception has occurred.
     * @see #authenticateSupported()
     * @since COMMONS 1.0
     */
    public abstract void authenticate(HttpRequestAuthenticate request,
                                      HttpResponseAuthenticate response, HttpHelper httpHelper) throws IOException;

    /**
     * Checks if authenticating is supported. Some authentications only require
     * to set password and username.
     *
     * @return true, if supported
     * @see #authenticate(HttpRequestAuthenticate, HttpResponseAuthenticate)
     * @since COMMONS 1.0
     */
    // TODO change name of method
    public boolean authenticateSupported() {
        return false;
    }

    /**
     * Resets any info in Authentication object, but <b>not</b> credentials.
     *
     * @since COMMONS 1.0
     */
    public abstract void reset();

}
