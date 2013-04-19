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

/**
 * Friend status enum for <code>PsnFriendData</code>.
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN 1.0
 */
public enum FriendStatus {

	/**
	 * Online status.
	 * 
	 * @since SEN-PSN 1.0
	 */
	ONLINE {

		/**
		 * Retuns status "Online".
		 */
		@Override
		public String getName() {
			return "Online";
		}

	},

	/**
	 * Offline status.
	 * 
	 * @since SEN-PSN 1.0
	 */
	OFFLINE {

		/**
		 * Returns status "Offline".
		 */
		@Override
		public String getName() {
			return "Offline";
		}

	},

	/**
	 * Away status.
	 * 
	 * @since SEN-PSN 1.0
	 */
	AWAY {

		/**
		 * Returns Status "Away".
		 */
		@Override
		public String getName() {
			return "Away";
		}

	},

	/**
	 * Pending friend request.
	 * 
	 * @since SEN-PSN 1.0
	 */
	PENDING_RESPONSE {

		/**
		 * Returns status "Pending Response".
		 */
		@Override
		public String getName() {
			return "Pending Response";
		}

	};

	/**
	 * Gets status name. Not same as {@link Enum#toString()}.
	 * 
	 * @return status name
	 * @since SEN-PSN 1.0
	 */
	public abstract String getName();

}
