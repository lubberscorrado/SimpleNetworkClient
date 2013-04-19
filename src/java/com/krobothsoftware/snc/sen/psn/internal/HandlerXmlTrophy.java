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

import static com.krobothsoftware.snc.sen.psn.Trophy.BRONZE;
import static com.krobothsoftware.snc.sen.psn.Trophy.GOLD;
import static com.krobothsoftware.snc.sen.psn.Trophy.PLATINUM;
import static com.krobothsoftware.snc.sen.psn.Trophy.SILVER;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.krobothsoftware.commons.parse.HandlerXml;
import com.krobothsoftware.snc.sen.Platform;
import com.krobothsoftware.snc.sen.psn.model.PsnTrophyOfficial;

/**
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public final class HandlerXmlTrophy extends HandlerXml {
	private static final String NPCOMMID = "npcommid";
	private static final String PF = "pf";
	private final List<PsnTrophyOfficial> list;
	private final PsnTrophyOfficial.Builder builder;

	private String result;

	public HandlerXmlTrophy(String psnId) {
		list = new ArrayList<PsnTrophyOfficial>();
		builder = new PsnTrophyOfficial.Builder(psnId);
	}

	public List<PsnTrophyOfficial> getTrophyList() {
		return list;
	}

	public String getResult() {
		return result;
	}

	@Override
	public void startElement( String uri, String localName,
			String qName, Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if (startTag.equalsIgnoreCase("nptrophy")) {
			result = attributes.getValue("result");
		} else if (startTag.equalsIgnoreCase("info")) {
			builder.setGameId(attributes.getValue(NPCOMMID));
			builder.setPlatform(Platform.getPlatform(attributes
					.getValue(PF)));
		} else if (startTag.equalsIgnoreCase("trophy")) {
			builder.setIndex(Integer.parseInt(attributes.getValue("id")));
			if (attributes.getIndex(NPCOMMID) != -1) builder
					.setGameId(attributes.getValue(NPCOMMID));
			switch (Integer.parseInt(attributes.getValue("type"))) {
				case 0:
					builder.setType(BRONZE);
					break;
				case 1:
					builder.setType(SILVER);
					break;
				case 2:
					builder.setType(GOLD);
					break;
				case 3:
					builder.setType(PLATINUM);
					break;
			}
			if (attributes.getIndex(PF) != -1) builder.setPlatform(Platform
					.getPlatform(attributes.getValue(PF)));

		}
	}

	@Override
	public void endElement(String uri, String localName,
			String qName) throws SAXException {

		if (qLocal(qName, localName).equalsIgnoreCase("trophy")) list
				.add(builder.build());
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (calledStartElement) {
			if (startTag.equalsIgnoreCase("trophy")) builder
					.setDateEarned(new String(ch, start, length));
		}

		calledStartElement = false;
	}

}
