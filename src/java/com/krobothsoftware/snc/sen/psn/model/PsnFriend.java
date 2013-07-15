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

import java.io.Serializable;

/**
 * Holds friend data used by <i>UK</i> methods.
 *
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public class PsnFriend implements OnlineId, Serializable {
    private static final long serialVersionUID = -2725063559049803762L;
    private final String psnId;
    private final FriendStatus status;
    private final String title;
    private final String avatar;
    private final String comment;
    private final boolean isPP;
    private final int level;
    private final int platinum;
    private final int gold;
    private final int silver;
    private final int bronze;

    PsnFriend(Builder builder) {
        psnId = builder.psnId;
        status = builder.status;
        title = builder.title;
        avatar = builder.avatar;
        comment = builder.comment;
        isPP = builder.isPP;
        level = builder.level;
        platinum = builder.platinum;
        gold = builder.gold;
        silver = builder.silver;
        bronze = builder.bronze;
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
     * Gets friend status.
     *
     * @return friend status
     * @since SEN-PSN 1.0
     */
    public FriendStatus getStatus() {
        return status;
    }

    /**
     * Gets title playing.
     *
     * @return current title
     * @since SEN-PSN 1.0
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets UK avatar link.
     * <p/>
     * <pre>
     * http://secure.eu.playstation.com (Avatar)
     * </pre>
     *
     * @return avatar link
     * @since SEN-PSN 1.0
     */
    public String getAvatar() {
        return "http://secure.eu.playstation.com" + avatar;
    }

    /**
     * Gets comment if online.
     *
     * @return comment
     * @since SEN-PSN 1.0
     */
    public String getComment() {
        return comment;
    }

    /**
     * Checks if friend is a PlayStation Plus member.
     *
     * @return is Playstation Plus
     * @since SEN-PSN 1.0
     */
    public boolean isPlayStationPlus() {
        return isPP;
    }

    /**
     * Gets trophy level.
     *
     * @return trophy level
     * @since SEN-PSN 1.0
     */
    public int getLevel() {
        return level;
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
     * Returns string in format
     * "PsnFriend [onlineId='psnId', status='status', title='title']"
     *
     * @since SEN-PSN 1.0
     */
    @Override
    public String toString() {
        return "PsnFriend [onlineId=" + psnId + ", status=" + status
                + ", title=" + title + "]";
    }

    /**
     * Builder for Friend.
     *
     * @author Kyle Kroboth
     * @since SEN-PSN 1.0
     */
    public static class Builder {
        String psnId;
        FriendStatus status;
        String title;
        String avatar;
        String comment;
        boolean isPP;
        int level;
        int platinum;
        int gold;
        int silver;
        int bronze;

        public Builder setPsnId(String psnId) {
            this.psnId = psnId;
            return this;
        }

        public Builder setStatus(FriendStatus presence) {
            this.status = presence;
            return this;
        }

        public Builder setGame(String game) {
            this.title = game;
            return this;
        }

        public Builder setAvatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder setPP(boolean isPP) {
            this.isPP = isPP;
            return this;
        }

        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder setPlatinum(int platinum) {
            this.platinum = platinum;
            return this;
        }

        public Builder setGold(int gold) {
            this.gold = gold;
            return this;
        }

        public Builder setSilver(int silver) {
            this.silver = silver;
            return this;
        }

        public Builder setBronze(int bronze) {
            this.bronze = bronze;
            return this;
        }

        public PsnFriend build() {
            return new PsnFriend(this);
        }

    }

}
