/* ===================================================
 * Copyright 2012 Kroboth Software
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

import static com.krobothsoftware.snc.sen.psn.Trophy.BRONZE;
import static com.krobothsoftware.snc.sen.psn.Trophy.GOLD;
import static com.krobothsoftware.snc.sen.psn.Trophy.HIDDEN;
import static com.krobothsoftware.snc.sen.psn.Trophy.PLATINUM;
import static com.krobothsoftware.snc.sen.psn.Trophy.SILVER;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.krobothsoftware.commons.parse.Expression;
import com.krobothsoftware.commons.parse.ExpressionFilter;
import com.krobothsoftware.commons.parse.HandlerHtml;
import com.krobothsoftware.commons.parse.StopException;
import com.krobothsoftware.commons.util.CommonUtils;
import com.krobothsoftware.snc.sen.psn.PsnUtils;
import com.krobothsoftware.snc.sen.psn.Trophy;
import com.krobothsoftware.snc.sen.psn.model.PsnTrophy;

/**
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public final class HandlerHtmlUKTrophy extends HandlerHtml implements
		ExpressionFilter {
	private static final Expression expr = Expression
			.parse("/div[@class='gameLogoImage']/img/&&/div[@class='gamelevelListingContainer']/div[2]");
	private final List<PsnTrophy> list;
	private final PsnTrophy.Builder builder;
	private boolean cont;
	private int type;
	private int trophy;

	public HandlerHtmlUKTrophy(final String psnId) {
		list = new ArrayList<PsnTrophy>();
		builder = new PsnTrophy.Builder(psnId);
	}

	public List<PsnTrophy> getTrophyList() {
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

		if (expr == 1) {
			if (startTag.equals("img")) {
				if (type++ == 0) {
					// image
					str = attributes.getValue("src");
					if (str.endsWith("icon_trophy_padlock.gif")) str = "http://webassets.scea.com/playstation/img/trophy_locksmall.png";
					else
						str = "http://trophy01.np.community.playstation.net/trophy/np/"
								+ str.substring(str.indexOf("/trophy/np/") + 11);
					builder.setImage(str);
				} else {
					// trophy type
					str = attributes.getValue("alt");
					Trophy trophyType = null;
					if (str.equals("Bronze")) trophyType = BRONZE;
					else if (str.equals("Silver")) trophyType = SILVER;
					else if (str.equals("Gold")) trophyType = GOLD;
					else if (str.equals("Platinum")) trophyType = PLATINUM;
					else if (str.equals("Hidden")) trophyType = HIDDEN;
					builder.setType(trophyType);
				}
			} else if (startTag.equals("p")) {
				str = attributes.getValue("class");
				if (str == null) type = 4;
				else if (str.equals("title")) type = 2;
				else if (str.equals("date")) type = 3;
			} else if (type == 0
					&& attributes.getValue("class").equals("sortBarHatchedBtm")) throw new StopException();

		} else {
			// game image
			builder.setGameId(PsnUtils.getGameIdOf(attributes.getValue("src")));
			cont = true;
		}

	}

	@Override
	public boolean endElement(int expr, String uri, String localName,
			String qName) throws SAXException {
		return cont;
	}

	@Override
	public void characters(int expr, char[] ch, int start, int length)
			throws SAXException {

		String str;

		if (calledStartElement) {
			if (expr == 1) {
				switch (type) {
					case 2:
						// title
						builder.setName(CommonUtils.trim(new String(ch, start,
								length)));
						break;
					case 3:
						// date
						str = new String(ch, start, length);
						builder.setDateEarned(str.substring(9));
						break;
					case 4:
						// description
						list.add(builder
								.setDescription(
										CommonUtils.trim(new String(ch, start,
												length))).setIndex(++trophy)
								.build());
						builder.setDateEarned(null);
						type = 0;
						break;
				}
			}

		}

		calledStartElement = false;
	}
}
