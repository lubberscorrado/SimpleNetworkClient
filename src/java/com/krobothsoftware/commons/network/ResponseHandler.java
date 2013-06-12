package com.krobothsoftware.commons.network;

import java.net.HttpURLConnection;

/**
 * Handle <code>Responses</code> with every
 * {@link RequestBuilder#execute(NetworkHelper)}.
 * 
 * @author Kyle Kroboth
 * @since COMMONS 1.0
 */
public interface ResponseHandler {

	/**
	 * Get correct {@link Response} for given results after
	 * {@link RequestBuilder} executes.
	 * 
	 * @param connection
	 *            connection for response
	 * @param input
	 *            stream for response
	 * @param status
	 *            response code for response
	 * @param charset
	 *            charset for response
	 * @return custom response or null
	 * @see com.krobothsoftware.commons.network.RequestBuilder#execute(NetworkHelper)
	 * @since COMMONS 1.0
	 */
	Response getResponse(HttpURLConnection connection,
			UnclosableInputStream input, int status, String charset);

}
