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

package com.krobothsoftware.snc.sen.psn.model;

import com.krobothsoftware.snc.sen.psn.Trophy;

/**
 * Builder for trophies.
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public abstract class BuilderTrophy<T> {
	public String userId;
	public int index;
	public String gameId;
	public String dateEarned;
	public boolean received;
	public Trophy type;

	public BuilderTrophy(String userId) {
		this.userId = userId;
	}

	public BuilderTrophy<T> setIndex(int index) {
		this.index = index;
		return this;
	}

	public BuilderTrophy<T> setGameId(String gameId) {
		this.gameId = gameId;
		return this;
	}

	public BuilderTrophy<T> setDateEarned(String dateEarned) {
		this.dateEarned = dateEarned;
		return this;
	}

	public BuilderTrophy<T> setReceieved(boolean received) {
		this.received = received;
		return this;
	}

	public BuilderTrophy<T> setType(Trophy handler) {
		this.type = handler;
		return this;
	}

	public abstract T build();

}
