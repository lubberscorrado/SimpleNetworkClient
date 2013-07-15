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
import com.krobothsoftware.commons.network.http.cookie.CookieList;
import com.krobothsoftware.commons.network.http.cookie.CookieMap;
import com.krobothsoftware.commons.network.http.cookie.Cookie;
import com.krobothsoftware.commons.parse.Handler;
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
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import static com.krobothsoftware.commons.network.http.Method.GET;
import static com.krobothsoftware.commons.network.http.Method.POST;

public class PlaystationNetworkUs extends SonyEntertainmentNetwork {
    private final Logger log;
    private final PlaystationNetwork psnClient;
    private final ParserSax htmlParser;
    private final ParserJson jsonParser;

    public PlaystationNetworkUs(PlaystationNetwork psnClient) {
        super(psnClient.getHttpHelper());
        log = LoggerFactory.getLogger(PlaystationNetworkUs.class);
        this.psnClient = psnClient;
        htmlParser = psnClient.getHtmlParser();
        jsonParser = new ParserJson();
    }

	public PsnHttpToken login(String username, String password,
			ProgressListener listener) throws IOException,
			ClientLoginException, PlaystationNetworkException {
		log.debug("login - Entering");

		Objects.requireNonNull(username, "username may not be null");
		Objects.requireNonNull(password, "password may not be null");

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

	@SuppressWarnings("resource")
	public void login(PsnHttpToken token, String username, String password,
			ProgressListener listener) throws IOException,
			ClientLoginException, PlaystationNetworkException {
		log.debug("login - Entering");

		Objects.requireNonNull(username, "username may not be null");
		Objects.requireNonNull(password, "password may not be null");

		ProgressMonitor monitor = ProgressMonitor.createMonitor(listener);
		monitor.beginTask("Logging in", 3);

		CookieMap cookies = token.getCookies();
		String session;

		List<NameValuePair> params = httpHelper
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

	public void logout(PsnHttpToken token) throws IOException {
		log.debug("logout - Entering");

		try (HttpResponse response = new HttpRequest(GET, new URL(
				"http://us.playstation.com/playstation/psn/logout")).use(
				token.getCookies()).execute(httpHelper)) {
			// no op
		} finally {
			token.getCookies().purgeExpired(true);
			token.setSession(null);
			log.debug("logout - Exiting");
		}
	}

	public boolean isTokenValid(PsnHttpToken token) throws IOException {
		try (HttpResponse response = new HttpRequest(
				POST,
				new URL(
						String.format(
								"http://us.playstation.com/uwps/CookieHandler?cookieName=userinfo&id=%s",
								String.valueOf(Math.random()))))
				.header("X-Requested-With", "XMLHttpRequest")
				.use(token.getCookies()).execute(httpHelper)) {
			return response.getContentLengthLong() > 0L;
		}
	}

	@Beta
	public PsnUser getUserInfo(PsnHttpToken token) throws IOException,
			TokenException {
		log.debug("getUserInfo - Entering");
		PsnUser user;

		Cookie cookie = token.getCookies().getCookie(".playstation.com",
				"userinfo");
		if (cookie == null) throw new TokenException(
				"Couldn't retrieve userinfo cookie");

		try (HttpResponse response = new HttpRequest(
				POST,
				new URL(
						String.format(
								"http://us.playstation.com/uwps/PSNLoginCookie?cookieName=userinfo&id=%s",
								String.valueOf(Math.random()))))
				.header("X-Requested-With", "XMLHttpRequest")
				.header("Cookie", cookie.getCookieString()).storeCookies(false)
				.execute(httpHelper)) {
			if (response.getContentLengthLong() <= 0L) throw new TokenException();
			user = PsnUser.newInstance(HttpResponse.toString(response));
		} finally {
			log.debug("getUserInfo - Exiting");
		}

		return user;
	}

	public List<String> getFriendList(PsnHttpToken token) throws IOException,
			ClientException {
		log.debug("getFriendList - Entering");
		HandlerHtmlFriend handler;

		try (HttpResponse response = new HttpRequest(GET, new URL(
				"http://us.playstation.com/playstation/psn/profile/friends?lang=en_US&id="
						+ String.valueOf(Math.random())))
				.header("Referer",
						"http://us.playstation.com/community/myfriends/")
				.use(token.getCookies()).execute(httpHelper)) {
			handler = new HandlerHtmlFriend();
			htmlParser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getFriendList - Exiting");
		}

		return handler.getFriendList();
	}

	public PsnFriendGamerProfile getGamerProfile(PsnHttpToken token, String psnId)
			throws IOException, ClientException {
		log.debug("getFriendGamerProfile [{}] - Entering", psnId);
		HandlerJsonGamerProfile handler;

		try (HttpResponse response = new HttpRequest(GET, new URL(
				"http://us.playstation.com/playstation/psn/profile/get_gamer_summary_data?id="
						+ psnId))
				.header("Referer",
						"http://us.playstation.com/playstation/psn/profile/friends?lang=en_US&id="
								+ Math.random())
				.header("X-Requested-With", "XMLHttpRequest")
				.use(token.getCookies()).execute(httpHelper)) {
			handler = new HandlerJsonGamerProfile();
			jsonParser.parse(response.getStream(), handler, response.getCharset());
			if (handler.failed()) {
				return null;
			}
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getFriendGamerProfile - Exiting");
		}

		return handler.getFriendProfile();
	}

	public List<PsnGamerProfile> getFriendProfileList(PsnHttpToken token)
			throws IOException, ClientException {
		log.debug("getFriendProfileList - Entering");
		HandlerJsonFriendProfile handler;

		try (HttpResponse response = new HttpRequest(
				GET,
				new URL(
						"http://us.playstation.com/playstation/psn/profile/get_friends_profile_with_trophy_total"))
				.header("Referer",
						"http://us.playstation.com/playstation/psn/profile/friends?lang=en_US&id="
								+ Math.random())
				.header("X-Requested-With", "XMLHttpRequest")
				.use(token.getCookies()).execute(httpHelper)) {
			handler = new HandlerJsonFriendProfile();
			jsonParser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getFriendProfileList - Exiting");
		}

		return handler.getProfileList();

	}

	public PsnGamerProfile getSimpleProfile(String psnId) throws IOException,
			ClientException {
		log.debug("getSimpleProfile [{}] - Entering", psnId);
		HandlerHtmlUserProfile handler;

		try (HttpResponse response = new HttpRequest(
				GET,
				new URL(
						String.format(
								"http://us.playstation.com/playstation/psn/profile/%s/psnUser?id=%s",
								psnId, String.valueOf(Math.random()))))
				.header("Referer",
						"http://us.playstation.com/playstation/psn/profile/myprofile?lang=en_US&id="
								+ Math.random())
				.header("X-Requested-With", "XMLHttpRequest")
				.execute(httpHelper)) {
			handler = new HandlerHtmlUserProfile();
			htmlParser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getSimpleProfile - Exiting");
		}

		return handler.getProfile();
	}

	public PsnGamerProfile getGamerProfile(String psnId) throws IOException,
			ClientException {
		log.debug("getGamerProfile [{}] - Entering", psnId);
		HandlerJsonGamerProfile handler;

		try (HttpResponse response = new HttpRequest(GET, new URL(
				"http://us.playstation.com/playstation/psn/profile/get_gamer_summary_data?id="
						+ psnId))
				.header("Referer",
						"http://us.playstation.com/playstation/psn/profile/friends?lang=en_US&id="
								+ Math.random())
				.header("X-Requested-With", "XMLHttpRequest")
				.execute(httpHelper)) {
			handler = new HandlerJsonGamerProfile();
			jsonParser.parse(response.getStream(), handler, response.getCharset());
			if (handler.failed()) {
				return null;
			}
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getGamerProfile - Exiting");
		}

		return handler.getProfile();
	}

	public List<PsnTrophy> getLatestTrophyList(String psnId)
			throws IOException, ClientException {
		log.debug("getLatestTrophyList - Entering");
		HandlerHtmlLatestTrophy handler;

		List<NameValuePair> params = httpHelper.getPairs("sortBy", "id_asc");
		CookieList cookies = new CookieList();
		cookies.add(PsnUtils.createCookiePsnTicket(psnId));
		cookies.add(PsnUtils.createCookieTicket(psnId));
		try (HttpResponse response = new HttpRequest(
				POST,
				new URL(
						String.format(
								"http://us.playstation.com/playstation/psn/profile/%s/get_ordered_title_details_data",
								psnId)))
				.header("Referer",
						"http://us.playstation.com/playstation/psn/profile/friends?lang=en_US&id="
								+ Math.random())
				.header("X-Requested-With", "XMLHttpRequest")
				.payload(params, "UTF-8").put(cookies).execute(httpHelper)) {
			handler = new HandlerHtmlLatestTrophy(psnId);
			htmlParser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
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
