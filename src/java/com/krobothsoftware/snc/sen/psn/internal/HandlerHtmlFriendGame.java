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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.krobothsoftware.commons.parse.Expression;
import com.krobothsoftware.commons.parse.ExpressionFilter;
import com.krobothsoftware.commons.parse.HandlerHtml;
import com.krobothsoftware.commons.parse.StopException;
import com.krobothsoftware.snc.sen.psn.PsnUtils;
import com.krobothsoftware.snc.sen.psn.model.PsnGame;

/**
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public final class HandlerHtmlFriendGame extends HandlerHtml implements
		ExpressionFilter {
	private static final Expression expr = Expression
			.parse("/table[@class='psnTrophyTable']/tbody");
	private final List<PsnGame> list;
	private final PsnGame.Builder builder;
	private int td = -1;
	private int tr = -1;
	private int div = -1;
	boolean friend = true;

	public HandlerHtmlFriendGame(String psnId) {
		list = new ArrayList<PsnGame>();
		builder = new PsnGame.Builder(psnId);
	}

	public List<PsnGame> getGameList() {
		return list;
	}

	@Override
	public Expression getExpression() {
		return expr;
	}

	@Override
	public void startElement(int expr, String uri, String localName,
			String qName, Attributes attributes) throws SAXException {

		String str;

		if (startTag.equals("td")) {
			td++;
			if (friend && td == 4 && tr == 1
					&& attributes.getIndex("class") != -1) {
				// td's are not friends
				friend = false;
				td -= 6;
			}
		} else if (startTag.equals("tr")) tr++;
		switch (td) {
			case 0:
				if (startTag.equals("a")) {
					// image
					builder.setTitleLinkId((str = attributes.getValue("href"))
							.substring(str.indexOf("?title=") + 7,
									str.indexOf("&")));
				} else if (startTag.equals("img")) {
					builder.setName(attributes.getValue("alt"))
							.setImage(
									"http://trophy01.np.community.playstation.net/trophy/np/"
											+ (str = attributes.getValue("src")).substring(str
													.indexOf("/trophy/np/") + 11))
							.setGameId(PsnUtils.getGameIdOf(str));
				}
				break;
			case 1:
				if (startTag.equals("div")) div++;
				break;
		}

	}

	@Override
	public boolean endElement(int expr, String uri, String localName,
			String qName) throws SAXException {
		if (!friend && tr == 1 && endTag.equals("tr")) friend = true;
		else if (td == -1 && endTag.equals("table")) throw new StopException();

		return true;
	}

	@Override
	public void characters(int expr, char[] ch, int start, int length)
			throws SAXException {

		if (calledStartElement) {
			switch (td) {
				case 1:
					// get platform
					if (div == 3) {
						builder.setPlatform(HandlerHtmlUKGame
								.getPlatform(new String(ch, start, length)));
						div = -1;
					}
					break;
				case 7:
					builder.setBronze(Integer.parseInt(new String(ch, start,
							length)));
					break;
				case 8:
					builder.setSilver(Integer.parseInt(new String(ch, start,
							length)));
					break;
				case 9:
					builder.setGold(Integer.parseInt(new String(ch, start,
							length)));
					break;
				case 10:
					builder.setPlatinum(Integer.parseInt(new String(ch, start,
							length)));
					break;
				case 13:
					String str = new String(ch, start, length);
					list.add(builder
							.setProgress(
									Integer.parseInt(str.substring(0,
											str.length() - 1))).build());
					td = -1;
					tr = -1;
					break;

			}
		}

		calledStartElement = false;

	}

}
