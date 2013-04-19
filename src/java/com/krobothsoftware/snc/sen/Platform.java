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

package com.krobothsoftware.snc.sen;

import com.krobothsoftware.snc.Beta;

/**
 * Current Platforms from Sony.
 * 
 * @author Kyle Kroboth
 * @since SEN 1.0
 */
public enum Platform {

	/**
	 * PlayStation 4 platform. Reserved and currently not used since the PS4
	 * isn't out.
	 * 
	 * @since SEN 1.0
	 * @beta Platform has not come out yet
	 */
	@Beta
	PS4 {

		/**
		 * Returns platform "ps4".
		 */
		@Override
		public String getTypeString() {
			return "ps4";
		}

	},

	/**
	 * PlayStation 3 platform.
	 * 
	 * @since SEN 1.0
	 */
	PS3 {

		/**
		 * Returns platform "ps3".
		 */
		@Override
		public String getTypeString() {
			return "ps3";
		}
	},

	/**
	 * PlayStation Vita platform.
	 * 
	 * @since SEN 1.0
	 */
	VITA {

		/**
		 * Returns platform "psp2".
		 */
		@Override
		public String getTypeString() {
			return "psp2";
		}
	},

	/**
	 * PlayStation PSP platform.
	 * 
	 * @since SEN 1.0
	 */
	PSP {

		/**
		 * Returns platform "psp".
		 */
		@Override
		public String getTypeString() {
			return "psp";
		}

	},

	/**
	 * Unknown platform.
	 * 
	 * @since SEN 1.0
	 */
	UNKNOWN {

		/**
		 * Returns platform "uknown".
		 */
		@Override
		public String getTypeString() {
			return "unknown";
		}

	};

	/**
	 * Gets valid values
	 * 
	 * @return PS3, VITA, and PSP
	 * @since SEN 1.0
	 */
	public static Platform[] validValues() {
		return new Platform[] { PS3, VITA, PSP };
	}

	/**
	 * Gets the type string used by <i>Official</i> methods.
	 * 
	 * @return type string
	 * @since SEN 1.0
	 */
	public abstract String getTypeString();

	/**
	 * Gets the platform from type string.
	 * 
	 * @param typeString
	 *            official platform type string
	 * @return platform
	 * @since SEN 1.0
	 */
	public static Platform getPlatform(String typeString) {
		if (typeString.equalsIgnoreCase("ps3")) return PS3;
		else if (typeString.equalsIgnoreCase("psp2")) return VITA;
		else if (typeString.equalsIgnoreCase("psp")) return PSP;

		return null;
	}

}
