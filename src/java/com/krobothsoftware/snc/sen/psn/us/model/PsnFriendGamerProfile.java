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

package com.krobothsoftware.snc.sen.psn.us.model;

import com.krobothsoftware.snc.sen.psn.model.FriendStatus;

/**
 * Holds friend gamer profile info used by <i>US</i> methods. Extension of
 * <code>GamerProfile</code>
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN-US 1.0
 * @see com.krobothsoftware.snc.sen.psn.us.model.PsnGamerProfile
 */
public class PsnFriendGamerProfile extends PsnGamerProfile {
	private static final long serialVersionUID = 4132907035009143250L;
	private final String comment;
	private final FriendStatus status;
	private final String title;
	private final String titleStatus;
	private final String lastSeen;

	PsnFriendGamerProfile(Builder builder) {
		super(builder);
		comment = builder.comment;
		status = builder.status;
		title = builder.title;
		titleStatus = builder.titleStatus;
		lastSeen = builder.lastSeen;
	}

	/**
	 * Gets comment if online.
	 * 
	 * @return comment or null if has no comment
	 * @since SEN-PSN-US 1.0
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Gets online status.
	 * 
	 * @return status
	 * @since SEN-PSN-US 1.0
	 */
	public FriendStatus getStatus() {
		return status;
	}

	/**
	 * Gets date of last seen when offline.
	 * 
	 * @return last seen date or null if online
	 * @see com.krobothsoftware.snc.sen.psn.PsnUtils#getOfficialDate(String,
	 *      java.util.Locale)
	 * @since SEN-PSN-US 1.0
	 */
	public String getLastSeen() {
		return lastSeen;
	}

	/**
	 * Gets title currently on.
	 * 
	 * @return game or service title, or null if none
	 * @since SEN-PSN-US 1.0
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets title status comment if supplied.
	 * 
	 * @return title status, or null if none
	 * @since SEN-PSN-US 1.0
	 */
	public String getTitleStatus() {
		return titleStatus;
	}

	/**
	 * Returns string in format
	 * "PsnFriendeGamerProfile [status='status', psnId='psnId']"
	 * 
	 * @since SEN-PSN-US 1.0
	 */
	@Override
	public String toString() {
		return String.format("PsnFriendGamerProfile [status=%s, psnId=%s]",
				status, psnId);
	}

	/**
	 * Builder for friend gamer profile
	 * 
	 * @author Kyle Kroboth
	 * @since SEN-PSN-US 1.0
	 */
	public static class Builder extends PsnGamerProfile.Builder {
		String comment;
		FriendStatus status;
		String title;
		String titleStatus;
		String lastSeen;

		public Builder setComment(String comment) {
			this.comment = comment;
			return this;
		}

		public Builder setStatus(FriendStatus status) {
			this.status = status;
			return this;
		}

		public Builder setLastSeen(String lastSeen) {
			this.lastSeen = lastSeen;
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setTitleStatus(String titleStatus) {
			this.titleStatus = titleStatus;
			return this;
		}

		@Override
		public PsnFriendGamerProfile build() {
			return new PsnFriendGamerProfile(this);

		}
	}

}
