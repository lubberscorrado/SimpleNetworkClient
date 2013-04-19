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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.krobothsoftware.commons.parse.HandlerHtml;
import com.krobothsoftware.commons.util.CommonUtils;
import com.krobothsoftware.snc.sen.psn.us.model.PsnGamerProfile;

/**
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN-US 1.0
 */
public class HandlerHtmlUserProfile extends HandlerHtml {
	private final PsnGamerProfile.Builder builder;
	private int type = -10;

	public HandlerHtmlUserProfile() {
		builder = new PsnGamerProfile.Builder();
	}

	public PsnGamerProfile getProfile() {
		return builder.build();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if (startTag.equals("img") && attributes.getLength() > 1) {
			builder.setAvatar(attributes.getValue("img"));
			builder.setPsnId(attributes.getValue("title"));
			type = 0;
		} else {
			type++;
		}

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		String str;
		if (calledStartElement) {
			switch (type) {
				case 6:
					builder.setLevel(Integer.parseInt(CommonUtils
							.trim(new String(ch, start, length))));
					break;
				case 10:
					// trims off percent sign
					str = new String(CommonUtils.trim(new String(ch, start,
							length)));
					str = str.substring(0, str.length() - 1);
					builder.setProgress(Integer.parseInt(str));
					break;
				case 12:
					builder.setBronze(Integer.parseInt(CommonUtils
							.trim(new String(ch, start, length))));
					break;
				case 15:
					builder.setSilver(Integer.parseInt(CommonUtils
							.trim(new String(ch, start, length))));
					break;
				case 18:
					builder.setGold(Integer.parseInt(CommonUtils
							.trim(new String(ch, start, length))));
					break;
				case 21:
					builder.setPlatinum(Integer.parseInt(CommonUtils
							.trim(new String(ch, start, length))));
					break;
			}
		}

		calledStartElement = false;
	}

}
