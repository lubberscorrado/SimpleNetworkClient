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

package com.krobothsoftware.snc.sen.psn;

import com.krobothsoftware.commons.network.http.*;
import com.krobothsoftware.commons.network.http.auth.AuthScope;
import com.krobothsoftware.commons.network.http.auth.AuthenticationManager;
import com.krobothsoftware.commons.network.http.auth.DigestAuthentication;
import com.krobothsoftware.commons.network.http.auth.HttpRequestAuthenticate;
import com.krobothsoftware.commons.network.http.cookie.Cookie;
import com.krobothsoftware.commons.network.http.cookie.CookieList;
import com.krobothsoftware.commons.network.http.cookie.CookieMap;
import com.krobothsoftware.commons.parse.ParseException;
import com.krobothsoftware.commons.parse.sax.ParserSax;
import com.krobothsoftware.commons.parse.sax.ParserSaxThreadSafe;
import com.krobothsoftware.commons.parse.sax.tagsoup.TagSoupComponentHandler;
import com.krobothsoftware.commons.progress.ProgressListener;
import com.krobothsoftware.commons.progress.ProgressMonitor;
import com.krobothsoftware.commons.util.CommonUtils;
import com.krobothsoftware.snc.ClientException;
import com.krobothsoftware.snc.ClientLoginException;
import com.krobothsoftware.snc.TokenException;
import com.krobothsoftware.snc.sen.Platform;
import com.krobothsoftware.snc.sen.SonyEntertainmentNetwork;
import com.krobothsoftware.snc.sen.psn.internal.*;
import com.krobothsoftware.snc.sen.psn.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.krobothsoftware.commons.network.http.Method.GET;
import static com.krobothsoftware.commons.network.http.Method.POST;

public class PlaystationNetwork extends SonyEntertainmentNetwork {
	public static final String AGENT_PS3_COMMUNITY = "PS3Community-agent/1.0.0 libhttp/1.0.0";
	public static final String AGENT_PS3_APPLICATION = "PS3Application libhttp/3.5.5-000 (CellOS)";
	public static final String AGENT_PS3_UPDATE = "PS3Update-agent/1.0.0 libhttp/1.0.0";
	public static final String AGENT_PS3_BROWSER = "Mozilla/5.0 (PLAYSTATION 3; 1.00)";
	public static final String AGENT_PS3_HTTPCLIENT = "Sony-HTTPClient/1.0 [PS3 test]";
	public static final String AGENT_VITA_LIBHTTP = "libhttp/1.66 (PS Vita)";
	public static final String AGENT_VITA_BROWSER = "Mozilla/5.0 (Playstation Vita 1.50) AppleWebKit/531.22.8 (KHTML, like Gecko)﻿ Silk/3.2﻿";
	public static final String AGENT_PSP_BROWSER = "Mozilla/4.0 (PSP (PlayStation Portable); 2.00)";
	public static final String AGENT_PSP_UPDATE = "PSPUpdate-agent/1.0.0 libhttp/1.0.0";
	public static String PS3_FIRMWARE_VERSION = "4.41";
	private static String PSN_TICKET_ID = "*";
    
    private Logger log;
    private ParserSax xmlParser;
    private ParserSax htmlParser;

	public static void setPsnTicketId(String id) {
		PSN_TICKET_ID = id;
	}

	public PlaystationNetwork() {
        log = LoggerFactory.getLogger(PlaystationNetwork.class);
        xmlParser = new ParserSaxThreadSafe(ParserSax.FACTORY_JAVAX_XML,
                ParserSax.RESETTABLE_JAVAX_XML);
        htmlParser = new ParserSaxThreadSafe(ParserSax.FACTORY_TAGSOUP,
                ParserSax.RESETTABLE_TAGSOUP);
        htmlParser.setComponentListener(new TagSoupComponentHandler());
        httpHelper.getCookieManager().putCookie(
                PsnUtils.createCookieTicket(PSN_TICKET_ID), true);
        httpHelper.getCookieManager().putCookie(
                PsnUtils.createCookiePsnTicket(PSN_TICKET_ID), true);
        AuthenticationManager authManager =
                httpHelper.getAuthenticationManager();
        DigestAuthentication basicDigest = new DigestAuthentication(
                "c7y-basic01", "A9QTbosh0W0D^{7467l-n_>2Y%JG^v>o".toCharArray());
        authManager.add(new AuthScope(
                "searchjid.usa.np.community.playstation.net"), basicDigest);
        authManager.add(new AuthScope(
                "getprof.us.np.community.playstation.net"), basicDigest);
        authManager.add(new AuthScope(
                "trophy.ww.np.community.playstation.net"),
                new DigestAuthentication("c7y-trophy01",
                        "jhlWmT0|:0!nC:b:#x/uihx'Y74b5Ycx".toCharArray()));
	}

    public ParserSax getXmlParser() {
        return xmlParser;
    }

    public ParserSax getHtmlParser() {
        return htmlParser;
    }

	public PsnHttpToken login(String username, String password,
			ProgressListener listener) throws IOException,
			PlaystationNetworkException, ClientLoginException {
		log.debug("login - Entering");

		Objects.requireNonNull(username, "username may not be null");
		Objects.requireNonNull(password, "password may not be null");

		ProgressMonitor monitor = ProgressMonitor.createMonitor(listener);
		monitor.beginTask("Logging in", 6);
		CookieMap cookies = new CookieMap();
		String jid;
		String session;

		List<NameValuePair> params = HttpHelper.getPairs("j_username",
                username, "j_password", password, "returnURL",
                "https://secure.eu.playstation.com/sign-in/confirmation/");

		HttpResponse response = null;

		HttpRequest builder = new HttpRequest(
				POST,
				new URL(
						"https://store.playstation.com/j_acegi_external_security_check?target=/external/loginDefault.action"))
				.payload(params, "UTF-8").use(cookies).requestCookies(false)
				.close(true);

		try {
			response = builder.execute(httpHelper);
			monitor.worked(1);

			if (response.isRedirection()) {
				String urlLocation = ((HttpResponseRedirect) response)
						.getRedirectUrl();

				monitor.setTask("Authenticating");
				builder.reset();
				builder.method(GET).url(new URL(urlLocation)).use(cookies)
						.requestCookies(false);
				response = builder.execute(httpHelper);
				isLoginValid(response);
				monitor.worked(1);
				response.close();

				// get session id and location
				urlLocation = ((HttpResponseRedirect) response).getRedirectUrl();
				session = urlLocation.substring(urlLocation
						.indexOf("?sessionId=") + 11);

				// get additional cookies
				// no need to reset builder
				builder.url(new URL(urlLocation)).close(true);
				response = builder.execute(httpHelper);
				monitor.worked(1);

				// get psn id

				// US method
				monitor.setTask("Retrieving PsnId");
				// no need to reset builder
				builder.url(new URL(
						String.format(
								"http://us.playstation.com/uwps/HandleIFrameRequests?sessionId=%s",
								session)));
				response = builder.execute(httpHelper);

				Cookie cookie;
				// get psnId
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

				if (psnId == null) throw new PlaystationNetworkException(
						"Sign-In unsuccessful");
				monitor.worked(1);

				// get Jid
				monitor.setTask("Retrieving Jid");
				jid = getOfficialJid(psnId);
				monitor.worked(1);

				monitor.done("Successfully logged in");
			} else if (response.isServerError()) throw new PlaystationNetworkException(
					"PlayStationNetwork is under maintenance");
			else
				throw new PlaystationNetworkException("Error when logging in: "
						+ String.valueOf(response.getStatusCode()));

		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("login - Exiting");
		}

		return new PsnHttpToken(cookies, jid, session);

	}

	public void login(PsnHttpToken token, String username, String password,
			ProgressListener listener) throws ClientLoginException,
			IOException, PlaystationNetworkException {
		log.debug("loginToken - Entering");

		Objects.requireNonNull(username, "username may not be null");
		Objects.requireNonNull(password, "password may not be null");

		ProgressMonitor monitor = ProgressMonitor.createMonitor(listener);
		monitor.beginTask("Logging in", 3);

		CookieMap cookies = token.getCookies();
		String session;

		List<NameValuePair> params = httpHelper.getPairs("j_username",
				username, "j_password", password, "returnURL",
				"https://secure.eu.playstation.com/sign-in/confirmation/");

		HttpResponse response = null;

		HttpRequest builder = new HttpRequest(
				POST,
				new URL(
						"https://store.playstation.com/j_acegi_external_security_check?target=/external/loginDefault.action"))
				.payload(params, "UTF-8").use(cookies).requestCookies(false)
				.close(true);

		try {
			response = builder.execute(httpHelper);
			monitor.worked(1);

			if (response.isRedirection()) {
				String urlLocation = ((HttpResponseRedirect) response)
						.getRedirectUrl();

				monitor.setTask("Authenticating");
				builder.reset();
				builder.method(GET).url(new URL(urlLocation)).use(cookies)
						.requestCookies(false);
				response = builder.execute(httpHelper);
				isLoginValid(response);
				monitor.worked(1);
				response.close();

				// get session id and location
				urlLocation = ((HttpResponseRedirect) response).getRedirectUrl();
				session = urlLocation.substring(urlLocation
						.indexOf("?sessionId=") + 11);

				// get additional cookies
				// no need to reset builder
				builder.url(new URL(urlLocation)).close(true);
				response = builder.execute(httpHelper);
				monitor.worked(1);
				monitor.done("Successfully logged in");
			} else if (response.isServerError()) throw new PlaystationNetworkException(
					"PlayStationNetwork is under maintenance");
			else
				throw new PlaystationNetworkException("Error when logging in: "
						+ String.valueOf(response.getStatusCode()));

		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("login - Exiting");
		}

		token.setSession(session);
	}

	public void logout(PsnHttpToken token) throws IOException {
		log.debug("logout - Entering");

		try (HttpResponse response = new HttpRequest(GET, new URL(
				"https://secure.eu.playstation.com/logout/")).use(
				token.getCookies()).execute(httpHelper)) {
			// no op
		} finally {
			token.getCookies().purgeExpired(true);
			token.setSession(null);
			log.debug("logout - Exiting");
		}
	}

	public boolean isServiceOnline() throws IOException {
		log.debug("isServiceOnline - Entering");

		try (HttpResponse response = new HttpRequest(GET, new URL(
				"http://uk.playstation.com/sign-in/")).followRedirects(true)
				.execute(httpHelper)) {
			String url = response.getConnection().getURL().toString();
			if (url.startsWith("http://uk.playstation.com/registration/unavailable/")) return false;
		} finally {
			log.debug("isServiceOnline - Exiting");
		}

		return true;
	}

	public List<PsnFriend> getFriendList(PsnHttpToken token) throws IOException,
			TokenException, ClientException, PlaystationNetworkException {
		log.debug("getFriendList - Entering");
		HandlerXmlFriend handler;

		try (HttpResponse response = getTokenResponse(
				"http://uk.playstation.com/ajax/mypsn/friend/presence/",
				"http://uk.playstation.com/psn/mypsn/friends/",
				token.getCookies())) {
			handler = new HandlerXmlFriend();
			xmlParser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getFriendList - Exiting");
		}

		return handler.getFriendList();

	}

	public List<PsnGame> getFriendGameList(PsnHttpToken token, String friendPsnId)
			throws IOException, TokenException, ClientException,
			PlaystationNetworkException {
		log.debug("getFriendGameList [{}] - Entering", friendPsnId);
		HandlerHtmlFriendGame handler;

		try (HttpResponse response = getTokenResponse(
				String.format(
						"http://uk.playstation.com/psn/mypsn/trophies-compare/?friend=%s&mode=FRIENDS",
						friendPsnId),
				"http://uk.playstation.com/psn/mypsn/friends/",
				token.getCookies())) {

			if (response.isRedirection()) {
				throw new PlaystationNetworkException("Invalid friend PsnId");
			}

			handler = new HandlerHtmlFriendGame(friendPsnId);
			htmlParser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getFriendGameList - Exiting");
		}

		return handler.getGameList();
	}

	public List<PsnTrophy> getFriendTrophyList(PsnHttpToken token,
			String friendPsnId, String titleLinkId) throws IOException,
			TokenException, ClientException, PlaystationNetworkException {
		log.debug("getFriendTrophyList [{}, {}] - Entering", friendPsnId,
				titleLinkId);
		HandlerHtmlFriendTrophy handler;
		try (HttpResponse response = getTokenResponse(
				String.format(
						"http://uk.playstation.com/psn/mypsn/trophies-compare/detail/?title=%s&friend=%s&sortBy=game",
						titleLinkId, friendPsnId),
				"http://uk.playstation.com/psn/mypsn/trophies-compare/?friend="
						+ friendPsnId + "&mode=FRIENDS", token.getCookies())) {

			if (response.isRedirection()) {
				throw new PlaystationNetworkException(
						"Invalid friend PsnId or trophy link Id");
			}

			handler = new HandlerHtmlFriendTrophy(friendPsnId);
			htmlParser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getClientTrophyList - Exiting");
		}

		return handler.getTrophyList();

	}

	public List<PsnGame> getGameList(PsnHttpToken token) throws IOException,
			TokenException, ClientException, PlaystationNetworkException {
		log.debug("getGameList - Entering");
		HandlerHtmlUKGame handler;
		try (HttpResponse response = getTokenResponse(
				"http://uk.playstation.com/psn/mypsn/trophies/", null,
				token.getCookies())) {
			handler = new HandlerHtmlUKGame(token.getOnlineId());
			htmlParser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getClientGameList - Exiting");
		}

		return handler.getGameList();
	}

	public List<PsnTrophy> getTrophyList(PsnHttpToken token, String titleLinkId)
			throws ClientException, IOException, TokenException,
			PlaystationNetworkException {
		log.debug("getTrophyList [{}] - Entering", titleLinkId);
		HandlerHtmlUKTrophy handler;

		try (HttpResponse response = getTokenResponse(
				String.format(
						"http://uk.playstation.com/psn/mypsn/trophies/detail/?title=%s",
						titleLinkId),
				"http://uk.playstation.com/psn/mypsn/trophies/",
				token.getCookies())) {

			if (response.isRedirection()) {
				throw new PlaystationNetworkException("Invalid trophy link id");
			}

			handler = new HandlerHtmlUKTrophy(token.getOnlineId());
			htmlParser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getTrophyList - Exiting");
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

	private HttpResponse getTokenResponse(String url, String referer,
			CookieMap cookies) throws IOException, TokenException,
			PlaystationNetworkException {
		HttpRequest builder = new HttpRequest(GET, new URL(url))
				.readTimeout(0).use(cookies);
		if (referer != null) builder.header("Referer", referer);
		HttpResponse response = builder.execute(httpHelper);
		if (response.isRedirection()) {
			String redirect = ((HttpResponseRedirect) response).getRedirectUrl();
			if (redirect.contains("/registration/unavailable/")
					|| redirect.contains("/static/maintenance/")) throw new PlaystationNetworkException(
					"PlayStationNetwork is under maintenance");
			else if (redirect.contains("/registration/")) throw new TokenException();

		}

		return response;
	}

	public List<PsnGame> getPublicGameList(String psnId) throws IOException,
			ClientException {
		log.debug("getPublicGameList [{}] - Entering", psnId);
		HandlerHtmlUSGame handler;

		// HACK get around not showing trophy link Ids
		CookieList cookieList = new CookieList();
		cookieList.add(new Cookie(".playstation.com", "APPLICATION_SITE_URL",
				"http%3A//us.playstation.com/community/mytrophies/"));
		cookieList.add(new Cookie(".playstation.com",
				"APPLICATION_SIGNOUT_URL",
				"http%3A//us.playstation.com/index.htm"));

		try (HttpResponse response = new HttpRequest(
				GET,
				new URL(
						String.format(
								"http://us.playstation.com/playstation/psn/profile/%s/get_ordered_trophies_data",
								psnId)))
				.header("Referer", "http://us.playstation.com")
				.header("X-Requested-With", "XMLHttpRequest").put(cookieList)
				.execute(httpHelper)) {

			handler = new HandlerHtmlUSGame(psnId);
			htmlParser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getPublicGameList - Exiting");
		}

		return handler.getGames();
	}

	public List<PsnTrophy> getPublicTrophyList(String psnId,
			String titleLinkId, String gameId) throws IOException,
			ClientException {
		log.debug("getPublicTrophyList [{}, {}] - Entering", psnId, gameId);
		HandlerHtmlUSTrophy handler;

		ArrayList<NameValuePair> params = new ArrayList<>();
		params.add(new NameValuePair("sortBy", "id_asc"));
		params.add(new NameValuePair("titleId", titleLinkId));
		// HACK seems any title text will work
		params.add(new NameValuePair("title", "Generic Game Title"));

		try (HttpResponse response = new HttpRequest(
				POST,
				new URL(
						String.format(
								"http://us.playstation.com/playstation/psn/profile/%s/get_ordered_title_details_overlay_data",
								psnId)))
				.header("Referer", "http://us.playstation.com/")
				.header("X-Requested-With", "XMLHttpRequest")
				.header("Accept", "text/html").payload(params, "UTF-8")
				.execute(httpHelper)) {

			handler = new HandlerHtmlUSTrophy(psnId, gameId);
			htmlParser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getPublicTrophyList - Exiting");
		}

		return handler.getTrophyList();

	}

	public String getOfficialJid(String psnId) throws IOException {
		log.debug("getJid [{}] - Entering", psnId);
		String jid = null;

		String xmlPost = String
				.format("<?xml version='1.0' encoding='utf-8'?><searchjid platform='ps3' sv='%s'><online-id>%s</online-id></searchjid>",
						PS3_FIRMWARE_VERSION, psnId);

		// searchjid.usa should work with all countries
		try (HttpResponse response = new HttpRequestAuthenticate(
				POST,
				new URL(
						"http://searchjid.usa.np.community.playstation.net/basic_view/func/search_jid"))
				.payload(xmlPost.getBytes("UTF-8"))
				.header("User-Agent", AGENT_PS3_COMMUNITY)
				.header("Content-Type", "text/xml; charset=UTF-8")
				.execute(httpHelper)) {

			if (response instanceof HttpResponseAuthenticate) {
				log.error("Unauthorized [{}]",
						((HttpResponseAuthenticate) response).getAuthentication());
				throw new IOException("Authentication required");
			}
			String data = HttpResponse.toString(response);

			int start = data.indexOf("<jid>");
			if (start != -1) jid = data.substring(start + 5,
					data.indexOf("</jid>"));

		} finally {
			log.debug("getJid - Exiting");
		}

		return jid;
	}

	@SuppressWarnings("incomplete-switch")
	public String getOfficialFirmwareVersion(Platform platform)
			throws IOException {
		log.debug("getOfficialFirmwareVersion [{}] - Entering", platform);
		String version = null;

		String userAgent = null;
		switch (platform) {
			case PS4:
				throw new UnsupportedOperationException("PS4 not supported");
			case PS3:
				userAgent = AGENT_PS3_UPDATE;
				break;
			case VITA:
				userAgent = AGENT_VITA_LIBHTTP;
				break;
			case PSP:
				userAgent = AGENT_PSP_UPDATE;
				break;
			case UNKNOWN:
				throw new IllegalArgumentException(
						"Platform may not be UNKNOWN");
		}

		// not using getOfficialResponse because of different headers
		try (HttpResponse response = new HttpRequest(
				GET,
				new URL(
						String.format(
								"http://%s.%s.update.playstation.%s/update/%s/%s/us/%s-updatelist.%s",
								platform == Platform.PSP ? "fu01" : "fus01",
								platform.getTypeString(),
								platform == Platform.PSP ? "org" : "net",
								platform.getTypeString(),
								platform == Platform.PSP ? "list2" : "list",
								platform.getTypeString(),
								platform == Platform.VITA ? "xml" : "txt")))
				.header("User-Agent", userAgent)
				.header("Accept-Encoding", "identity").execute(httpHelper)) {
			String data = HttpResponse.toString(response);

			switch (platform) {
				case PS3:
					String[] dataArray = data.split(";");

					for (String part : dataArray)
						if (part.subSequence(0, part.indexOf("=")).toString()
								.equalsIgnoreCase("SystemSoftwareVersion")) {
							version = PS3_FIRMWARE_VERSION = part.substring(
									part.indexOf("=") + 1, part.length() - 2);
							break;
						}

					break;
				case VITA:
					Matcher matcher = Pattern.compile("label=\"\\S+\">")
							.matcher(data);
					matcher.find();
					String find = matcher.group();
					version = find.substring(find.indexOf("label=\"") + 7,
							find.lastIndexOf("\">"));
					break;
				case PSP:
					int start = data.indexOf("#SystemSoftwareVersion=");
					version = data.substring(start + 23,
							data.indexOf(";", start + 23));
					break;
			}
		} finally {
			log.debug("getOfficialFirmwareVersion - Exiting");
		}

		return version;
	}

	public PsnProfile getOfficialProfile(String jid) throws IOException,
			ClientException {
		HttpResponse response = null;
		log.debug("getProfile [{}] - Entering", jid);
		HandlerXmlProfile handler;

		String payload = String.format(
				"<profile platform='ps3' sv='%s'><jid>%s</jid></profile>",
				PS3_FIRMWARE_VERSION, jid);

		try {
			response = getOfficialResponse(
					"http://getprof.us.np.community.playstation.net/basic_view/func/get_profile",
					AGENT_PS3_COMMUNITY, payload);
			handler = new HandlerXmlProfile();
			xmlParser.parse(response.getStream(), handler, response.getCharset());
			response.close();
			if (handler.getProfile() == null) return null;

			payload = String
					.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid></nptrophy>",
							PS3_FIRMWARE_VERSION, jid);

			response = getOfficialResponse(
					"http://trophy.ww.np.community.playstation.net/trophy/func/get_user_info",
					AGENT_PS3_COMMUNITY, payload);

			xmlParser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getProfile - Exiting");
		}

		return handler.getProfile();
	}

	public List<PsnGameOfficial> getOfficialGameList(String jid, int start,
			int max, Platform... platforms) throws IOException,
			ClientException, PlaystationNetworkException {
		log.debug("getOfficialGameList [{}, {}, {}, {}] - Entering", jid,
				String.valueOf(start), String.valueOf(max), platforms);
		HandlerXmlGame handler;

		if (start <= 0) throw new IllegalArgumentException(
				"start index must be greater than 0");

		// if (max > 64) log.warn("max index is greater than 64");

		String payload = String
				.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid><start>%s</start><max>%s</max>%s</nptrophy>",
						PS3_FIRMWARE_VERSION, jid, String.valueOf(start),
						String.valueOf(max), getPlatformString(platforms));

		try (HttpResponse response = getOfficialResponse(
				"http://trophy.ww.np.community.playstation.net/trophy/func/get_title_list",
				AGENT_PS3_APPLICATION, payload)) {
			handler = new HandlerXmlGame(jid);
			xmlParser.parse(response.getStream(), handler, response.getCharset());
			if (handler.getResult().equals("05")) throw new PlaystationNetworkException(
					"Jid invalid");
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getOfficialGameList - Exiting");
		}

		return handler.getGames();

	}

	public List<PsnTrophyOfficial> getOfficialTrophyList(String jid,
			String gameId) throws IOException, PlaystationNetworkException,
			ClientException {
		log.debug("getOfficialTrophyList [{}, {}] - Entering", jid, gameId);
		HandlerXmlTrophy handler;

		if (!PsnUtils.isValidGameId(gameId)) throw new IllegalArgumentException(
				"Must be a valid PsnGame Id");

		String payload = String
				.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid><list><info npcommid='%s'><target>FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF</target></info></list></nptrophy>",
						PS3_FIRMWARE_VERSION, jid, gameId);

		try (HttpResponse response = getOfficialResponse(
				"http://trophy.ww.np.community.playstation.net/trophy/func/get_trophies",
				AGENT_PS3_APPLICATION, payload)) {

			handler = new HandlerXmlTrophy(jid);
			xmlParser.parse(response.getStream(), handler, response.getCharset());
			if (handler.getResult().equals("05")) throw new PlaystationNetworkException(
					"Jid invalid");
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getOfficialTrophyList - Exiting");
		}

		return handler.getTrophyList();

	}

	public List<PsnTrophyOfficial> getOfficialLatestTrophyList(String jid,
			int max, Platform... platforms) throws PlaystationNetworkException,
			IOException, ClientException {
		log.debug("getOfficialLatestTrophyList [{}, {}, {}] - Entering", jid,
				String.valueOf(max), platforms);
		HandlerXmlTrophy handler;

		// if (max > 64) log.warn("max index is greater than 64");

		String payload = String
				.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid><max>%s</max>%s</nptrophy>",
						PS3_FIRMWARE_VERSION, jid, String.valueOf(max),
						getPlatformString(platforms));

		try (HttpResponse response = getOfficialResponse(
				"http://trophy.ww.np.community.playstation.net/trophy/func/get_latest_trophies",
				AGENT_PS3_APPLICATION, payload)) {

			handler = new HandlerXmlTrophy(jid);
			xmlParser.parse(response.getStream(), handler, response.getCharset());
			if (handler.getResult().equals("05")) throw new PlaystationNetworkException(
					"Jid invalid");
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getOfficialLatestTrophyList - Exiting");
		}

		return handler.getTrophyList();

	}

	public List<PsnTrophyOfficial> getOfficialTrophyListSince(String jid,
			int max, String since, Platform... platforms) throws IOException,
			PlaystationNetworkException, ClientException {
		HandlerXmlTrophy handler;
		// if (max > 64) log.warn("max index is greater than 64");

		log.debug("getOfficialTrophyListSince [{}, {}, {}, {}] - Entering",
				jid, String.valueOf(max), since, platforms);

		String payload = String
				.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid><max>%s</max><since>%s</since>%s</nptrophy>",
						PS3_FIRMWARE_VERSION, jid, String.valueOf(max), since,
						getPlatformString(platforms));

		try (HttpResponse response = getOfficialResponse(
				"http://trophy.ww.np.community.playstation.net/trophy/func/get_latest_trophies",
				AGENT_PS3_APPLICATION, payload)) {

			handler = new HandlerXmlTrophy(jid);
			xmlParser.parse(response.getStream(), handler, response.getCharset());
			if (handler.getResult().equals("05")) throw new PlaystationNetworkException(
					"jid invalid");
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			log.debug("getOfficialTrophyListSince - Exiting");
		}

		return handler.getTrophyList();
	}

	private HttpResponse getOfficialResponse(String url, String userAgent,
			String payload) throws IOException {
		HttpResponse response = new HttpRequestAuthenticate(POST, new URL(url))
				.header("Content-Type", "text/xml; charset=UTF-8")
				.header("Accept-Encoding", "identity")
				.header("User-Agent", userAgent)
				.payload(payload.getBytes("UTF-8")).execute(httpHelper);

		if (response instanceof HttpResponseAuthenticate) {
			log.error("Unauthorized [{}]",
					((HttpResponseAuthenticate) response).getAuthentication());
			throw new IOException("Authentication required");
		}

		return response;
	}

	private static String getPlatformString(Platform[] platforms) {
		if (platforms == null) return "";
		String platformString = "";
		for (Platform platform : platforms) {
			if (platform == Platform.UNKNOWN) throw new IllegalArgumentException(
					"Platform may not be UNKNOWN");
			else if (platform == Platform.PS4) throw new UnsupportedOperationException(
					"PS4 not supported");
			platformString += String.format("<pf>%s</pf>",
					platform.getTypeString());
		}

		return platformString;
	}

}
