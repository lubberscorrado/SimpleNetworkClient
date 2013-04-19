package com.krobothsoftware.snc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krobothsoftware.commons.network.NetworkHelper;
import com.krobothsoftware.commons.parse.Parser;

/**
 * 
 * Base client for networks on handling connections and parsing data. Using the
 * same instance of a client is a good practice.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public class NetworkClient {

	/**
	 * Network Helper for all connections in client.
	 * 
	 * @since SNC 1.0
	 */
	protected final NetworkHelper networkHelper;

	/**
	 * Parser for parsing Handlers in client.
	 * 
	 * @since SNC 1.0
	 */
	protected final Parser parser;

	/**
	 * Specific logger for client.
	 * 
	 * @see #NetworkClient(String)
	 * @since SNC 1.0
	 */
	protected final Logger log;

	/**
	 * Create new client and set values.
	 * 
	 * @param name
	 *            logger type
	 * @param networkHelper
	 *            network helper for client
	 * @param parser
	 *            parser for client
	 * @since SNC 1.0
	 */
	protected NetworkClient(String name, NetworkHelper networkHelper,
			Parser parser) {
		log = LoggerFactory.getLogger(name);
		this.networkHelper = networkHelper;
		this.parser = parser;
	}

	/**
	 * Creates new Network Client with name for logger.
	 * 
	 * @param name
	 *            logger type
	 * @since SNC 1.0
	 */
	public NetworkClient(String name) {
		log = LoggerFactory.getLogger(name);
		networkHelper = new NetworkHelper();
		parser = new Parser();
	}

	/**
	 * Reset client.
	 * 
	 * @since SNC 1.0
	 */
	public void cleanup() {
		networkHelper.reset();
	}

	/**
	 * Gets network helper.
	 * 
	 * @return network helper
	 * @since SNC 1.0
	 */
	public final NetworkHelper getNetworkHelper() {
		return networkHelper;
	}

	/**
	 * Gets parser.
	 * 
	 * @return parser
	 * @since SNC 1.0
	 */
	public final Parser getParser() {
		return parser;
	}

}
