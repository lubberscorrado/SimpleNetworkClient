package com.krobothsoftware.commons.network;

import java.io.IOException;
import java.net.HttpURLConnection;
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

	static class RedirectHandler implements RequestHandler {

		@Override
		public RequestBuilder getRequest(int statuscode,
				RequestBuilder builder, HttpURLConnection connection)
				throws IOException {
			// respect follow redirects option
			if (statuscode == 302 && !builder.followRedirects) return null;

			builder.log.debug("Internally handled redirect");
			String location = connection.getHeaderField("Location");
			connection.disconnect();

			// only use one instance of redirect builder
			RequestBuilder newBuilder;
			if (builder instanceof RequestBuilderRedirect) {
				newBuilder = builder;
				newBuilder.setUrl(new URL(location));
			} else {
				newBuilder = new RequestBuilderRedirect(builder, location);
			}
			return newBuilder;
		}

	}

}
