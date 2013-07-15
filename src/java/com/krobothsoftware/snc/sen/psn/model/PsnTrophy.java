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

import com.krobothsoftware.snc.sen.OnlineId;
import com.krobothsoftware.snc.sen.psn.Trophy;

import java.io.Serializable;

/**
 * Holds trophy data used by <i>UK</i> and <i>US</i> methods.
 *
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public class PsnTrophy implements OnlineId, Serializable {
    private static final long serialVersionUID = -2664313320588242916L;
    private final String psnId;
    private final int index;
    private final String gameId;
    private final String name;
    private final String image;
    private final String description;
    private final String dateEarned;
    private final Trophy type;
    private final boolean isReceived;

    PsnTrophy(Builder builder) {
        psnId = builder.userId;
        index = builder.index;
        gameId = builder.gameId;
        name = builder.name;
        image = builder.image;
        description = builder.description;
        dateEarned = builder.dateEarned;
        type = builder.type;
        isReceived = builder.received;
    }

    /**
     * Returns PsnId.
     *
     * @since SEN-PSN 1.0
     */
    @Override
    public String getOnlineId() {
        return psnId;
    }

    /**
     * Gets name of trophy. May return "???" if is hidden.
     *
     * @return trophy name
     * @since SEN-PSN 1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Gets trophy image url.
     *
     * @return image url
     * @since SEN-PSN 1.0
     */
    public String getImage() {
        return image;
    }

    /**
     * Gets trophy description text.
     *
     * @return description text
     * @since SEN-PSN 1.0
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets game Id of trophy.
     *
     * @return game Id
     * @since SEN-PSN 1.0
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * Gets index of trophy in game.
     *
     * @return index of trophy
     * @since SEN-PSN 1.0
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets date earned of trophy.
     *
     * @return date earned, or null if haven't gotten
     * @since SEN-PSN 1.0
     */
    public String getDateEarned() {
        return dateEarned;
    }

    /**
     * Gets trophy type.
     *
     * @return trophy type
     * @since SEN-PSN 1.0
     */
    public Trophy getType() {
        return type;
    }

    /**
     * Checks if trophy has been received.
     *
     * @return has received trophy
     * @since SEN-PSN 1.0
     */
    public boolean isReceived() {
        return isReceived;
    }

    /**
     * Returns string in format "PsnTrophy [trophyId='index', name='name',
     * dateEarned='earned', type='type', received='received']".
     *
     * @since SEN-PSN 1.0
     */
    @Override
    public String toString() {
        return String
                .format("PsnTrophy [trophyId=%s, name=%s, earned=%s, type=%s, received=%s]",
                        String.valueOf(index), name, dateEarned, type,
                        String.valueOf(isReceived));
    }

    /**
     * Builder for trophies.
     *
     * @author Kyle Kroboth
     * @since SEN-PSN 1.0
     */
    public static class Builder extends BuilderTrophy<PsnTrophy> {
        String name;
        String image;
        String description;

        public Builder(String userId) {
            super(userId);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setImage(String image) {
            this.image = image;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        @Override
        public PsnTrophy build() {
            return new PsnTrophy(this);
        }

    }

}
