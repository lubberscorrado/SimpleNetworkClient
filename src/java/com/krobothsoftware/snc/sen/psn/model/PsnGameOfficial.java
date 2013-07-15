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

package com.krobothsoftware.snc.sen.psn.model;

import com.krobothsoftware.snc.sen.Platform;
import com.krobothsoftware.snc.sen.psn.Jid;

import java.io.Serializable;

/**
 * Holds game data for <i>Official</i> methods.
 *
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public class PsnGameOfficial implements Jid, Serializable {
    private static final long serialVersionUID = -2913100475832848476L;
    private final String jid;
    private final String npCommid;
    private final Platform pf;
    private final int platinum;
    private final int gold;
    private final int silver;
    private final int bronze;
    private final String lastUpdated;

    PsnGameOfficial(Builder builder) {
        jid = builder.userId;
        npCommid = builder.gameId;
        pf = builder.pf;
        platinum = builder.platinum;
        gold = builder.gold;
        silver = builder.silver;
        bronze = builder.bronze;
        lastUpdated = builder.lastUpdated;
    }

    /**
     * @since SEN-PSN 1.0
     */
    @Override
    public String getJid() {
        return jid;
    }

    /**
     * Gets official game Id.
     *
     * @return game Id
     * @since SEN-PSN 1.0
     */
    public String getGameId() {
        return npCommid;
    }

    /**
     * Gets game platform.
     *
     * @return game platform
     */
    public Platform getPlatform() {
        return pf;
    }

    /**
     * Gets platinum trophy count.
     *
     * @return platinum count
     * @since SEN-PSN 1.0
     */
    public int getPlatinum() {
        return platinum;
    }

    /**
     * Gets gold trophy count.
     *
     * @return gold count
     * @since SEN-PSN 1.0
     */
    public int getGold() {
        return gold;
    }

    /**
     * Gets silver trophy count.
     *
     * @return silver count
     * @since SEN-PSN 1.0
     */
    public int getSilver() {
        return silver;
    }

    /**
     * Gets bronze trophy count.
     *
     * @return bronze count
     * @since SEN-PSN 1.0
     */
    public int getBronze() {
        return bronze;
    }

    /**
     * Gets last updated or when trophy was received.
     *
     * @return last updated
     * @see com.krobothsoftware.snc.sen.psn.PsnUtils#getOfficialDate(String,
     *      java.util.Locale)
     * @since SEN-PSN 1.0
     */
    public String getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Returns string in format "PsnGameOfficial [npCommid='nmCommid',
     * platinum='platinum', gold='gold', silver='silver', bronze='bronze',
     * lastUpdated='lastupdated']" .
     *
     * @since SEN-PSN 1.0
     */
    @Override
    public String toString() {
        return String
                .format("PsnGameOfficial [npCommid=%s, platinum=%s, gold=%s, silver=%s, bronze=%s, lastUpdated=%s]",
                        npCommid, String.valueOf(platinum),
                        String.valueOf(gold), String.valueOf(silver),
                        String.valueOf(bronze), lastUpdated);
    }

    /**
     * Builder for official games.
     *
     * @author Kyle Kroboth
     * @since SEN-PSN 1.0
     */
    public static class Builder extends BuilderGame<PsnGameOfficial> {
        String lastUpdated;
        Platform pf;

        public Builder(String userId) {
            super(userId);

        }

        public Builder setPlatform(Platform platform) {
            pf = platform;
            return this;
        }

        public Builder setLastUpdated(String lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        @Override
        public PsnGameOfficial build() {
            return new PsnGameOfficial(this);
        }

    }

}
