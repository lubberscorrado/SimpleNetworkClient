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
				newBuilder.url(new URL(location));
			} else {
				newBuilder = new RequestBuilderRedirect(builder, location);
			}

			return newBuilder;
		}

	}

}
