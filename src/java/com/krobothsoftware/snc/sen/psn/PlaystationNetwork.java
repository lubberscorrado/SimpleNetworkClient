package com.krobothsoftware.snc.sen.psn;

import static com.krobothsoftware.commons.network.Method.GET;
import static com.krobothsoftware.commons.network.Method.POST;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.krobothsoftware.commons.network.NetworkHelper;
import com.krobothsoftware.commons.network.RequestBuilder;
import com.krobothsoftware.commons.network.Response;
import com.krobothsoftware.commons.network.ResponseAuthenticate;
import com.krobothsoftware.commons.network.ResponseRedirect;
import com.krobothsoftware.commons.network.authentication.AuthScope;
import com.krobothsoftware.commons.network.authentication.AuthenticationManager;
import com.krobothsoftware.commons.network.authentication.DigestAuthentication;
import com.krobothsoftware.commons.network.authentication.RequestBuilderAuthenticate;
import com.krobothsoftware.commons.network.value.Cookie;
import com.krobothsoftware.commons.network.value.CookieList;
import com.krobothsoftware.commons.network.value.CookieMap;
import com.krobothsoftware.commons.network.value.NameValuePair;
import com.krobothsoftware.commons.parse.ParseException;
import com.krobothsoftware.commons.progress.ProgressListener;
import com.krobothsoftware.commons.progress.ProgressMonitor;
import com.krobothsoftware.commons.util.CommonUtils;
import com.krobothsoftware.snc.ClientException;
import com.krobothsoftware.snc.ClientLoginException;
import com.krobothsoftware.snc.TokenException;
import com.krobothsoftware.snc.sen.Platform;
import com.krobothsoftware.snc.sen.SonyEntertainmentNetwork;
import com.krobothsoftware.snc.sen.psn.internal.HandlerHtmlFriendGame;
import com.krobothsoftware.snc.sen.psn.internal.HandlerHtmlFriendTrophy;
import com.krobothsoftware.snc.sen.psn.internal.HandlerHtmlUKGame;
import com.krobothsoftware.snc.sen.psn.internal.HandlerHtmlUKTrophy;
import com.krobothsoftware.snc.sen.psn.internal.HandlerHtmlUSGame;
import com.krobothsoftware.snc.sen.psn.internal.HandlerHtmlUSTrophy;
import com.krobothsoftware.snc.sen.psn.internal.HandlerXmlFriend;
import com.krobothsoftware.snc.sen.psn.internal.HandlerXmlGame;
import com.krobothsoftware.snc.sen.psn.internal.HandlerXmlProfile;
import com.krobothsoftware.snc.sen.psn.internal.HandlerXmlTrophy;
import com.krobothsoftware.snc.sen.psn.model.PsnFriend;
import com.krobothsoftware.snc.sen.psn.model.PsnGame;
import com.krobothsoftware.snc.sen.psn.model.PsnGameOfficial;
import com.krobothsoftware.snc.sen.psn.model.PsnProfile;
import com.krobothsoftware.snc.sen.psn.model.PsnTrophy;
import com.krobothsoftware.snc.sen.psn.model.PsnTrophyOfficial;

/**
 * Sony's PlayStationNetwork client for getting games, trophies, and friends.
 * 
 * There are three types of methods.
 * 
 * <ul>
 * <li>Token - Data retrieved by login Token: Friends, Games, and Trophies.</li>
 * <li>Public - Publically data without any login token. Uses US site for public
 * games and trophies.</li>
 * <li>Official - Methods that are internally used in platforms and not public
 * to get data.</li>
 * </ul>
 * 
 * <p>
 * Call {@link #login(String, String, ProgressListener)} to obtain a login
 * token.
 * </p>
 * 
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 * 
 */
public class PlaystationNetwork extends SonyEntertainmentNetwork {

	/**
	 * Agent used for getting profile and jid data in <i>Official</i> methods.
	 * 
	 * @since SEN-PSN 1.0
	 */
	public static final String AGENT_PS3_COMMUNITY = "PS3Community-agent/1.0.0 libhttp/1.0.0";

	/**
	 * Agent used for getting trophy and game data in <i>Official</i> methods.
	 * 
	 * @since SEN-PSN 1.0
	 */
	public static final String AGENT_PS3_APPLICATION = "PS3Application libhttp/3.5.5-000 (CellOS)";

	/**
	 * PS3 Update Agent.
	 * 
	 * @since SEN-PSN 1.0
	 */
	public static final String AGENT_PS3_UPDATE = "PS3Update-agent/1.0.0 libhttp/1.0.0";

	/**
	 * Agent used in the PS3 console's browser.
	 * 
	 * @since SEN-PSN 1.0
	 */
	public static final String AGENT_PS3_BROWSER = "Mozilla/5.0 (PLAYSTATION 3; 1.00)";

	/**
	 * Seems to be the agent used for checking for game and app updates. Don't
	 * know for sure.
	 * 
	 * @since SEN-PSN 1.0
	 */
	public static final String AGENT_PS3_HTTPCLIENT = "Sony-HTTPClient/1.0 [PS3 test]";

	/**
	 * Vita's main user agent.
	 * 
	 * @since SEN-PSN 1.0
	 */
	public static final String AGENT_VITA_LIBHTTP = "libhttp/1.66 (PS Vita)";

	/**
	 * Agent used for vita's browser.
	 * 
	 * @since SEN-PSN 1.0
	 */
	public static final String AGENT_VITA_BROWSER = "Mozilla/5.0 (Playstation Vita 1.50) AppleWebKit/531.22.8 (KHTML, like Gecko)﻿ Silk/3.2﻿";

	/**
	 * Agent used for PSP's browser.
	 * 
	 * @since SEN-PSN 1.0
	 */
	public static final String AGENT_PSP_BROWSER = "Mozilla/4.0 (PSP (PlayStation Portable); 2.00)";

	/**
	 * PSP Update Agent.
	 * 
	 * @since SEN-PSN 1.0
	 */
	public static final String AGENT_PSP_UPDATE = "PSPUpdate-agent/1.0.0 libhttp/1.0.0";

	/**
	 * Current PS3 firmware version as of 4/25/13. Is not final since firmwares
	 * change.
	 * 
	 * @since SEN-PSN 1.0
	 */
	public static String PS3_FIRMWARE_VERSION = "4.41";

	/**
	 * Ticket Id for US cookies.
	 */
	private static String PSN_TICKET_ID = "*";

	/**
	 * Sets PsnId for TICKET and PSNTICKET cookies. Will only take effect once
	 * new client is initiated. Default value is <code>"*"</code>.
	 * 
	 * @param id
	 *            new PsnId
	 * @since SEN-PSN 1.0
	 */
	public static void setPsnTicketId(String id) {
		PSN_TICKET_ID = id;
	}

	/**
	 * Creates new PlayStationNetwork client.
	 * 
	 * <ul>
	 * <li>Creates TICKET and PSNTICKET cookies for <i>public</i> methods</li>
	 * <li>Sets up Authentications: c7y-basic01, and c7y-trophy01 for
	 * <i>official</i> methods</li>
	 * </ul>
	 * 
	 * @see #setPsnTicketId(String)
	 * @since SEN-PSN 1.0
	 */
	public PlaystationNetwork() {
		super(PlaystationNetwork.class.getName());
		networkHelper.getCookieManager().putCookie(
				PsnUtils.createCookieTicket(PSN_TICKET_ID), true);
		networkHelper.getCookieManager().putCookie(
				PsnUtils.createCookiePsnTicket(PSN_TICKET_ID), true);
		AuthenticationManager authManager = networkHelper
				.getAuthorizationManager();
		DigestAuthentication basicDigest = new DigestAuthentication(
				"c7y-basic01", "A9QTbosh0W0D^{7467l-n_>2Y%JG^v>o".toCharArray());
		authManager.addAuthentication(new AuthScope(
				"searchjid.usa.np.community.playstation.net"), basicDigest);
		authManager.addAuthentication(new AuthScope(
				"getprof.us.np.community.playstation.net"), basicDigest);
		authManager.addAuthentication(new AuthScope(
				"trophy.ww.np.community.playstation.net"),
				new DigestAuthentication("c7y-trophy01",
						"jhlWmT0|:0!nC:b:#x/uihx'Y74b5Ycx".toCharArray()));

	}

	/**
	 * Performs a full login to UK account and quietly into US site.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * https://store.playstation.com/j_acegi_external_security_check?target=/external/loginDefault.action
	 * https://store.playstation.com/j_acegi_external_security_check?target=/external/loginDefault.action
	 * https://store.playstation.com/external/loginDefault.action (;jsessionid=****) is appended at end if not set
	 * https://secure.eu.playstation.com/sign-in/confirmation/ (?sessionId=****)
	 * http://us.playstation.com/uwps/HandleIFrameRequests (?sessionId=****)
	 * http://searchjid.usa.np.community.playstation.net/basic_view/func/search_jid
	 * </pre>
	 * 
	 * </p>
	 * 
	 * <p>
	 * Data retrieved
	 * <ul>
	 * <li>Login cookies</li>
	 * <li>Jid</li>
	 * <li>SessionId</li>
	 * </ul>
	 * </p>
	 * 
	 * @param username
	 *            email of account
	 * @param password
	 *            password of account
	 * @param listener
	 *            monitor progress events. May be null.
	 * @return login token
	 * @throws IllegalArgumentException
	 *             if username or password are null
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClientLoginException
	 *             if credentials are incorrect
	 * @throws PlaystationNetworkException
	 *             if service is down or login failed
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public PsnToken login(String username, String password,
			ProgressListener listener) throws IOException,
			PlaystationNetworkException, ClientLoginException {
		log.debug("login - Entering");
		Response response = null;

		if (username == null || password == null) throw new IllegalArgumentException(
				"username and password may not be null");

		ProgressMonitor monitor = ProgressMonitor.newInstance(listener);
		monitor.beginTask("Logging in", 6);
		CookieMap cookies = new CookieMap();
		String jid;
		String session;

		List<NameValuePair> params = NetworkHelper.getPairs("j_username",
				username, "j_password", password, "returnURL",
				"https://secure.eu.playstation.com/sign-in/confirmation/");

		try {
			response = new RequestBuilder(
					POST,
					new URL(
							"https://store.playstation.com/j_acegi_external_security_check?target=/external/loginDefault.action"))
					.payload(params, "UTF-8").use(cookies)
					.requestCookies(false).close(true).execute(networkHelper);
			monitor.worked(1);

			switch (response.getStatusCode()) {
				case HttpURLConnection.HTTP_MOVED_TEMP:
					String urlLocation = ((ResponseRedirect) response)
							.getRedirectUrl();

					monitor.setTask("Authenticating");
					response = new RequestBuilder(GET, new URL(urlLocation))
							.use(cookies).requestCookies(false)
							.execute(networkHelper);
					isLoginValid(response);
					monitor.worked(1);
					response.close();

					// get session id and location
					urlLocation = ((ResponseRedirect) response)
							.getRedirectUrl();
					session = urlLocation.substring(urlLocation
							.indexOf("?sessionId=") + 11);

					// get additional cookies
					response = new RequestBuilder(GET, new URL(urlLocation))
							.use(cookies).requestCookies(false).close(true)
							.execute(networkHelper);
					monitor.worked(1);

					// get psn id

					// US method
					monitor.setTask("Retrieving PsnId");
					response = new RequestBuilder(
							GET,
							new URL(
									String.format(
											"http://us.playstation.com/uwps/HandleIFrameRequests?sessionId=%s",
											session))).use(cookies)
							.requestCookies(false).close(true)
							.execute(networkHelper);

					Cookie cookie;
					// get psnId
					String psnId = null;
					cookie = cookies.getCookie(".playstation.com", "ph");
					if (cookie != null) psnId = cookie.getValue();
					else
						log.warn("Couldn't retrieve psnId in ph cookie");

					if (psnId == null) {
						log.info("Retrieving PsnId with userinfo cookie");
						cookie = cookies.getCookie(".playstation.com",
								"userinfo");
						if (cookie == null) {
							log.warn("Couldn't retrieve userinfo cookie");
						} else {
							response = new RequestBuilder(
									GET,
									new URL(
											String.format(
													"http://us.playstation.com/uwps/CookieHandler?cookieName=userinfo&id=%s",
													String.valueOf(Math
															.random()))))
									.header("X-Requested-With",
											"XMLHttpRequest")
									.header("Cookie", cookie.getCookieString())
									.storeCookies(false).execute(networkHelper);
							String content = Response.toString(response);
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
					break;
				case HttpURLConnection.HTTP_UNAVAILABLE:
					throw new PlaystationNetworkException(
							"PlayStationNetwork is under maintenance");
				default:
					throw new PlaystationNetworkException(
							"Error when logging in: "
									+ response.getStatusCode());
			}

		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("login - Exiting");
		}

		return new PsnToken(cookies, jid, session);

	}

	/**
	 * Performs a re-login for given <code>token</code>; updates token.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * https://store.playstation.com/j_acegi_external_security_check?target=/external/loginDefault.action
	 * https://store.playstation.com/j_acegi_external_security_check?target=/external/loginDefault.action
	 * https://store.playstation.com/external/loginDefault.action (;jsessionid=****) is appended at end if not set
	 * https://secure.eu.playstation.com/sign-in/confirmation/ (?sessionId=****)
	 * </pre>
	 * 
	 * </p>
	 * 
	 * <p>
	 * Data retrieved
	 * <ul>
	 * <li>Updated login cookies</li>
	 * <li>New sessionId</li>
	 * </ul>
	 * </p>
	 * 
	 * @param token
	 *            login token to log into again
	 * @param username
	 *            email of account
	 * @param password
	 *            password of account
	 * @param listener
	 *            monitor progress events. May be null.
	 * @throws IllegalArgumentException
	 *             if username or password are null
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClientLoginException
	 *             if credentials are incorrect
	 * @throws PlaystationNetworkException
	 *             if service is down or login failed
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public void login(PsnToken token, String username, String password,
			ProgressListener listener) throws ClientLoginException,
			IOException, PlaystationNetworkException {
		log.debug("loginToken - Entering");
		Response response = null;

		if (username == null || password == null) throw new IllegalArgumentException(
				"username and password may not be null");

		ProgressMonitor monitor = ProgressMonitor.newInstance(listener);
		monitor.beginTask("Logging in", 3);

		CookieMap cookies = token.getCookies();
		String session;

		List<NameValuePair> params = NetworkHelper.getPairs("j_username",
				username, "j_password", password, "returnURL",
				"https://secure.eu.playstation.com/sign-in/confirmation/");

		try {

			response = new RequestBuilder(
					POST,
					new URL(
							"https://store.playstation.com/j_acegi_external_security_check?target=/external/loginDefault.action"))
					.payload(params, "UTF-8").use(cookies)
					.requestCookies(false).close(true).execute(networkHelper);
			monitor.worked(1);

			switch (response.getStatusCode()) {
				case HttpURLConnection.HTTP_MOVED_TEMP:
					String urlLocation = ((ResponseRedirect) response)
							.getRedirectUrl();

					monitor.setTask("Authenticating");
					isLoginValid(response = new RequestBuilder(GET, new URL(
							urlLocation)).use(cookies).requestCookies(false)
							.execute(networkHelper));
					monitor.worked(1);
					response.close();

					// get session id and location
					urlLocation = ((ResponseRedirect) response)
							.getRedirectUrl();
					session = urlLocation.substring(urlLocation
							.indexOf("?sessionId=") + 11);

					// get additional cookies
					response = new RequestBuilder(GET, new URL(urlLocation))
							.use(cookies).close(true).requestCookies(false)
							.execute(networkHelper);
					monitor.done("Successfully logged in");
					break;
				case HttpURLConnection.HTTP_UNAVAILABLE:
					throw new PlaystationNetworkException(
							"PlayStationNetwork is under maintenance");
				default:
					throw new ClientLoginException("Error when logging in: "
							+ response.getStatusCode());
			}

		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("loginToken - Exiting");
		}

		token.setSession(session);
	}

	/**
	 * Logs out of account(token). Clearing cookies in token and and setting
	 * SessionId to null is an alternative.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * https://secure.eu.playstation.com/logout/
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param token
	 *            psn login token
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public void logout(PsnToken token) throws IOException {
		log.debug("logout - Entering");
		Response response = null;

		try {
			response = new RequestBuilder(GET, new URL(
					"https://secure.eu.playstation.com/logout/")).use(
					token.getCookies()).execute(networkHelper);
		} finally {
			CommonUtils.closeQuietly(response);
			token.getCookies().purgeExpired(true);
			token.setSession(null);
			log.debug("logout - Exiting");
		}
	}

	/**
	 * Checks if psn service is online.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * http://uk.playstation.com/sign-in/
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @return true, if psn is online
	 * @throws IOException
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public boolean isServiceOnline() throws IOException {
		log.debug("isServiceOnline - Entering");
		Response response = null;

		try {
			response = new RequestBuilder(GET, new URL(
					"http://uk.playstation.com/sign-in/"))
					.followRedirects(true).execute(networkHelper);
			String url = response.getConnection().getURL().toString();
			if (url.startsWith("http://uk.playstation.com/registration/unavailable/")) return false;
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("isServiceOnline - Exiting");
		}

		return true;
	}

	/**
	 * Retrieves token friend list.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * https://secure.eu.playstation.com/ajax/mypsn/friend/presence/
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param token
	 *            psn login token
	 * @return list of friends or empty if account has none
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TokenException
	 *             if token has expired or invalid
	 * @throws ClientException
	 *             if parser encountered an error
	 * @throws PlaystationNetworkException
	 *             if psn is not available
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public List<PsnFriend> getFriendList(PsnToken token) throws IOException,
			TokenException, ClientException, PlaystationNetworkException {
		log.debug("getFriendList - Entering");
		Response response = null;
		HandlerXmlFriend handler;

		try {
			response = getTokenResponse(
					"http://uk.playstation.com/ajax/mypsn/friend/presence/",
					"http://uk.playstation.com/psn/mypsn/friends/",
					token.getCookies());

			handler = new HandlerXmlFriend();
			parser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getFriendList - Exiting");
		}

		return handler.getFriendList();

	}

	/**
	 * Retrieves token friend's game list.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * http://uk.playstation.com/psn/mypsn/trophies-compare/?friend= (friend psnId) &mode=FRIENDS
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param token
	 *            psn login token
	 * @param friendPsnId
	 *            friend psn id
	 * @return list of friend's game list or empty if friend has none
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TokenException
	 *             if token has expired or invalid
	 * @throws ClientException
	 *             if parser encountered an error
	 * @throws PlaystationNetworkException
	 *             if friend psnId is invalid or psn is not available
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public List<PsnGame> getFriendGameList(PsnToken token, String friendPsnId)
			throws IOException, TokenException, ClientException,
			PlaystationNetworkException {
		log.debug("getFriendGameList [{}] - Entering", friendPsnId);
		Response response = null;
		HandlerHtmlFriendGame handler;

		try {

			response = getTokenResponse(
					String.format(
							"http://uk.playstation.com/psn/mypsn/trophies-compare/?friend=%s&mode=FRIENDS",
							friendPsnId),
					"http://uk.playstation.com/psn/mypsn/friends/",
					token.getCookies());

			if (response instanceof ResponseRedirect) {
				throw new PlaystationNetworkException("Invalid friend PsnId");
			}

			handler = new HandlerHtmlFriendGame(friendPsnId);
			parser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getFriendGameList - Exiting");
		}

		return handler.getGameList();
	}

	/**
	 * Retrieves token friend's trophy list.
	 * 
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * http://uk.playstation.com/psn/mypsn/trophies-compare/detail/?title= (trophy linkId) &friend= (friend psnId) &sortBy=game
	 * </pre>
	 * 
	 * </p>
	 * 
	 * 
	 * @param token
	 *            psn login token
	 * @param friendPsnId
	 *            friend psn id
	 * @param titleLinkId
	 *            UK title link id
	 * @return list of friend's trophy list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TokenException
	 *             if token has expired or invalid
	 * @throws ClientException
	 *             if parser encountered an error
	 * @throws PlaystationNetworkException
	 *             if friend psnId or trophy link Id is invalid, or psn is not
	 *             available
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public List<PsnTrophy> getFriendTrophyList(PsnToken token,
			String friendPsnId, String titleLinkId) throws IOException,
			TokenException, ClientException, PlaystationNetworkException {
		log.debug("getFriendTrophyList [{}, {}] - Entering", friendPsnId,
				titleLinkId);
		Response response = null;
		HandlerHtmlFriendTrophy handler;
		try {

			response = getTokenResponse(
					String.format(
							"http://uk.playstation.com/psn/mypsn/trophies-compare/detail/?title=%s&friend=%s&sortBy=game",
							titleLinkId, friendPsnId),
					"http://uk.playstation.com/psn/mypsn/trophies-compare/?friend="
							+ friendPsnId + "&mode=FRIENDS", token.getCookies());

			if (response instanceof ResponseRedirect) {
				throw new PlaystationNetworkException(
						"Invalid friend PsnId or trophy link Id");
			}

			handler = new HandlerHtmlFriendTrophy(friendPsnId);
			parser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getClientTrophyList - Exiting");
		}

		return handler.getTrophyList();

	}

	/**
	 * Retrieves token game list.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * http://uk.playstation.com/psn/mypsn/trophies/
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param token
	 *            psn login token
	 * @return list of games or empty if account has none
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TokenException
	 *             if token has expired or invalid
	 * @throws ClientException
	 *             if parser encountered an error
	 * @throws PlaystationNetworkException
	 *             if psn is not available
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public List<PsnGame> getGameList(PsnToken token) throws IOException,
			TokenException, ClientException, PlaystationNetworkException {
		log.debug("getGameList - Entering");
		Response response = null;
		HandlerHtmlUKGame handler;

		try {
			response = getTokenResponse(
					"http://uk.playstation.com/psn/mypsn/trophies/", null,
					token.getCookies());

			handler = new HandlerHtmlUKGame(token.getOnlineId());
			parser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getClientGameList - Exiting");
		}

		return handler.getGameList();
	}

	/**
	 * Retrieves token trophy list.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * http://uk.playstation.com/psn/mypsn/trophies/detail/?title= (trophy link Id)
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param token
	 *            psn login token
	 * @param titleLinkId
	 *            UK title link Id
	 * @return list of trophies
	 * @throws ClientException
	 *             if parser encountered an error
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TokenException
	 *             if token has expired or invalid
	 * @throws PlaystationNetworkException
	 *             if trophy link id is invalid or psn is not available
	 */
	@SuppressWarnings("resource")
	public List<PsnTrophy> getTrophyList(PsnToken token, String titleLinkId)
			throws ClientException, IOException, TokenException,
			PlaystationNetworkException {
		log.debug("getTrophyList [{}] - Entering", titleLinkId);
		Response response = null;
		HandlerHtmlUKTrophy handler;

		try {
			response = getTokenResponse(
					String.format(
							"http://uk.playstation.com/psn/mypsn/trophies/detail/?title=%s",
							titleLinkId),
					"http://uk.playstation.com/psn/mypsn/trophies/",
					token.getCookies());

			if (response instanceof ResponseRedirect) {
				throw new PlaystationNetworkException("Invalid trophy link id");
			}

			handler = new HandlerHtmlUKTrophy(token.getOnlineId());
			parser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getTrophyList - Exiting");
		}

		return handler.getTrophyList();
	}

	/**
	 * If response is a redirect, it's valid. Does a streaming check for
	 * keywords <tt>Incorrect</tt> and <tt>maintenance</tt>.
	 */
	private boolean isLoginValid(Response response) throws IOException,
			ClientLoginException, PlaystationNetworkException {
		if (response instanceof ResponseRedirect) return true;

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

	/**
	 * Sets up client(UK) request and checks response if token is expired or psn
	 * is down.
	 */
	private Response getTokenResponse(String url, String referer,
			CookieMap cookies) throws IOException, TokenException,
			PlaystationNetworkException {
		RequestBuilder builder = new RequestBuilder(GET, new URL(url))
				.readTimeout(0).use(cookies);
		if (referer != null) builder.header("Referer", referer);
		Response response = builder.execute(networkHelper);
		if (response instanceof ResponseRedirect) {
			String redirect = ((ResponseRedirect) response).getRedirectUrl();
			if (redirect.contains("/registration/unavailable/")
					|| redirect.contains("/static/maintenance/")) throw new PlaystationNetworkException(
					"PlayStationNetwork is under maintenance");
			else if (redirect.contains("/registration/")) throw new TokenException();

		}
		return response;
	}

	/**
	 * Retrieve the public game list from US site. If account doesn't exist, an
	 * empty list will still be returned.
	 * 
	 * <p>
	 * May not get all given games for psnId.
	 * </p>
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * http://us.playstation.com/playstation/psn/profile/ (psnId) /get_ordered_trophies_data
	 * </pre>
	 * 
	 * </p>
	 * 
	 * <p>
	 * Platform will {@link Platform#UNKNOWN} because there is no way to tell.
	 * </p>
	 * 
	 * @param psnId
	 *            psn id
	 * @return list of games or empty if account has none
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClientException
	 *             if parser encountered an error
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public List<PsnGame> getPublicGameList(String psnId) throws IOException,
			ClientException {
		log.debug("getPublicGameList [{}] - Entering", psnId);
		Response response = null;
		HandlerHtmlUSGame handler;

		// HACK get around not showing trophy link Ids
		CookieList cookieList = new CookieList();
		cookieList.add(new Cookie(".playstation.com", "APPLICATION_SITE_URL",
				"http%3A//us.playstation.com/community/mytrophies/"));
		cookieList.add(new Cookie(".playstation.com",
				"APPLICATION_SIGNOUT_URL",
				"http%3A//us.playstation.com/index.htm"));

		try {
			response = new RequestBuilder(
					GET,
					new URL(
							String.format(
									"http://us.playstation.com/playstation/psn/profile/%s/get_ordered_trophies_data",
									psnId)))
					.header("Referer", "http://us.playstation.com")
					.header("X-Requested-With", "XMLHttpRequest")
					.put(cookieList).execute(networkHelper);

			handler = new HandlerHtmlUSGame(psnId);
			parser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getPublicGameList - Exiting");
		}

		return handler.getGames();
	}

	/**
	 * Retrieve public trophy list. If <gameId> is null, it will be set only if
	 * there is a earned trophy. Needs to extract Id from image.
	 * 
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * http://us.playstation.com/playstation/psn/profile/ (psnId) /get_ordered_title_details_data
	 * </pre>
	 * 
	 * </p>
	 * 
	 * <p>
	 * <b>WARNING</b>: With new US site, trophy date earned will always be null
	 * because there isn't a way to retrieve it. The description may an
	 * ellipsis(...) at the end if it is large.
	 * </p>
	 * 
	 * 
	 * @param psnId
	 *            psn id
	 * @param titleLinkId
	 *            US trophy link id
	 * @param gameId
	 *            Optional <i>Official</i> game Id. </b>Will not affect
	 *            request.</b>
	 * @return list of trophies or empty list if user has none. Will return an
	 *         empty list if title link Id is invalid.
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClientException
	 *             if parser encountered an error or US login cookies invalid
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public List<PsnTrophy> getPublicTrophyList(String psnId,
			String titleLinkId, String gameId) throws IOException,
			ClientException {
		log.debug("getPublicTrophyList [{}, {}] - Entering", psnId, gameId);
		Response response = null;
		HandlerHtmlUSTrophy handler;

		try {

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new NameValuePair("sortBy", "id_asc"));
			params.add(new NameValuePair("titleId", titleLinkId));
			// HACK seems any title text will work
			params.add(new NameValuePair("title", "Generic Game Title"));

			response = new RequestBuilder(
					POST,
					new URL(
							String.format(
									"http://us.playstation.com/playstation/psn/profile/%s/get_ordered_title_details_overlay_data",
									psnId)))
					.header("Referer", "http://us.playstation.com/")
					.header("X-Requested-With", "XMLHttpRequest")
					.header("Accept", "text/html").payload(params, "UTF-8")
					.execute(networkHelper);

			handler = new HandlerHtmlUSTrophy(psnId, gameId);
			parser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getPublicTrophyList - Exiting");
		}

		return handler.getTrophyList();

	}

	/**
	 * Retrieves Jid for giving PsnId. Every psnId should be on the US server no
	 * matter what country.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * http://searchjid.usa.np.community.playstation.net/basic_view/func/search_jid
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param psnId
	 *            psn id
	 * @return jid or null if not found
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public String getOfficialJid(String psnId) throws IOException {
		Response response = null;
		String jid = null;
		log.debug("getJid [{}] - Entering", psnId);

		String xmlPost = String
				.format("<?xml version='1.0' encoding='utf-8'?><searchjid platform='ps3' sv='%s'><online-id>%s</online-id></searchjid>",
						PS3_FIRMWARE_VERSION, psnId);

		try {

			// searchjid.usa should work with all countries
			response = new RequestBuilderAuthenticate(
					POST,
					new URL(
							"http://searchjid.usa.np.community.playstation.net/basic_view/func/search_jid"))
					.payload(xmlPost.getBytes("UTF-8"))
					.header("User-Agent", AGENT_PS3_COMMUNITY)
					.header("Content-Type", "text/xml; charset=UTF-8")
					.execute(networkHelper);

			if (response instanceof ResponseAuthenticate) {
				log.error("Unauthorized [{}]",
						((ResponseAuthenticate) response).getAuthentication());
				throw new IOException("Authentication required");
			}
			String data = Response.toString(response);

			int start = data.indexOf("<jid>");
			if (start != -1) jid = data.substring(start + 5,
					data.indexOf("</jid>"));

		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getJid - Exiting");
		}

		return jid;
	}

	/**
	 * Retrieves official firmware version of platform type.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * (PS3) http://fus01.ps3.update.playstation.net/update/ps3/list/us/ps3-updatelist.txt
	 * (VITA) http://fus01.psp2.update.playstation.net/update/psp2/list/us/psp2-updatelist.xml
	 * (PSP) http://fu01.psp.update.playstation.org/update/psp/list2/us/psp-updatelist.txt
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param platform
	 *            platform to check firmware
	 * @return firmware version of platform
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws IllegalArgumentException
	 *             if platform is invalid, UNKNOWN
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public String getOfficialFirmwareVersion(Platform platform)
			throws IOException {
		Response response = null;
		String version = null;
		log.debug("getOfficialFirmwareVersion [{}] - Entering", platform);
		try {
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
			response = new RequestBuilder(
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
					.header("Accept-Encoding", "identity")
					.execute(networkHelper);

			String data = Response.toString(response);

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
				default:
					throw new UnsupportedOperationException();
			}
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getOfficialFirmwareVersion - Exiting");
		}

		return version;
	}

	/**
	 * Retrieves official profile. Will not check if valid Jid.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * http://getprof.us.np.community.playstation.net/basic_view/func/get_profile
	 * http://trophy.ww.np.community.playstation.net/trophy/func/get_user_info
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param jid
	 *            jid
	 * @return profile, null if not found
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClientException
	 *             if parser encountered an error or US login cookies invalid
	 * @see com.krobothsoftware.snc.sen.psn.PsnUtils#isValidJid(String)
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public PsnProfile getOfficialProfile(String jid) throws IOException,
			ClientException {
		Response response = null;
		log.debug("getProfile [{}] - Entering", jid);
		HandlerXmlProfile handler;
		try {

			String payload = String.format(
					"<profile platform='ps3' sv='%s'><jid>%s</jid></profile>",
					PS3_FIRMWARE_VERSION, jid);

			response = getOfficialResponse(
					"http://getprof.us.np.community.playstation.net/basic_view/func/get_profile",
					AGENT_PS3_COMMUNITY, payload);

			handler = new HandlerXmlProfile();
			parser.parse(response.getStream(), handler, response.getCharset());
			if (handler.getProfile() == null) return null;

			payload = String
					.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid></nptrophy>",
							PS3_FIRMWARE_VERSION, jid);

			response = getOfficialResponse(
					"http://trophy.ww.np.community.playstation.net/trophy/func/get_user_info",
					AGENT_PS3_COMMUNITY, payload);

			parser.parse(response.getStream(), handler, response.getCharset());
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getProfile - Exiting");
		}

		return handler.getProfile();
	}

	/**
	 * Retrieves game list for Jid. Good to do increments of 64 for min and max
	 * arguments. If no platform is given, PS3 will be used as default.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * http://trophy.ww.np.community.playstation.net/trophy/func/get_title_list
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param jid
	 *            jid
	 * @param start
	 *            start index. Must be one or greater.
	 * @param max
	 *            max games
	 * @param platforms
	 *            platforms for games
	 * @return official game list, empty if account has none
	 * @throws IllegalArgumentException
	 *             thrown if start index is zero or less
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClientException
	 *             if parser encountered an error or US login cookies invalid
	 * @throws PlaystationNetworkException
	 *             if jid is invalid
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public List<PsnGameOfficial> getOfficialGameList(String jid, int start,
			int max, Platform... platforms) throws IOException,
			ClientException, PlaystationNetworkException {
		Response response = null;
		HandlerXmlGame handler;
		log.debug("getOfficialGameList [{}, {}, {}, {}] - Entering", jid,
				String.valueOf(start), String.valueOf(max), platforms);

		try {
			if (start <= 0) throw new IllegalArgumentException(
					"start index must be greater than 0");

			// if (max > 64) log.warn("max index is greater than 64");

			String payload = String
					.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid><start>%s</start><max>%s</max>%s</nptrophy>",
							PS3_FIRMWARE_VERSION, jid, String.valueOf(start),
							String.valueOf(max), getPlatformString(platforms));

			response = getOfficialResponse(
					"http://trophy.ww.np.community.playstation.net/trophy/func/get_title_list",
					AGENT_PS3_APPLICATION, payload);

			handler = new HandlerXmlGame(jid);
			parser.parse(response.getStream(), handler, response.getCharset());
			if (handler.getResult().equals("05")) throw new PlaystationNetworkException(
					"Jid invalid");
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getOfficialGameList - Exiting");
		}

		return handler.getGames();

	}

	/**
	 * Retrieves official trophy list from jid and <i>Official</i> game Id.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * http://trophy.ww.np.community.playstation.net/trophy/func/get_trophies
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param jid
	 *            jid
	 * @param gameId
	 *            <i>Official</i> game id
	 * @return official trophy list or empty if none found for gameId
	 * @throws IllegalArgumentException
	 *             thrown if game id isn't in <i>Official</i> format
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClientException
	 *             if parser encountered an error or US login cookies invalid
	 * @throws PlaystationNetworkException
	 *             if jid is invalid
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public List<PsnTrophyOfficial> getOfficialTrophyList(String jid,
			String gameId) throws IOException, PlaystationNetworkException,
			ClientException {
		Response response = null;
		HandlerXmlTrophy handler;
		log.debug("getOfficialTrophyList [{}, {}] - Entering", jid, gameId);

		if (!PsnUtils.isValidGameId(gameId)) throw new IllegalArgumentException(
				"Must be a valid PsnGame Id");

		try {
			String payload = String
					.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid><list><info npcommid='%s'><target>FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF</target></info></list></nptrophy>",
							PS3_FIRMWARE_VERSION, jid, gameId);

			response = getOfficialResponse(
					"http://trophy.ww.np.community.playstation.net/trophy/func/get_trophies",
					AGENT_PS3_APPLICATION, payload);

			handler = new HandlerXmlTrophy(jid);
			parser.parse(response.getStream(), handler, response.getCharset());
			if (handler.getResult().equals("05")) throw new PlaystationNetworkException(
					"Jid invalid");
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getOfficialTrophyList - Exiting");
		}

		return handler.getTrophyList();

	}

	/**
	 * Retrieves official latest trophy list. Good to do increments of 64 for
	 * max argument. If no platform is given, PS3 will be used as default.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * http://trophy.ww.np.community.playstation.net/trophy/func/get_latest_trophies
	 * </pre>
	 * 
	 * </p>
	 * 
	 * 
	 * @param jid
	 *            jid
	 * @param max
	 *            max amount of trophies
	 * @param platforms
	 *            platforms for trophies
	 * @return official trophy list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws PlaystationNetworkException
	 *             if jid is invalid
	 * @throws ClientException
	 *             if parser encountered an error
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public List<PsnTrophyOfficial> getOfficialLatestTrophyList(String jid,
			int max, Platform... platforms) throws PlaystationNetworkException,
			IOException, ClientException {
		Response response = null;
		HandlerXmlTrophy handler;
		log.debug("getOfficialLatestTrophyList [{}, {}, {}] - Entering", jid,
				String.valueOf(max), platforms);

		// if (max > 64) log.warn("max index is greater than 64");

		try {
			String payload = String
					.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid><max>%s</max>%s</nptrophy>",
							PS3_FIRMWARE_VERSION, jid, String.valueOf(max),
							getPlatformString(platforms));

			response = getOfficialResponse(
					"http://trophy.ww.np.community.playstation.net/trophy/func/get_latest_trophies",
					AGENT_PS3_APPLICATION, payload);

			handler = new HandlerXmlTrophy(jid);
			parser.parse(response.getStream(), handler, response.getCharset());
			if (handler.getResult().equals("05")) throw new PlaystationNetworkException(
					"Jid invalid");
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getOfficialLatestTrophyList - Exiting");
		}

		return handler.getTrophyList();

	}

	/**
	 * Retrieves official trophy list from date. Good to do increments of 64 for
	 * max argument. If no platform is given, PS3 will be used as default.
	 * 
	 * <p>
	 * Connections made
	 * 
	 * <pre>
	 * http://trophy.ww.np.community.playstation.net/trophy/func/get_latest_trophies
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param jid
	 *            jid
	 * @param max
	 *            max amount of trophies
	 * @param since
	 *            date in format <code>yyyy-MM-dd'T'HH:mm:ss.SSSZ</code>
	 * @param platforms
	 *            platforms for trophies
	 * @return official trophy list since
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws PlaystationNetworkException
	 *             if jid is invalid
	 * @throws ClientException
	 *             if parser encountered an error
	 * @see PsnUtils#getOfficialDateFormat(java.util.Date)
	 * @since SEN-PSN 1.0
	 */
	@SuppressWarnings("resource")
	public List<PsnTrophyOfficial> getOfficialTrophyListSince(String jid,
			int max, String since, Platform... platforms) throws IOException,
			PlaystationNetworkException, ClientException {
		Response response = null;
		HandlerXmlTrophy handler;
		// if (max > 64) log.warn("max index is greater than 64");

		log.debug("getOfficialTrophyListSince [{}, {}, {}, {}] - Entering",
				jid, String.valueOf(max), since, platforms);

		try {
			String payload = String
					.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid><max>%s</max><since>%s</since>%s</nptrophy>",
							PS3_FIRMWARE_VERSION, jid, String.valueOf(max),
							since, getPlatformString(platforms));

			response = getOfficialResponse(
					"http://trophy.ww.np.community.playstation.net/trophy/func/get_latest_trophies",
					AGENT_PS3_APPLICATION, payload);

			handler = new HandlerXmlTrophy(jid);
			parser.parse(response.getStream(), handler, response.getCharset());
			if (handler.getResult().equals("05")) throw new PlaystationNetworkException(
					"jid invalid");
		} catch (ParseException e) {
			throw new ClientException(e);
		} finally {
			CommonUtils.closeQuietly(response);
			log.debug("getOfficialTrophyListSince - Exiting");
		}

		return handler.getTrophyList();
	}

	private Response getOfficialResponse(String url, String userAgent,
			String payload) throws IOException {
		Response response = new RequestBuilderAuthenticate(POST, new URL(url))
				.header("Content-Type", "text/xml; charset=UTF-8")
				.header("Accept-Encoding", "identity")
				.header("User-Agent", userAgent)
				.payload(payload.getBytes("UTF-8")).execute(networkHelper);

		if (response instanceof ResponseAuthenticate) {
			log.error("Unauthorized [{}]",
					((ResponseAuthenticate) response).getAuthentication());
			throw new IOException("Authentication required");
		}

		return response;
	}

	private String getPlatformString(Platform[] platforms) {
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
