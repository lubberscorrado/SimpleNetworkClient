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

import com.krobothsoftware.snc.sen.OnlineId;
import com.krobothsoftware.snc.sen.Platform;

/**
 * Holds game data used by <i>UK</i> and <i>US</i> methods.
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public class PsnGame implements OnlineId, Serializable {
	private static final long serialVersionUID = 7368966773723281525L;
	private final String psnId;
	private final String gameId;
	private final Platform pf;
	private final String name;
	private final String image;
	private final int progress;
	private final int platinum;
	private final int gold;
	private final int silver;
	private final int bronze;
	private final String titleLinkId;

	PsnGame(Builder builder) {
		psnId = builder.userId;
		gameId = builder.gameId;
		pf = builder.pf;
		name = builder.name;
		image = builder.image;
		progress = builder.progress;
		platinum = builder.platinum;
		gold = builder.gold;
		silver = builder.silver;
		bronze = builder.bronze;
		titleLinkId = builder.titleLinkId;

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
	 * Gets official game Id.
	 * 
	 * @return game id
	 * @since SEN-PSN 1.0
	 */
	public String getGameId() {
		return gameId;
	}

	/**
	 * Gets game title name.
	 * 
	 * @return game name
	 * @since SEN-PSN 1.0
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets game platform. On <i>US</i> methods, this will be
	 * {@link Platform#UNKNOWN} since there is no way to get it.
	 * 
	 * @return game platform
	 * @since SEN-PSN 1.0
	 */
	public Platform getPlatform() {
		return pf;
	}

	/**
	 * Gets game image.
	 * 
	 * @return game image
	 * @since SEN-PSN 1.0
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Gets progress of game.
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
	 * Gets trophy link Id depending on <i>US</i> or <i>UK</i> method.
	 * 
	 * @return trophy link Id
	 * @since SEN-PSN 1.0
	 */
	public String getTitleLinkId() {
		return titleLinkId;
	}

	/**
	 * Returns string in format
	 * "PsnGame [gameId='gameId', name='name', progress='progress']".
	 * 
	 * @since SEN-PSN 1.0
	 */
	@Override
	public String toString() {
		return String.format("PsnGame [gameId=%s, name=%s, progress=%s]",
				gameId, name, String.valueOf(progress));
	}

	/**
	 * Builder for Games.
	 * 
	 * @author Kyle Kroboth
	 * @since SEN-PSN 1.0
	 */
	public static class Builder extends BuilderGame<PsnGame> {
		String name;
		Platform pf;
		String image;
		int progress;

		public Builder(String userId) {
			super(userId);

		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setPlatform(Platform platform) {
			pf = platform;
			return this;
		}

		public Builder setImage(String gameImage) {
			this.image = gameImage;
			return this;
		}

		public Builder setProgress(int progress) {
			this.progress = progress;
			return this;
		}

		@Override
		public PsnGame build() {
			return new PsnGame(this);
		}

	}

}
