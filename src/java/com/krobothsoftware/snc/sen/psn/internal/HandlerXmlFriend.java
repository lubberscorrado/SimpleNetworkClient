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

package com.krobothsoftware.snc.sen.psn.internal;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import com.krobothsoftware.commons.parse.HandlerXml;
import com.krobothsoftware.snc.sen.psn.model.FriendStatus;
import com.krobothsoftware.snc.sen.psn.model.PsnFriend;

/**
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public final class HandlerXmlFriend extends HandlerXml {
	private final List<PsnFriend> list;
	private final PsnFriend.Builder builder;

	public HandlerXmlFriend() {
		list = new ArrayList<PsnFriend>();
		builder = new PsnFriend.Builder();
	}

	public List<PsnFriend> getFriendList() {
		return list;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);

		if (endTag.equalsIgnoreCase("psn_friend")) list.add(builder.build());

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		String str;

		if (calledStartElement) {
			if (startTag.equalsIgnoreCase("onlineid")) builder
					.setPsnId(new String(ch, start, length));
			else if (startTag.equalsIgnoreCase("current_presence")) builder
					.setStatus(getOnlineStatus(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase("current_game")) {
				str = new String(ch, start, length);
				builder.setGame(str.equals("null") ? null : str);
			} else if (startTag.equalsIgnoreCase("current_avatar")) builder
					.setAvatar(new String(ch, start, length));
			else if (startTag.equalsIgnoreCase("comment")) {
				str = new String(ch, start, length);
				builder.setComment(str.equals("null") ? null : str);
			} else if (startTag.equalsIgnoreCase("playstationplus")) builder
					.setPP(Boolean.parseBoolean(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase("level")) builder
					.setLevel(Integer.parseInt(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase("platinum")) builder
					.setPlatinum(Integer
							.parseInt(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase("gold")) builder.setGold(Integer
					.parseInt(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase("silver")) builder
					.setSilver(Integer.parseInt(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase("bronze")) builder
					.setBronze(Integer.parseInt(new String(ch, start, length)));
		}

		calledStartElement = false;

	}

	private FriendStatus getOnlineStatus(String currentPresence) {
		if (currentPresence.equalsIgnoreCase("offline")) return FriendStatus.OFFLINE;
		else if (currentPresence.equalsIgnoreCase("online")
				|| currentPresence.equalsIgnoreCase("online-ingame")) return FriendStatus.ONLINE;
		else if (currentPresence.equalsIgnoreCase("online-away")
				|| currentPresence.equalsIgnoreCase("online-ingame-away")) return FriendStatus.AWAY;

		return FriendStatus.OFFLINE;
	}

}
