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

package com.krobothsoftware.snc.sen.psn.internal;

import com.krobothsoftware.commons.parse.sax.HandlerSax;
import com.krobothsoftware.snc.sen.psn.model.PsnProfile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public final class HandlerXmlProfile extends HandlerSax {
    private static final int DEFAULT_COLOR = 0x989898;

    private String id;
    private int backgroundColor;

    private final PsnProfile.Builder builder;

    public HandlerXmlProfile() {
        builder = new PsnProfile.Builder();
    }

    public PsnProfile getProfile() {
        if (id == null) return null;
        if (backgroundColor == 0) builder.setBackgroundColor(DEFAULT_COLOR);

        return builder.setBackgroundColor(backgroundColor).setJid(id).build();

    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if (startTag.equalsIgnoreCase("level")) {
            builder.setLevelFloor(Integer.parseInt(attributes.getValue("base")));
            builder.setLevelCeiling(Integer.parseInt(attributes
                    .getValue("next")));
            builder.setProgress(Integer.parseInt(attributes
                    .getValue("progress")));
        } else if (startTag.equalsIgnoreCase("types")) {
            builder.setPlatinum(Integer.parseInt(attributes
                    .getValue("platinum")));
            builder.setGold(Integer.parseInt(attributes.getValue("gold")));
            builder.setSilver(Integer.parseInt(attributes.getValue("silver")));
            builder.setBronze(Integer.parseInt(attributes.getValue("bronze")));
        } else if (startTag.equalsIgnoreCase("panelurl")) {
            // get index (0, 5) to cut off alpha value
            builder.setPanelBackgroundColor(Integer.parseInt(attributes
                    .getValue("bgc").substring(0, 5), 16));
        }

    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        String str;

        if (calledStartElement) {
            if (startTag.equalsIgnoreCase("onlinename")) id = new String(ch,
                    start, length);
            else if (startTag.equalsIgnoreCase("avatarurl")) builder
                    .setAvatar(new String(ch, start, length));
            else if (startTag.equalsIgnoreCase("aboutme")) builder
                    .setAboutMe(new String(ch, start, length));
            else if (startTag.equalsIgnoreCase("country")) builder
                    .setCountry(new String(ch, start, length).toUpperCase());
            else if (startTag.equalsIgnoreCase("plusicon")) builder
                    .setPP(new String(ch, start, length).equals("0") ? false
                            : true);
            else if (startTag.equals("ucbgp")) {
                str = new String(ch, start, length);
                str = str.substring(8, str.length() - 2);
                backgroundColor = Integer.parseInt(str, 16);
            } else if (startTag.equalsIgnoreCase("point")) builder
                    .setPoints(Integer.parseInt(new String(ch, start, length)));
            else if (startTag.equalsIgnoreCase("level")) builder
                    .setLevel(Integer.parseInt(new String(ch, start, length)));
            else if (startTag.equalsIgnoreCase("panelurl")) builder
                    .setPanel(new String(ch, start, length));

        }

        calledStartElement = false;

    }
}
