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

import com.krobothsoftware.commons.parse.sax.Expression;
import com.krobothsoftware.commons.parse.sax.ExpressionFilter;
import com.krobothsoftware.commons.parse.sax.HandlerSax;
import com.krobothsoftware.snc.sen.Platform;
import com.krobothsoftware.snc.sen.psn.PsnUtils;
import com.krobothsoftware.snc.sen.psn.model.PsnGame;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public final class HandlerHtmlUSGame extends HandlerSax implements
        ExpressionFilter {
    private static final Expression expr = Expression
            .parse("/div[@id='mainContentDiv']");
    private final List<PsnGame> list;
    private final PsnGame.Builder builder;
    private int type = -1;
    private int column = -1;

    public HandlerHtmlUSGame(String psnId) {
        list = new ArrayList<PsnGame>();
        builder = new PsnGame.Builder(psnId).setPlatform(Platform.UNKNOWN);
    }

    public List<PsnGame> getGames() {
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

        if (type == -1 && startTag.equals("img")) {
            column = 0;
            type = 0;
            // image, gameId, and title
            str = attributes.getValue("src");
            builder.setImage(str).setName(attributes.getValue("title"))
                    .setGameId(PsnUtils.getGameIdOf(str));
        } else if (column == 0 && type == 4) {
            // get title link id
            str = attributes.getValue("onclick");
            int index = str.indexOf("\",");
            builder.setTitleLinkId(str.substring(str.indexOf("(\"") + 2, index));
            builder.setName(str.substring(index + 3, str.indexOf("\")", index)));
            type++;
        } else if (column == 0 && startTag.equals("div")) {
            str = attributes.getValue("class");
            if (str != null && str.equals("secondColumn")) {
                column = 1;
                type = 0;
            }
        } else if (type != -1) {
            type++;
        }

    }

    @Override
    public void characters(int expr, char[] ch, int start, int length)
            throws SAXException {

        if (calledStartElement) {

            if (column == 1) {

                switch (type) {
                    case 3:
                        // bronze
                        builder.setBronze((Integer.parseInt(new String(ch,
                                start, length))));
                        break;
                    case 5:
                        // silver
                        builder.setSilver((Integer.parseInt(new String(ch,
                                start, length))));
                        break;
                    case 7:
                        // gold
                        builder.setGold((Integer.parseInt(new String(ch, start,
                                length))));
                        break;
                    case 9:
                        // platinum
                        builder.setPlatinum((Integer.parseInt(new String(ch,
                                start, length))));
                        break;
                    case 13:
                        // progress. Cuts off percent sign
                        builder.setProgress(Integer.parseInt(new String(ch,
                                start, length - 1)));
                        list.add(builder.build());
                        type = -1;
                        break;

                }

            }

        }
    }

    @Override
    public boolean endElement(int expr, String uri, String localName,
                              String qName) throws SAXException {
        return true;
    }

}
