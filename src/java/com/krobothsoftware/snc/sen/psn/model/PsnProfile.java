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

import com.krobothsoftware.snc.sen.psn.Jid;

import java.io.Serializable;

/**
 * Holds profile data used by <i>Official</i> methods.
 *
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public class PsnProfile implements Jid, Serializable {
    private static final long serialVersionUID = 8271705791064135500L;
    private final String jid;
    private final String avatar;
    private final String aboutMe;
    private final String country;
    private final boolean isPP;
    private final int backgroundColor;
    private final String panel;
    private final int panelBackgroundColor;

    private final int points;
    private final int level;
    private final int levelFloor;
    private final int levelCeiling;
    private final int progress;
    private final int platinum;
    private final int gold;
    private final int silver;
    private final int bronze;

    PsnProfile(Builder builder) {
        jid = builder.jid;
        avatar = builder.avatar;
        aboutMe = builder.aboutMe;
        country = builder.country;
        isPP = builder.isPP;
        backgroundColor = builder.backgroundColor;
        points = builder.points;
        level = builder.level;
        levelFloor = builder.levelFloor;
        levelCeiling = builder.levelCeiling;
        progress = builder.progress;
        platinum = builder.platinum;
        gold = builder.gold;
        silver = builder.silver;
        bronze = builder.bronze;
        panel = builder.panel;
        panelBackgroundColor = builder.panelBackgroundColor;
    }

    /**
     * @since SEN-PSN 1.0
     */
    @Override
    public String getJid() {
        return jid;
    }

    /**
     * Gets avatar.
     *
     * @return avatar image link
     * @since SEN-PSN 1.0
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Gets about me message.
     *
     * @return about me message
     * @since SEN-PSN 1.0
     */
    public String getAboutMe() {
        return aboutMe;
    }

    /**
     * Gets country culture in Locale. Will iterate each Local from
     * {@link java.util.Locale#getAvailableLocales()} and find correct match.
     *
     * @return country culture
     * @since SEN-PSN 1.0.2
     */
    public String getCountryCulture() {
        return country;
    }

    /**
     * Checks if profile is a Playstation Plus member.
     *
     * @return is Playstation Plus
     * @since SEN-PSN 1.0
     */
    public boolean isPlayStationPlus() {
        return isPP;
    }

    /**
     * Gets background color of profile.
     *
     * @return color in RGB form
     * @since SEN-PSN 1.0
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Gets trophy points.
     *
     * @return trophy points
     * @since SEN-PSN 1.0
     */
    public int getPoints() {
        return points;
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
     * Gets current trophy level floor.
     *
     * @return trophy level floor
     * @since SEN-PSN 1.0
     */
    public int getLevelFloor() {
        return levelFloor;
    }

    /**
     * Gets current trophy level ceiling.
     *
     * @return trophy level ceiling
     * @since SEN-PSN 1.0
     */
    public int getLevelCeiling() {
        return levelCeiling;
    }

    /**
     * Gets trophy progress percent.
     *
     * @return trophy progress
     * @since SEN-PSN 1.0
     */
    public int getProgress() {
        return progress;
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
     * Gets PS Vita panel url.
     *
     * @return panel url
     * @since SEN-PSN 1.0
     */
    public String getPanel() {
        return panel;
    }

    /**
     * Gets PS Vita panel background color.
     *
     * @return panel color in RGB format
     * @since SEN-PSN 1.0
     */
    public int getPanelBackgroundColor() {
        return panelBackgroundColor;
    }

    /**
     * Returns string in format "PsnProfile [aboutMe='aboutme',
     * countryCulture='culture', level='level', progress='progress',
     * psnId='psnId']".
     *
     * @since SEN-PSN 1.0
     */
    @Override
    public String toString() {
        return String
                .format("PsnProfile [aboutMe=%s, countryCulture=%s, level=%s, progress=%s, jid=%s]",
                        aboutMe, country, String.valueOf(level),
                        String.valueOf(progress), jid);
    }

    /**
     * Builder for profiles.
     *
     * @author Kyle Kroboth
     * @since SEN-PSN 1.0
     */
    public static class Builder {
        String jid;
        String avatar;
        String aboutMe;
        String country;
        boolean isPP;
        int backgroundColor;
        String panel;
        int panelBackgroundColor;

        int points;
        int level;
        int levelFloor;
        int levelCeiling;
        int progress;
        int platinum;
        int gold;
        int silver;
        int bronze;

        public Builder setJid(String jid) {
            this.jid = jid;
            return this;
        }

        public Builder setAvatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder setAboutMe(String aboutMe) {
            this.aboutMe = aboutMe;
            return this;
        }

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder setPP(boolean isPP) {
            this.isPP = isPP;
            return this;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setPoints(int points) {
            this.points = points;
            return this;
        }

        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder setLevelFloor(int levelFloor) {
            this.levelFloor = levelFloor;
            return this;
        }

        public Builder setLevelCeiling(int levelCeiling) {
            this.levelCeiling = levelCeiling;
            return this;
        }

        public Builder setProgress(int progress) {
            this.progress = progress;
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

        public Builder setPanel(String panel) {
            this.panel = panel;
            return this;
        }

        public Builder setPanelBackgroundColor(int panelBackgroundColor) {
            this.panelBackgroundColor = panelBackgroundColor;
            return this;
        }

        public PsnProfile build() {
            return new PsnProfile(this);
        }

    }

}
