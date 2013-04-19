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
import java.util.Collections;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.krobothsoftware.commons.parse.HandlerXml;
import com.krobothsoftware.snc.sen.Platform;
import com.krobothsoftware.snc.sen.psn.model.PsnGameOfficial;

/**
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public final class HandlerXmlGame extends HandlerXml {
	private String result;
	private int numGames;

	private List<PsnGameOfficial> list;
	private final PsnGameOfficial.Builder builder;

	public HandlerXmlGame(String jid) {
		builder = new PsnGameOfficial.Builder(jid);
	}

	public List<PsnGameOfficial> getGames() {
		return list != null ? list : Collections.<PsnGameOfficial> emptyList();
	}

	public String getResult() {
		return result;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if (startTag.equalsIgnoreCase("nptrophy")) {
			result = attributes.getValue("result");
		} else if (startTag.equalsIgnoreCase("info")) {
			builder.setGameId(attributes.getValue("npcommid"));
			builder.setPlatform(Platform.getPlatform(attributes.getValue("pf")));
		} else if (startTag.equalsIgnoreCase("types")) {
			builder.setPlatinum(Integer.parseInt(attributes
					.getValue("platinum")));
			builder.setGold(Integer.parseInt(attributes.getValue("gold")));
			builder.setSilver(Integer.parseInt(attributes.getValue("silver")));
			builder.setBronze(Integer.parseInt(attributes.getValue("bronze")));
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (qLocal(qName, localName).equalsIgnoreCase("info")) {

			if (list == null) list = new ArrayList<PsnGameOfficial>(numGames);
			list.add(builder.build());
		}

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (calledStartElement) {
			if (startTag.equalsIgnoreCase("title")) numGames = Integer
					.parseInt(new String(ch, start, length));
			else if (startTag.equalsIgnoreCase("last-updated")) builder
					.setLastUpdated(new String(ch, start, length));
		}

		calledStartElement = false;

	}

}
