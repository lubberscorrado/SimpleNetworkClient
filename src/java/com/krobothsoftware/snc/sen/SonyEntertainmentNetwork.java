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

import com.krobothsoftware.commons.network.NetworkHelper;
import com.krobothsoftware.commons.parse.Parser;
import com.krobothsoftware.snc.NetworkClient;

/**
 * Sony Entertainment Network client for all of Sony's services.
 * 
 * @author Kyle Kroboth
 * @since SEN 1.0
 */
public class SonyEntertainmentNetwork extends NetworkClient {

	/**
	 * Creates SEN client.
	 * 
	 * @since SEN 1.0
	 */
	public SonyEntertainmentNetwork() {
		super(SonyEntertainmentNetwork.class.getName());
	}

	/**
	 * Creates sub network of SEN client.
	 * 
	 * @param name
	 *            name of logger
	 * @since SEN 1.0
	 */
	protected SonyEntertainmentNetwork(String name) {
		super(name);
	}

	/**
	 * Creates sub network of SEN client with network and parser objects.
	 * 
	 * @param name
	 *            name of logger
	 * @param networkHelper
	 *            network for client
	 * @param parser
	 *            parser for client
	 * @since SEN 1.0
	 */
	protected SonyEntertainmentNetwork(String name,
			NetworkHelper networkHelper, Parser parser) {
		super(name, networkHelper, parser);
	}

	// ******************************
	// Reserved client
	// ******************************

}
