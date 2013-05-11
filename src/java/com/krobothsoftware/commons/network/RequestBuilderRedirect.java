package com.krobothsoftware.commons.network;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Internal redirect request.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
class RequestBuilderRedirect extends RequestBuilder {
	private int redirect = 1;

	public RequestBuilderRedirect(RequestBuilder builder, String location)
			throws MalformedURLException {
		super(builder, builder.method, new URL(location));
		if (builder instanceof RequestBuilderRedirect) {
			redirect = ((RequestBuilderRedirect) builder).redirect;
		}
	}

	@Override
	public Response execute(NetworkHelper networkHelper) throws IOException {
		if (redirect++ > networkHelper.maxRedirects) throw new IOException(
				"Request was redirected too many times: " + redirect);
		return super.execute(networkHelper);
	}

}
