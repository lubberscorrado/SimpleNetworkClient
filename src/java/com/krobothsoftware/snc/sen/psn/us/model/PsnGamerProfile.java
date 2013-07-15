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

package com.krobothsoftware.snc.sen.psn.us.model;

import com.krobothsoftware.snc.sen.OnlineId;

import java.io.Serializable;

/**
 * Holds gamer profile info for <i>US</i> methods.
 *
 * @author Kyle Kroboth
 * @since SEN-PSN-US 1.0
 */
public class PsnGamerProfile implements OnlineId, Serializable {
    private static final long serialVersionUID = 647667938575958088L;
    final String psnId;
    final String aboutMe;
    final String avatar;
    final int bronze;
    final int silver;
    final int gold;
    final int platinum;
    final int level;
    final int progress;

    PsnGamerProfile(Builder builder) {
        psnId = builder.psnId;
        aboutMe = builder.aboutMe;
        avatar = builder.avatar;
        bronze = builder.bronze;
        silver = builder.silver;
        gold = builder.gold;
        platinum = builder.platinum;
        level = builder.level;
        progress = builder.progress;
    }

    /**
     * Returns PsnId.
     *
     * @since SEN-PSN-US 1.0
     */
    @Override
    public String getOnlineId() {
        return psnId;
    }

    /**
     * Gets profile about me message
     *
     * @return about me message
     * @since SEN-PSN-US 1.0
     */
    public String getAboutMe() {
        return aboutMe;
    }

    /**
     * Gets avatar.
     *
     * @return avatar URL
     * @since SEN-PSN-US 1.0
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Gets bronze trophy count.
     *
     * @return bronze count
     * @since SEN-PSN-US 1.0
     */
    public int getBronze() {
        return bronze;
    }

    /**
     * Gets silver trophy count.
     *
     * @return silver count
     * @since SEN-PSN-US 1.0
     */
    public int getSilver() {
        return silver;
    }

    /**
     * Gets bronze trophy count.
     *
     * @return bronze count
     * @since SEN-PSN-US 1.0
     */
    public int getGold() {
        return gold;
    }

    /**
     * Gets platinum trophy count.
     *
     * @return platinum count
     * @since SEN-PSN-US 1.0
     */
    public int getPlatinum() {
        return platinum;
    }

    /**
     * Gets trophy level.
     *
     * @return trophy level
     * @since SEN-PSN-US 1.0
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets trophy progress percent.
     *
     * @return trophy progress
     * @since SEN-PSN-US 1.0
     */
    public int getProgress() {
        return progress;
    }

    /**
     * Returns string in format
     * "PsnGamerProfile [psnId='psnId', aboutMe='aboutme']".
     *
     * @since SEN-PSN-US 1.0
     */
    @Override
    public String toString() {
        return String.format("PsnGamerProfile [psnId=%s, aboutMe=%s]", psnId,
                aboutMe);
    }

    /**
     * Builder for gamer profile.
     *
     * @author Kyle Kroboth
     * @since SEN-PSN-US 1.0
     */
    public static class Builder {
        String psnId;
        String aboutMe;
        String avatar;
        int bronze;
        int silver;
        int gold;
        int platinum;
        int level;
        int progress;

        public Builder setPsnId(String psnId) {
            this.psnId = psnId;
            return this;
        }

        public Builder setAboutMe(String aboutMe) {
            this.aboutMe = aboutMe;
            return this;
        }

        public Builder setAvatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder setBronze(int bronze) {
            this.bronze = bronze;
            return this;
        }

        public Builder setSilver(int silver) {
            this.silver = silver;
            return this;
        }

        public Builder setGold(int gold) {
            this.gold = gold;
            return this;
        }

        public Builder setPlatinum(int platinum) {
            this.platinum = platinum;
            return this;
        }

        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder setProgress(int progress) {
            this.progress = progress;
            return this;
        }

        public <T extends PsnGamerProfile> PsnGamerProfile build() {
            return new PsnGamerProfile(this);
        }

    }

}
