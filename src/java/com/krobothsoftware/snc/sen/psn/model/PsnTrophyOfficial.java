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

import java.io.Serializable;

import com.krobothsoftware.snc.sen.Platform;
import com.krobothsoftware.snc.sen.psn.Jid;
import com.krobothsoftware.snc.sen.psn.Trophy;

/**
 * Holds trophy data for <i>Official</i> methods.
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public class PsnTrophyOfficial implements Jid, Serializable {
	private static final long serialVersionUID = -5311787059197244617L;
	private final String jid;
	private final Platform pf;
	private final int index;
	private final String gameId;
	private final String dateEarned;
	private final Trophy type;
	private final boolean isReceived;

	PsnTrophyOfficial(Builder builder) {
		jid = builder.userId;
		index = builder.index;
		gameId = builder.gameId;
		dateEarned = builder.dateEarned;
		type = builder.type;
		isReceived = builder.received;
		pf = builder.pf;
	}

	/**
	 * @since SEN-PSN 1.0
	 */
	@Override
	public String getJid() {
		return jid;
	}

	/**
	 * Gets platform of trophy.
	 * 
	 * @return trophy platform
	 * @since SEN-PSN 1.0
	 */
	public Platform getPlatform() {
		return pf;
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
	 * Gets game Id of trophy.
	 * 
	 * @return game Id
	 * @since SEN-PSN 1.0
	 */
	public String getGameId() {
		return gameId;
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
	 * Returns string in format "PsnTrophyOfficial [trophyId='index',
	 * gameId='gameId', earned='earned', type='type']".
	 * 
	 * @since SEN-PSN 1.0
	 */
	@Override
	public String toString() {
		return String
				.format("PsnTrophyOfficial [trophyId=%s, gameId=%s, earned=%s, type=%s]",
						String.valueOf(index), gameId, dateEarned, type);
	}

	/**
	 * Builder for official trophies.
	 * 
	 * @author Kyle Kroboth
	 * @since SEN-PSN 1.0
	 */
	public static class Builder extends BuilderTrophy<PsnTrophyOfficial> {
		Platform pf;

		public Builder(String userId) {
			super(userId);
		}

		public Builder setPlatform(Platform pf) {
			this.pf = pf;
			return this;
		}

		@Override
		public PsnTrophyOfficial build() {
			return new PsnTrophyOfficial(this);
		}

	}

}
