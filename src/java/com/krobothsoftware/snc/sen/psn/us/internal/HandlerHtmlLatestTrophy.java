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

package com.krobothsoftware.snc.sen.psn.us.internal;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.krobothsoftware.commons.parse.HandlerHtml;
import com.krobothsoftware.commons.util.CommonUtils;
import com.krobothsoftware.snc.sen.psn.PsnUtils;
import com.krobothsoftware.snc.sen.psn.Trophy;
import com.krobothsoftware.snc.sen.psn.model.PsnTrophy;
import com.krobothsoftware.snc.sen.psn.model.PsnTrophy.Builder;

/**
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN-US 1.0
 */
public class HandlerHtmlLatestTrophy extends HandlerHtml {
	private final List<PsnTrophy> list;
	private final PsnTrophy.Builder builder;
	private int type = -10;

	public HandlerHtmlLatestTrophy(String psnId) {
		list = new ArrayList<PsnTrophy>(5);
		builder = (Builder) new PsnTrophy.Builder(psnId).setReceieved(true);
	}

	public List<PsnTrophy> getTrophyList() {
		return list;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		String str;
		if (startTag.equals("img")) {
			str = attributes.getValue("src");
			builder.setImage(str);
			builder.setGameId(PsnUtils.getGameIdOf(str));
			type = 0;
		} else if (type == 1) {
			str = attributes.getValue("id");
			Trophy trophyType = null;
			if (str.equals("bronzetrop")) trophyType = Trophy.BRONZE;
			else if (str.equals("silvertrop")) trophyType = Trophy.SILVER;
			else if (str.equals("goldtrop")) trophyType = Trophy.GOLD;
			else if (str.equals("platinumtrop")) trophyType = Trophy.PLATINUM;
			builder.setType(trophyType);
			type++;
		} else {
			type++;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (calledStartElement) {
			switch (type) {
				case 4:
					builder.setName(new String(ch, start, length));
					break;
				case 5:
					builder.setDescription(CommonUtils.trim(new String(ch,
							start, length)));
					list.add(builder.build());
					break;
			}
		}

		calledStartElement = false;
	}

}
