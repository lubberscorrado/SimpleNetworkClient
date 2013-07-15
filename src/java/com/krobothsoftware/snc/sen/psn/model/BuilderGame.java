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

/**
 * Builder for games.
 *
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public abstract class BuilderGame<T> {
    public String userId;
    public String gameId;
    public int platinum;
    public int gold;
    public int silver;
    public int bronze;
    public String titleLinkId;

    public BuilderGame(String userId) {
        this.userId = userId;
    }

    public BuilderGame<T> setGameId(String gameId) {
        this.gameId = gameId;
        return this;
    }

    public BuilderGame<T> setPlatinum(int platinum) {
        this.platinum = platinum;
        return this;
    }

    public BuilderGame<T> setGold(int gold) {
        this.gold = gold;
        return this;
    }

    public BuilderGame<T> setSilver(int silver) {
        this.silver = silver;
        return this;
    }

    public BuilderGame<T> setBronze(int bronze) {
        this.bronze = bronze;
        return this;
    }

    public BuilderGame<T> setTitleLinkId(String titleLinkId) {
        this.titleLinkId = titleLinkId;
        return this;
    }

    public abstract T build();

}
