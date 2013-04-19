package com.krobothsoftware.commons.network;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Request handler for getting RequestBuilder depending on status code.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 * @see com.krobothsoftware.commons.network.RequestBuilder#setInternalHandler(int,
 *      RequestHandler)
 */
public interface RequestHandler {

	/**
	 * Handle request for status code. Must close connection after.
	 * 
	 * @param builder
	 *            builder for handling
	 * @param connection
	 *            of builder, must close
	 * @return new request builder
	 * @throws IOException
	 * @since SNC 1.0
	 */
	RequestBuilder getRequest(RequestBuilder builder,
			HttpURLConnection connection) throws IOException;

}
