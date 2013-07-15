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
import com.krobothsoftware.commons.util.CommonUtils;
import com.krobothsoftware.snc.sen.psn.PsnUtils;
import com.krobothsoftware.snc.sen.psn.Trophy;
import com.krobothsoftware.snc.sen.psn.model.PsnTrophy;
import com.krobothsoftware.snc.sen.psn.model.PsnTrophy.Builder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public final class HandlerHtmlUSTrophy extends HandlerSax implements
        ExpressionFilter {
    private static final Expression expr = Expression
            .parse("/div[@class='content_box']/div[@class='topRow']");
    private final List<PsnTrophy> list;
    private final PsnTrophy.Builder builder;
    private int type = -10;
    private int trophy;

    public HandlerHtmlUSTrophy(String psnId, String gameId) {
        list = new ArrayList<PsnTrophy>();
        builder = (Builder) new PsnTrophy.Builder(psnId).setGameId(gameId);
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
        super.startElement(uri, localName, qName, attributes);

        String str;
        if (startTag.equals("img")) {
            str = attributes.getValue("src");
            builder.setImage(str);
            if (!str.endsWith("locksmall.png")) {
                builder.setReceieved(true);
                builder.setGameId(PsnUtils.getGameIdOf(str));
            } else {
                builder.setReceieved(false);
            }
            type = 0;
        } else if (type == 1) {
            str = attributes.getValue("id");
            Trophy trophyType = null;
            if (str.equals("bronzetrop")) trophyType = Trophy.BRONZE;
            else if (str.equals("silvertrop")) trophyType = Trophy.SILVER;
            else if (str.equals("goldtrop")) trophyType = Trophy.GOLD;
            else if (str.equals("platinumtrop")) trophyType = Trophy.PLATINUM;
            else {
                trophyType = Trophy.HIDDEN;
                builder.setName("???").setDescription(null);
                // skip switch
                type = 6;
                list.add(builder.setIndex(++trophy).build());
            }
            builder.setType(trophyType);
            type++;
        } else {
            type++;
        }

    }

    @Override
    public void characters(int expr, char[] ch, int start, int length)
            throws SAXException {
        if (calledStartElement) {
            switch (type) {
                case 4:
                    builder.setName(CommonUtils.trim(new String(ch, start,
                            length)));
                    break;
                case 5:
                    builder.setDescription(CommonUtils.trim(new String(ch,
                            start, length)));
                    list.add(builder.setIndex(++trophy).build());
                    break;
            }
        }

        calledStartElement = false;
    }

    @Override
    public boolean endElement(int expr, String uri, String localName,
                              String qName) throws SAXException {
        return true;
    }
}
