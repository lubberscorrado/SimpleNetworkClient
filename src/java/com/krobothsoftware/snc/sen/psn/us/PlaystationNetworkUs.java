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

package com.krobothsoftware.snc.sen.psn.us;

import com.krobothsoftware.commons.network.http.*;
import com.krobothsoftware.commons.network.http.cookie.Cookie;
import com.krobothsoftware.commons.network.http.cookie.CookieList;
import com.krobothsoftware.commons.network.http.cookie.CookieMap;
import com.krobothsoftware.commons.parse.ParseException;
import com.krobothsoftware.commons.parse.json.ParserJson;
import com.krobothsoftware.commons.parse.sax.ParserSax;
import com.krobothsoftware.commons.progress.ProgressListener;
import com.krobothsoftware.commons.progress.ProgressMonitor;
import com.krobothsoftware.commons.util.CommonUtils;
import com.krobothsoftware.snc.Beta;
import com.krobothsoftware.snc.ClientException;
import com.krobothsoftware.snc.ClientLoginException;
import com.krobothsoftware.snc.TokenException;
import com.krobothsoftware.snc.sen.SonyEntertainmentNetwork;
import com.krobothsoftware.snc.sen.psn.PlaystationNetwork;
import com.krobothsoftware.snc.sen.psn.PlaystationNetworkException;
import com.krobothsoftware.snc.sen.psn.PsnHttpToken;
import com.krobothsoftware.snc.sen.psn.PsnUtils;
import com.krobothsoftware.snc.sen.psn.model.PsnTrophy;
import com.krobothsoftware.snc.sen.psn.us.internal.*;
import com.krobothsoftware.snc.sen.psn.us.model.PsnFriendGamerProfile;
import com.krobothsoftware.snc.sen.psn.us.model.PsnGamerProfile;
import com.krobothsoftware.snc.sen.psn.us.model.PsnUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.krobothsoftware.commons.network.http.Method.GET;
import static com.krobothsoftware.commons.network.http.Method.POST;

/**
 * Extension PlayStationNetwork client for US website only. Requires
 * <code>SEN-PSN</code> and <code>COMMONS-PARSE-JSON</code> for use.
 * <p/>
 * <p>
 * There are multiple types of <i>Gamer Profiles</i> but use the same object.
 * Make sure to read documentation on what type of data it holds. If non is
 * specified, all is used.
 * </p>
 * <p/>
 * <p>
 * Doesn't include games and trophies because they are in <code>SEN-PSN</code>
 * using public methods.
 * </p>
 * <p/>
 * <p>
 * <code>SEN-PSN</code> silently logs in US account which will make all methods
 * in this client valid for token. Use
 * {@link #login(com.krobothsoftware.snc.sen.psn.PsnHttpToken, String, String, ProgressListener)} to update token.
 * Use {@link #isLoginValid(HttpResponse)} to check if token has expired since all
 * methods here won't throw a HttpToken Exception.
 * </p>
 *
 * @author Kyle Kroboth
 * @since SEN-PSN-US 1.0
 */
public class PlaystationNetworkUs extends SonyEntertainmentNetwork {
    private final Logger log;
    private final PlaystationNetwork psnClient;
    private final ParserSax htmlParser;
    private final ParserJson jsonParser;

    /**
     * Creates new US Psn Client.
     *
     * @param psnClient wrapped client for networking
     * @since SEN-PSN-US 1.0
     */
    public PlaystationNetworkUs(PlaystationNetwork psnClient) {
        super(psnClient.getHttpHelper());
        log = LoggerFactory.getLogger(PlaystationNetworkUs.class);
        this.psnClient = psnClient;
        htmlParser = psnClient.getHtmlParser();
        jsonParser = new ParserJson();
    }

    /**
     * Performs a full login to US account.
     * <p/>
     * <p>
     * Connections made
     * <p/>
     * <pre>
     * https://account.sonyentertainmentnetwork.com/external/auth/login!authenticate.action
     * https://us.playstation.com/uwps/PSNTicketRetrievalGenericServlet (?sessionId=****)
     * http://us.playstation.com/uwps/HandleIFrameRequests (?sessionId=****)
     * http://us.playstation.com/uwps/CookieHandler?cookieName=userinfo&id= (random) only if psnId could not be found in <b>ph</b> cookie
     * http://searchjid.usa.np.community.playstation.net/basic_view/func/search_jid
     * </pre>
     * <p/>
     * </p>
     * <p/>
     * <p>
     * Data retrieved
     * <ul>
     * <li>Login cookies</li>
     * <li>Jid</li>
     * <li>SessionId</li>
     * </ul>
     * </p>
     *
     * @param username email of account
     * @param password password of account
     * @param listener monitor progress events. May be null.
     * @return psn login token
     * @throws IllegalArgumentException    if username or password are null
     * @throws IOException                 Signals that an I/O exception has occurred.
     * @throws ClientLoginException        if credentials are incorrect
     * @throws PlaystationNetworkException if service is down or login failed
     * @since SEN-PSN-US 1.0
     */
    public PsnHttpToken login(String username, String password,
                              ProgressListener listener) throws IOException,
            ClientLoginException, PlaystationNetworkException {
        log.debug("login - Entering");

        if (username == null || password == null) throw new IllegalArgumentException(
                "username and password may not be null");

        ProgressMonitor monitor = ProgressMonitor.createMonitor(listener);
        monitor.beginTask("Logging in", 5);

        CookieMap cookies = new CookieMap();
        String session;
        String jid;

        List<NameValuePair> params = HttpHelper
                .getPairs(
                        "j_username",
                        username,
                        "j_password",
                        password,
                        "returnURL",
                        "https://us.playstation.com/uwps/PSNTicketRetrievalGenericServlet",
                        "service-entity", "psn");

        HttpResponse response = null;
        HttpRequest builder = new HttpRequest(
                POST,
                new URL(
                        "https://account.sonyentertainmentnetwork.com/external/auth/login!authenticate.action"))
                .payload(params, "UTF-8").use(cookies).requestCookies(false);

        try {
            response = builder.execute(httpHelper);
            monitor.worked(1, "Authenticating");
            isLoginValid(response);
            response.close();

            // will always be a redirect at this point
            String urlLocation = ((HttpResponseRedirect) response).getRedirectUrl();
            session = urlLocation
                    .substring(urlLocation.indexOf("?sessionId=") + 11);

            // no need to reset builder
            builder.method(GET).url(new URL(urlLocation)).close(true);
            response = builder.execute(httpHelper);
            monitor.worked(1);

            // no need to reset builder
            builder.method(GET)
                    .url(new URL(
                            String.format(
                                    "http://us.playstation.com/uwps/HandleIFrameRequests?sessionId=%s",
                                    session)));
            response = builder.execute(httpHelper);
            monitor.worked(1, "Retrieving PsnId");

            // get psnId
            Cookie cookie;
            String psnId = null;
            cookie = cookies.getCookie(".playstation.com", "ph");
            if (cookie != null) psnId = cookie.getValue();
            else
                log.warn("Couldn't retrieve psnId in ph cookie");

            if (psnId == null) {
                log.info("Retrieving PsnId with userinfo cookie");
                cookie = cookies.getCookie(".playstation.com", "userinfo");
                if (cookie == null) {
                    log.warn("Couldn't retrieve userinfo cookie");
                } else {
                    response = new HttpRequest(
                            GET,
                            new URL(
                                    String.format(
                                            "http://us.playstation.com/uwps/CookieHandler?cookieName=userinfo&id=%s",
                                            String.valueOf(Math.random()))))
                            .header("X-Requested-With", "XMLHttpRequest")
                            .header("Cookie", cookie.getCookieString())
                            .storeCookies(false).execute(httpHelper);
                    String content = HttpResponse.toString(response);
                    int index = content.indexOf("handle=");
                    if (index != -1) {
                        psnId = content.substring(index,
                                content.indexOf(',', index));
                    }
                    response.close();
                }

            }

            if (psnId == null) throw new ClientLoginException(
                    "Sign-In unsuccessful");
            monitor.worked(1, "Retrieving Jid");

            // get Jid
            jid = psnClient.getOfficialJid(psnId);
            monitor.worked(1);

            monitor.done("Successfully logged in");
        } finally {
            CommonUtils.closeQuietly(response);
            log.debug("login - Exiting");
        }

        return new PsnHttpToken(cookies, jid, session);
    }

    /**
     * Performs a re-login for given <code>token</code>; updates token.
     * <p/>
     * <p>
     * Connections made
     * <p/>
     * <pre>
     * https://account.sonyentertainmentnetwork.com/external/auth/login!authenticate.action
     * https://us.playstation.com/uwps/PSNTicketRetrievalGenericServlet (?sessionId=****)
     * http://us.playstation.com/uwps/HandleIFrameRequests (?sessionId=****)
     * </pre>
     * <p/>
     * </p>
     * <p/>
     * <p>
     * Data retrieved
     * <ul>
     * <li>Updated login cookies</li>
     * <li>New sessionId</li>
     * </ul>
     * </p>
     *
     * @param token    login token to log into again
     * @param username email of account
     * @param password password of account
     * @param listener monitor progress events. May be null.
     * @throws IllegalArgumentException    if username or password are null
     * @throws IOException                 Signals that an I/O exception has occurred.
     * @throws ClientLoginException        if credentials are incorrect
     * @throws PlaystationNetworkException if service is down or login failed
     * @since SEN-PSN-US 1.0
     */
    public void login(PsnHttpToken token, String username, String password,
                      ProgressListener listener) throws IOException,
            ClientLoginException, PlaystationNetworkException {
        log.debug("login - Entering");

        if (username == null || password == null) throw new IllegalArgumentException(
                "username and password may not be null");

        ProgressMonitor monitor = ProgressMonitor.createMonitor(listener);
        monitor.beginTask("Logging in", 3);

        CookieMap cookies = token.getCookies();
        String session;

        List<NameValuePair> params = HttpHelper
                .getPairs(
                        "j_username",
                        username,
                        "j_password",
                        password,
                        "returnURL",
                        "https://us.playstation.com/uwps/PSNTicketRetrievalGenericServlet",
                        "service-entity", "psn");

        HttpResponse response = null;
        HttpRequest builder = new HttpRequest(
                POST,
                new URL(
                        "https://account.sonyentertainmentnetwork.com/external/auth/login!authenticate.action"))
                .payload(params, "UTF-8").use(cookies).requestCookies(false);

        try {
            response = builder.execute(httpHelper);
            monitor.worked(1, "Authenticating");
            isLoginValid(response);
            response.close();

            // will always be a redirect at this point
            String urlLocation = ((HttpResponseRedirect) response).getRedirectUrl();
            session = urlLocation
                    .substring(urlLocation.indexOf("?sessionId=") + 11);

            // no need to reset builder
            builder.method(GET).url(new URL(urlLocation)).close(true);
            response = builder.execute(httpHelper);
            monitor.worked(1);

            // no need to reset builder
            builder.method(GET)
                    .url(new URL(
                            String.format(
                                    "http://us.playstation.com/uwps/HandleIFrameRequests?sessionId=%s",
                                    session)));
            response = builder.execute(httpHelper);
            monitor.worked(1);
            monitor.done("Successfully logged in");
        } finally {
            CommonUtils.closeQuietly(response);
            log.debug("login - Exiting");
        }

        token.setSession(session);
    }

    /**
     * Logs out of account(token). Clearing cookies in token and and setting
     * SessionId to null is an alternative.
     * <p/>
     * <p>
     * Connections made
     * <p/>
     * <pre>
     * http://us.playstation.com/playstation/psn/logout
     * </pre>
     * <p/>
     * </p>
     *
     * @param token psn login token
     * @throws IOException Signals that an I/O exception has occurred.
     * @since SEN-PSN-US 1.0
     */
    public void logout(PsnHttpToken token) throws IOException {
        log.debug("logout - Entering");
        HttpResponse response = null;

        try {
            response = new HttpRequest(GET, new URL(
                    "http://us.playstation.com/playstation/psn/logout")).use(
                    token.getCookies()).execute(httpHelper);
        } finally {
            CommonUtils.closeQuietly(response);
            token.getCookies().purgeExpired(true);
            token.setSession(null);
            log.debug("logout - Exiting");
        }
    }

    /**
     * Checks if psn token is still valid by sending a request to verify
     * <code>userinfo</code> cookie.
     *
     * @param token psn login token
     * @return true, if token is still valid
     * @throws IOException Signals that an I/O exception has occurred.
     * @since SEN-PSN-US 1.0
     */
    public boolean isTokenValid(PsnHttpToken token) throws IOException {
        HttpResponse response = null;

        try {
            response = new HttpRequest(
                    POST,
                    new URL(
                            String.format(
                                    "http://us.playstation.com/uwps/CookieHandler?cookieName=userinfo&id=%s",
                                    String.valueOf(Math.random()))))
                    .header("X-Requested-With", "XMLHttpRequest")
                    .use(token.getCookies()).execute(httpHelper);
            return response.getContentLengthLong() > 0L;
        } finally {
            CommonUtils.closeQuietly(response);
        }

    }

    /**
     * Retrieves user info from token. <code>userinfo</code> cookie is required
     * in token beforehand which can be retrieved with a login.
     *
     * @param token psn login token
     * @return user info from token
     * @throws IOException    Signals that an I/O exception has occurred.
     * @throws TokenException no userinfo cookie or expired token
     * @beta request may change in the future.
     * @since SEN-PSN-US 1.0
     */
    @Beta
    public PsnUser getUserInfo(PsnHttpToken token) throws IOException,
            TokenException {
        log.debug("getUserInfo - Entering");
        HttpResponse response = null;
        PsnUser user;

        Cookie cookie = token.getCookies().getCookie(".playstation.com",
                "userinfo");
        if (cookie == null) throw new TokenException(
                "Couldn't retrieve userinfo cookie");

        try {
            response = new HttpRequest(
                    POST,
                    new URL(
                            String.format(
                                    "http://us.playstation.com/uwps/PSNLoginCookie?cookieName=userinfo&id=%s",
                                    String.valueOf(Math.random()))))
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Cookie", cookie.getCookieString())
                    .storeCookies(false).execute(httpHelper);
            if (response.getContentLengthLong() <= 0L) throw new TokenException();
            user = PsnUser.newInstance(HttpResponse.toString(response));
        } finally {
            CommonUtils.closeQuietly(response);
            log.debug("getUserInfo - Exiting");
        }

        return user;
    }

    /**
     * Retrieves list of friends. Each friend has to be retrieved individually
     * with {@link #getGamerProfile(com.krobothsoftware.snc.sen.psn.PsnHttpToken, String)}. The list is sorted from
     * most recent at the top. Even if token expired, it will return an empty
     * list.
     * <p/>
     * <p>
     * Pending friend requests will be at bottom of list, if there are any. Only
     * way to tell is if friend gamer profile doesn't show up for user.
     * </p>
     * <p/>
     * <p>
     * Connections made
     * <p/>
     * <pre>
     * http://us.playstation.com/playstation/psn/profile/friends?lang=en_US&id= (random)
     * </pre>
     * <p/>
     * </p>
     *
     * @param token psn login token
     * @return list of friends or empty if user has none
     * @throws IOException     Signals that an I/O exception has occurred.
     * @throws ClientException if parser encountered an error
     * @since SEN-PSN-US 1.0
     */
    public List<String> getFriendList(PsnHttpToken token) throws IOException,
            ClientException {
        log.debug("getFriendList - Entering");
        HttpResponse response = null;
        HandlerHtmlFriend handler;

        try {
            response = new HttpRequest(GET, new URL(
                    "http://us.playstation.com/playstation/psn/profile/friends?lang=en_US&id="
                            + String.valueOf(Math.random())))
                    .header("Referer",
                            "http://us.playstation.com/community/myfriends/")
                    .use(token.getCookies()).execute(httpHelper);
            handler = new HandlerHtmlFriend();
            htmlParser.parse(response.getStream(), handler, response.getCharset());
        } catch (ParseException e) {
            throw new ClientException(e);
        } finally {
            CommonUtils.closeQuietly(response);
            log.debug("getFriendList - Exiting");
        }

        return handler.getFriendList();
    }

    /**
     * Retrieves friend gamer profile for login token and PsnId. Extended
     * version of {@link #getGamerProfile(String)} for friends.
     * <p/>
     * <p>
     * Connections made
     * <p/>
     * <pre>
     * http://us.playstation.com/playstation/psn/profile/get_gamer_summary_data?id= (random)
     * </pre>
     * <p/>
     * </p>
     *
     * @param token psn login token
     * @param psnId id
     * @return friend gamer profile or null if PsnId was not found
     * @throws IOException     Signals that an I/O exception has occurred.
     * @throws ClientException if parser encountered an error
     * @since SEN-PSN-US 1.0
     */
    public PsnFriendGamerProfile getGamerProfile(PsnHttpToken token, String psnId)
            throws IOException, ClientException {
        log.debug("getFriendGamerProfile [{}] - Entering", psnId);
        HttpResponse response = null;
        HandlerJsonGamerProfile handler;

        try {
            response = new HttpRequest(GET, new URL(
                    "http://us.playstation.com/playstation/psn/profile/get_gamer_summary_data?id="
                            + psnId))
                    .header("Referer",
                            "http://us.playstation.com/playstation/psn/profile/friends?lang=en_US&id="
                                    + Math.random())
                    .header("X-Requested-With", "XMLHttpRequest")
                    .use(token.getCookies()).execute(httpHelper);
            handler = new HandlerJsonGamerProfile();
            jsonParser.parse(response.getStream(), handler, response.getCharset());
            if (handler.failed()) {
                return null;
            }
        } catch (ParseException e) {
            throw new ClientException(e);
        } finally {
            CommonUtils.closeQuietly(response);
            log.debug("getFriendGamerProfile - Exiting");
        }

        return handler.getFriendProfile();
    }

    /**
     * Retrieves list of friend profiles. Even if token expired, it will return
     * an empty list.
     * <p/>
     * <p>
     * <b>WARNING:</b> This method will take approximately 20 seconds to connect
     * and wait for server.
     * </p>
     * <p/>
     * <p>
     * Connections made
     * <p/>
     * <pre>
     * http://us.playstation.com/playstation/psn/profile/get_friends_profile_with_trophy_total
     * </pre>
     * <p/>
     * </p>
     * <p/>
     * <p>
     * Data retrieved
     * <p/>
     * <pre>
     * <table border="1">
     * <tr>
     * <th>Type</th>
     * <th>Method</th>
     * </tr>
     * <tr>
     * <td>Name</td>
     * <td>getOnlineId()</td>
     * </tr>
     * <tr>
     * <td>Avatar URL</td>
     * <td>getAvatar()</td>
     * </tr>
     * <tr>
     * <td>Bronze trophies</td>
     * <td>getBronze()</td>
     * </tr>
     * <tr>
     * <td>Silver trophies</td>
     * <td>getSilver()</td>
     * </tr>
     * <tr>
     * <td>Gold trophies</td>
     * <td>getGold()</td>
     * </tr>
     * <tr>
     * <td>Platinum trophies</td>
     * <td>getPlatinum()</td>
     * </tr>
     * <tr>
     * <td>Trophy Level</td>
     * <td>getLevel()</td>
     * </tr>
     * <tr>
     * <td>Trophy Progress</td>
     * <td>getProgress()</td>
     * </tr>
     * </table>
     * </pre>
     * <p/>
     * </p>
     *
     * @param token psn login token
     * @return list of simple friend profiles or empty if user has none
     * @throws IOException     Signals that an I/O exception has occurred.
     * @throws ClientException if parser encountered an error
     * @since SEN-PSN-US 1.0
     */
    public List<PsnGamerProfile> getFriendProfileList(PsnHttpToken token)
            throws IOException, ClientException {
        log.debug("getFriendProfileList - Entering");
        HttpResponse response = null;
        HandlerJsonFriendProfile handler;

        try {
            response = new HttpRequest(
                    GET,
                    new URL(
                            "http://us.playstation.com/playstation/psn/profile/get_friends_profile_with_trophy_total"))
                    .header("Referer",
                            "http://us.playstation.com/playstation/psn/profile/friends?lang=en_US&id="
                                    + Math.random())
                    .header("X-Requested-With", "XMLHttpRequest")
                    .use(token.getCookies()).execute(httpHelper);
            handler = new HandlerJsonFriendProfile();
            jsonParser.parse(response.getStream(), handler, response.getCharset());
        } catch (ParseException e) {
            throw new ClientException(e);
        } finally {
            CommonUtils.closeQuietly(response);
            log.debug("getFriendProfileList - Exiting");
        }

        return handler.getProfileList();

    }

    /**
     * Retrieves simple gamer profile. Connection time is low. If PsnId is not
     * found, it will return an all zero and null value profile.
     * <p/>
     * <p>
     * Connections made
     * <p/>
     * <pre>
     * http://us.playstation.com/playstation/psn/profile/ (psnId) /psnUser?id= (random)
     * </pre>
     * <p/>
     * </p>
     * <p/>
     * <p>
     * Data retrieved
     * <p/>
     * <pre>
     * <table border="1">
     * <tr>
     * <th>Type</th>
     * <th>Method</th>
     * </tr>
     * <tr>
     * <td>Name</td>
     * <td>getOnlineId()</td>
     * </tr>
     * <tr>
     * <td>Avatar URL</td>
     * <td>getAvatar()</td>
     * </tr>
     * <tr>
     * <td>Bronze trophies</td>
     * <td>getBronze()</td>
     * </tr>
     * <tr>
     * <td>Silver trophies</td>
     * <td>getSilver()</td>
     * </tr>
     * <tr>
     * <td>Gold trophies</td>
     * <td>getGold()</td>
     * </tr>
     * <tr>
     * <td>Platinum trophies</td>
     * <td>getPlatinum()</td>
     * </tr>
     * <tr>
     * <td>Trophy Level</td>
     * <td>getLevel()</td>
     * </tr>
     * <tr>
     * <td>Trophy Progress</td>
     * <td>getProgress()</td>
     * </tr>
     * </table>
     * </pre>
     * <p/>
     * </p>
     *
     * @param psnId id
     * @return simple gamer profile, or empty profile if not found
     * @throws IOException     Signals that an I/O exception has occurred.
     * @throws ClientException if parser encountered an error
     * @since SEN-PSN-US 1.0
     */
    public PsnGamerProfile getSimpleProfile(String psnId) throws IOException,
            ClientException {
        log.debug("getSimpleProfile [{}] - Entering", psnId);
        HttpResponse response = null;
        HandlerHtmlUserProfile handler;

        try {
            response = new HttpRequest(
                    GET,
                    new URL(
                            String.format(
                                    "http://us.playstation.com/playstation/psn/profile/%s/psnUser?id=%s",
                                    psnId, String.valueOf(Math.random()))))
                    .header("Referer",
                            "http://us.playstation.com/playstation/psn/profile/myprofile?lang=en_US&id="
                                    + Math.random())
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute(httpHelper);
            handler = new HandlerHtmlUserProfile();
            htmlParser.parse(response.getStream(), handler, response.getCharset());
        } catch (ParseException e) {
            throw new ClientException(e);
        } finally {
            CommonUtils.closeQuietly(response);
            log.debug("getSimpleProfile - Exiting");
        }

        return handler.getProfile();
    }

    /**
     * Retrieves gamer profile for psnId.
     * <p/>
     * <p>
     * Connections made
     * <p/>
     * <pre>
     * http://us.playstation.com/playstation/psn/profile/get_gamer_summary_data?id= (random)
     * </pre>
     * <p/>
     * </p>
     *
     * @param psnId id
     * @return gamer profile or null if PsnId was not found
     * @throws IOException     Signals that an I/O exception has occurred.
     * @throws ClientException if parser encountered an error
     * @since SEN-PSN-US 1.0
     */
    public PsnGamerProfile getGamerProfile(String psnId) throws IOException,
            ClientException {
        log.debug("getGamerProfile [{}] - Entering", psnId);
        HttpResponse response = null;
        HandlerJsonGamerProfile handler;

        try {
            response = new HttpRequest(GET, new URL(
                    "http://us.playstation.com/playstation/psn/profile/get_gamer_summary_data?id="
                            + psnId))
                    .header("Referer",
                            "http://us.playstation.com/playstation/psn/profile/friends?lang=en_US&id="
                                    + Math.random())
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute(httpHelper);
            handler = new HandlerJsonGamerProfile();
            jsonParser.parse(response.getStream(), handler, response.getCharset());
            if (handler.failed()) {
                return null;
            }
        } catch (ParseException e) {
            throw new ClientException(e);
        } finally {
            CommonUtils.closeQuietly(response);
            log.debug("getGamerProfile - Exiting");
        }

        return handler.getProfile();
    }

    /**
     * Retrieves last 5 trophies user has.
     * <p/>
     * <p>
     * Connections made
     * <p/>
     * <pre>
     * http://us.playstation.com/playstation/psn/profile/ (psnId) /get_ordered_title_details_data
     * </pre>
     * <p/>
     * </p>
     * <p/>
     * <p>
     * Data retrieved
     * <p/>
     * <pre>
     * <table border="1">
     * <tr>
     * <th>Type</th>
     * <th>Method</th>
     * </tr>
     * <tr>
     * <td>PsnId</td>
     * <td>getOnlineId()</td>
     * </tr>
     * <tr>
     * <td>Game Id</td>
     * <td>getGameId()</td>
     * </tr>
     * <tr>
     * <td>Name</td>
     * <td>getName()</td>
     * </tr>
     * <tr>
     * <td>Description</td>
     * <td>getDescription()</td>
     * </tr>
     * <tr>
     * <td>Image URL</td>
     * <td>getImage()</td>
     * </tr>
     * <tr>
     * <td>Type</td>
     * <td>getType()</td>
     * </tr>
     * </table>
     * </pre>
     * <p/>
     * If <i>description</i> is long, may have an ellipsis(...) at end.
     * <code>getDateEarned()</code> will be null.
     * <p/>
     * </p>
     *
     * @param psnId Id
     * @return list of latest trophies or empty if psnId is not found
     * @throws IOException     Signals that an I/O exception has occurred.
     * @throws ClientException if parser encountered an error
     * @since SEN-PSN-US 1.0
     */
    public List<PsnTrophy> getLatestTrophyList(String psnId)
            throws IOException, ClientException {
        log.debug("getLatestTrophyList - Entering");
        HttpResponse response = null;
        HandlerHtmlLatestTrophy handler;

        List<NameValuePair> params = HttpHelper.getPairs("sortBy", "id_asc");
        CookieList cookies = new CookieList();
        cookies.add(PsnUtils.createCookiePsnTicket(psnId));
        cookies.add(PsnUtils.createCookieTicket(psnId));
        try {
            response = new HttpRequest(
                    POST,
                    new URL(
                            String.format(
                                    "http://us.playstation.com/playstation/psn/profile/%s/get_ordered_title_details_data",
                                    psnId)))
                    .header("Referer",
                            "http://us.playstation.com/playstation/psn/profile/friends?lang=en_US&id="
                                    + Math.random())
                    .header("X-Requested-With", "XMLHttpRequest")
                    .payload(params, "UTF-8").put(cookies)
                    .execute(httpHelper);
            handler = new HandlerHtmlLatestTrophy(psnId);
            htmlParser.parse(response.getStream(), handler, response.getCharset());
        } catch (ParseException e) {
            throw new ClientException(e);
        } finally {
            CommonUtils.closeQuietly(response);
            log.debug("getFriendGamerProfile - Exiting");
        }

        return handler.getTrophyList();

    }

    private static boolean isLoginValid(HttpResponse response) throws IOException,
            ClientLoginException, PlaystationNetworkException {
        if (response.isRedirection()) return true;

        switch (CommonUtils.streamingContains(response.getStream(),
                response.getCharset(), "Incorrect", "maintenance")) {
            case 0:
                throw new ClientLoginException("Incorrect username or password");
            case 1:
                throw new PlaystationNetworkException(
                        "PlayStationNetwork is under maintenance");
            default:
                throw new PlaystationNetworkException("Login Failed");

        }
    }
}
