/* ===================================================
 * Copyright 2013 Kroboth Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================== 
 */

package com.krobothsoftware.snc.sen.psn;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import com.krobothsoftware.commons.network.value.Cookie;
import com.krobothsoftware.commons.network.value.Cookie.Builder;
import com.krobothsoftware.commons.util.Base64;
import com.krobothsoftware.commons.util.ThreadSafeDateUtil;

/**
 * Utils for creating cookies, checking Ids, and other psn related methods.
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public final class PsnUtils {
	private static Pattern JID_MATCHES;

	/**
	 * Date format for official methods and US sites.
	 * 
	 * <pre>
	 * yyyy-MM-dd'T'HH:mm:ss'Z'
	 * </pre>
	 * 
	 * @since SEN-PSN 1.0
	 */
	public static final String OFFICIAL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	/**
	 * Locale.US week data. First day of week and minimum days in week.
	 */
	private static final int[] OFFICIAL_WEEK_DATA = { 1, 1 };

	private PsnUtils() {

	}

	/**
	 * Generate <code>TICKET</code> cookie for US site.
	 * 
	 * @param psnId
	 *            psn id
	 * @return TICKET cookie
	 * @since SEN-PSN 1.0
	 */
	public static Cookie createCookieTicket(String psnId) {
		psnId = rightPad(Base64.encodeToString(psnId.getBytes(), false)
				.replaceAll("=", ""), 40, 'A');
		StringBuilder sb = new StringBuilder();
		sb.append(
				"MQAAAAAAAQcwAAC7AAgAFMAzj73%2FkeHsYc7s%2F2mIW0yw8KbaAAEABAAAAQAABwAIAAABOjiuhlEA%0ABwAIAAABOj3U36AAAgAIMWuXSWuxj08ABAAg")
				.append(psnId)
				.append("%0AAAAACAAEdXMAAQAEAARiNgAAAAgAGFVQOTAwMi1OUFdBMDAwMzVfMDAAAAAAADARAAQHwgwfAAEA%0ABBkAAgAwEAAPBlNUUkhXSwAAATPOWLloAAAAADACAEQACAAE2%2B8LsgAIADgwNgIZAOCdX0tizuh0%0AaKpHe%2BtLap6jQMmNHw4pnAIZAJnz9fvKhnuM9uUbR5MHrq3i4ALOvWlUoA%3D%3D");

		return new Builder().setName("TICKET").setDomain(".playstation.com")
				.setValue(sb.toString()).setMaxAge(-1).build();

	}

	/**
	 * Generate <code>PSNS2STICKET</code> Cookie for US site.
	 * 
	 * @param psnId
	 *            psn id
	 * @return PSNS2TICKET Cookie
	 * @since SEN-PSN 1.0
	 */
	public static Cookie createCookiePsnTicket(String psnId) {
		psnId = rightPad(Base64.encodeToString(psnId.getBytes(), false)
				.replaceAll("=", ""), 40, 'A');
		StringBuilder sb = new StringBuilder();
		sb.append(
				"MQAAAAAAAQcwAAC7AAgAFMAzj73%2FkeHsYc7s%2F2mIW0yw8KbaAAEABAAAAQAABwAIAAABOjiuhlEA%0ABwAIAAABOj3U36AAAgAIMWuXSWuxj08ABAAg")
				.append(psnId)
				.append("%0AAAAACAAEdXMAAQAEAARiNgAAAAgAGElWMDAwMS1OUFhTMDEwMDRfMDAAAAAAADARAAQHwgwfAAEA%0ABBkAAgAwEAAAAAAAADACAEQACAAEyS7rGwAIADgwNQIYC0htxjeTFvBo7nPpSPJCwAWjRtzfVa5f%0AAhkAwjEzsDCC0XZBjPz%2FKko5ogByHFzFXnx%2FAA%3D%3D");

		return new Builder().setName("PSNS2STICKET")
				.setDomain(".playstation.com").setValue(sb.toString())
				.setMaxAge(-1).build();

	}

	/**
	 * Extract psn id from jid.
	 * 
	 * @param jid
	 * @return psnId
	 * @since SEN-PSN 1.0
	 */
	public static String getPsnIdFromJid(String jid) {
		if (jid == null) return null;
		else if (isValidJid(jid)) return jid.substring(0, jid.indexOf("@"));
		return jid;
	}

	/**
	 * Checks if JID is valid.
	 * 
	 * @param jid
	 * @return true, if is valid
	 * @since SEN-PSN 1.0
	 */
	public static boolean isValidJid(String jid) {
		if (JID_MATCHES == null) JID_MATCHES = Pattern.compile("(\\S+@\\S+)");
		return JID_MATCHES.matcher(jid).matches();
	}

	/**
	 * Checks if game Id is <i>official</i>.
	 * 
	 * @param gameId
	 * @return true, if is valid game id
	 * @since SEN-PSN 1.0
	 */
	public static boolean isValidGameId(String gameId) {
		return gameId.length() == 12 && gameId.endsWith("_00");
	}

	/**
	 * Extracts game Id of valid trophy image link.
	 * 
	 * @param trophyImageLink
	 * 
	 * @return official game Id
	 * @since SEN-PSN 1.0
	 */
	public static String getGameIdOf(String trophyImageLink) {
		int index = trophyImageLink.indexOf("/trophy/np/") + 11;
		return trophyImageLink.substring(index, index + 12);
	}

	/**
	 * Gets the official date format used by <i>Official</i> methods.
	 * 
	 * <p>
	 * Uses {@link ThreadSafeDateUtil} as of <i>SEN-PSN 1.0.2</i>.
	 * </p>
	 * 
	 * @param date
	 * @return official date format
	 * @see #OFFICIAL_DATE_FORMAT
	 * @since SEN-PSN 1.0
	 */
	public static String getOfficialDateFormat(Date date) {
		return ThreadSafeDateUtil.format(OFFICIAL_DATE_FORMAT, date,
				OFFICIAL_WEEK_DATA);
	}

	/**
	 * Gets the official date format used by <i>Official</i> methods.
	 * 
	 * <p>
	 * Uses {@link ThreadSafeDateUtil} as of <i>SEN-PSN 1.0.2</i>.
	 * {@link ThreadSafeDateUtil#getWeekData(Locale)} retrieves week data for
	 * locale.
	 * </p>
	 * 
	 * @param date
	 *            official date string
	 * @param locale
	 *            locale for <code>Date</code>
	 * @return parsed <code>Date</code>
	 * @throws ParseException
	 *             if the beginning of the specified string cannot be parsed.
	 * @see #OFFICIAL_DATE_FORMAT
	 * @since SEN-PSN 1.0
	 */
	public static Date getOfficialDate(String date, Locale locale)
			throws ParseException {
		return ThreadSafeDateUtil.parse(OFFICIAL_DATE_FORMAT, date,
				ThreadSafeDateUtil.getWeekData(locale));
	}

	private static String rightPad(String str, int size, char padChar) {
		int pads = size - str.length();
		StringBuilder builder = new StringBuilder(str);

		for (int i = 0; i < pads; i++) {
			builder.append(padChar);
		}

		return builder.toString();
	}
}
