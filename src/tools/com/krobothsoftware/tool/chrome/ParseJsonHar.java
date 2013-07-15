package com.krobothsoftware.tool.chrome;

import static com.krobothsoftware.tool.chrome.SourceFile.unEscapeString;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krobothsoftware.commons.network.value.Cookie;
import com.krobothsoftware.commons.network.value.CookieMap;
import com.krobothsoftware.commons.network.value.NameValuePair;
import com.krobothsoftware.tool.chrome.HarExport.Option;

public class ParseJsonHar {
	private final List<String> ignoreHeaders;
	private final SourceFile source;
	private final Option options;
	private Map<String, Object> map;
	private String redirectUrl;

	@SuppressWarnings("unchecked")
	public ParseJsonHar(HarExport har) throws JsonParseException,
			JsonMappingException, IOException {
		this.source = har.source;
		this.options = har.options;
		ignoreHeaders = new ArrayList<String>();
		// ignoreHeaders.add("ACCEPT");
		ignoreHeaders.add("ACCEPT-CHARSET");
		ignoreHeaders.add("ACCEPT-ENCODING");
		ignoreHeaders.add("ACCEPT-LANGUAGE");
		ignoreHeaders.add("HOST");
		ignoreHeaders.add("USER-AGENT");
		if (options.cookies > 0) ignoreHeaders.add("COOKIE");
		ignoreHeaders.add("CONNECTION");
		ignoreHeaders.add("ORIGIN");

		// init class
		if (options.buildParams) {
			source.addImport(List.class);
			source.addImport(NameValuePair.class);
		}
		if (options.cookies == 1) {
			source.addImport(Cookie.class);
		} else if (options.cookies == 2) {
			source.addImport(CookieMap.class);
		}
		source.startClass();

		// fields
		source.addField("public static final NetworkHelper networkHelper = new NetworkHelper();");

		// main method
		source.addMethod("public static void main(String[] args)");
		source.appendLine("Response response = null;");
		source.appendLine("RequestBuilder request;");
		if (options.buildParams) {
			source.appendLine("List<NameValuePair> query;");
		}
		// TODO add remove
		source.appendLine("Object payload;");
		source.appendNewLine();
		if (options.cookies == 2) {
			source.appendLine("CookieMap token = new CookieMap();");
		}
		source.appendNewLine();
		source.appendLine("try {");
		source.addTabs(1);
		source.appendNewLine();

		ObjectMapper mapper = new ObjectMapper();
		map = mapper.readValue(har.input,
				new TypeReference<Map<String, Object>>() {
				});
		map = (Map<String, Object>) map.get("log");

		// parse entries
		for (Map<String, Object> entry : (List<Map<String, Object>>) map
				.get("entries")) {
			source.comment("Started date time " + entry.get("startedDateTime"),
					false);
			source.comment("Total time " + entry.get("time"), false);
			Map<String, Object> requestMap = (Map<String, Object>) entry
					.get("request");
			Map<String, Object> responseMap = (Map<String, Object>) entry
					.get("response");

			// check if mineType is ok
			String mimeType = (String) ((Map<String, Object>) responseMap
					.get("content")).get("mimeType");
			if (mimeType != null && !options.acceptMimes.isEmpty()
					&& !options.acceptMimes.contains(mimeType)) {
				source.comment("Ignored mime type: " + mimeType, false);
				source.comment(requestMap.get("url").toString(), false);
				source.appendNewLine();
				continue;
			}

			source.comment("Mime type: " + mimeType, false);
			parseRequest(requestMap);
			parseResponse(responseMap);
		}

		// end try-catch, main method, and class
		source.addTabs(-1);
		source.appendNewLine().append("} catch (IOException e) {");
		source.appendNewLine(1).append("e.printStackTrace();");
		source.appendNewLine().append("} finally {");
		source.appendNewLine(1).append("CommonUtils.closeQuietly(response);");
		source.appendNewLine().append('}');

		source.endMethod();
		source.endClass();

	}

	@SuppressWarnings("unchecked")
	private void parseRequest(Map<String, Object> request) {
		boolean redirect = false;
		String url = request.get("url").toString();
		if (redirectUrl != null && redirectUrl.equals(url)) redirect = true;
		if (!redirect && options.buildParams && url.contains("?")) {
			source.builder().append("query = NetworkHelper.getPairs(");

			for (Object item : ((List<Object>) request.get("queryString"))) {
				source.appendNewLine(1);
				Map<String, Object> tmp = (Map<String, Object>) item;
				source.builder().append('"').append(tmp.get("name"))
						.append("\", ").append('"')
						.append(decodedValue(tmp.get("value").toString()))
						.append("\",");
			}
			source.builder().deleteCharAt(source.builder().length() - 1);
			source.builder().append(");\n");
			source.writeBuilder();
			source.appendNewLine();
		}

		if (redirect) source.comment(url, false);
		source.builder().append("request = new RequestBuilder(Method.")
				.append(request.get("method")).append(", ").append("new URL(");
		source.appendNewLine(1);
		if (redirect) {
			source.builder().append(
					"((ResponseRedirect) response).getRedirectUrl())");
		} else if (options.buildParams && url.contains("?")) {
			url = url.substring(0, url.indexOf("?"));
			source.builder().append("NetworkHelper.setQuery(\"").append(url)
					.append("\", query)");
		} else {
			source.builder().append('"').append(url).append('"');
		}
		source.builder().append("))");
		// source.appendNewLine(1);
		source.writeBuilder();

		parseHeaders((List<Map<String, Object>>) request.get("headers"));
		parseCookies((List<Map<String, Object>>) request.get("cookies"));
		if (options.cookies == 2) {
			source.appendNewLine(1).append(".use(token)");
		}
		source.appendLine(";");
		source.appendNewLine();

		if (request.containsKey("postData")) {
			parseParams((Map<String, Object>) request.get("postData"));
		}

		redirectUrl = null;

	}

	private void parseHeaders(List<Map<String, Object>> headers) {
		for (Map<String, Object> entry : headers) {
			String str = entry.get("name").toString();
			if (ignoreHeaders.contains(str.toUpperCase())) continue;
			source.appendNewLine(1);
			source.builder().append(".header(\"").append(str).append("\", \"")
					.append(entry.get("value").toString()).append("\")");
			source.writeBuilder();
		}
	}

	private void parseCookies(List<Map<String, Object>> cookies) {
		if (options.cookies != 1) return;
		for (Map<String, Object> entry : cookies) {
			source.appendNewLine(1);
			source.builder().append(".put(new Cookie(\"")
					.append(entry.get("name").toString()).append("\", \"")
					.append(entry.get("value").toString()).append("\"))");
			source.writeBuilder();
		}
	}

	@SuppressWarnings("unchecked")
	private void parseParams(Map<String, Object> post) {
		String mime = post.get("mimeType").toString();
		if (mime.toUpperCase().equals("APPLICATION/X-WWW-FORM-URLENCODED")) {
			if (options.buildParams) {
				source.builder().append("payload = NetworkHelper.getPairs(");
				for (Map<String, Object> entry : (List<Map<String, Object>>) post
						.get("params")) {
					source.appendNewLine(1);
					source.builder()
							.append('"')
							.append(entry.get("name"))
							.append("\", ")
							.append('"')
							.append(decodedValue(unEscapeString(
									entry.get("value").toString()).toString()))
							.append("\",");
				}
				source.builder().deleteCharAt(source.builder().length() - 1);
				source.builder().append(");");
				source.appendNewLine();
				source.appendLine("request.payload((List<NameValuePair>) payload, \"UTF-8\");");
			} else {
				source.builder().append("payload = new String(");
				source.appendNewLine(1).append('"')
						.append(unEscapeString(post.get("text").toString()))
						.append("\")");
				source.appendNewLine();
				source.appendLine("request.payload(((String) payload).getBytes(\"UTF-8\"));");
			}
		} else {
			source.builder().append("payload = new String(");
			source.appendNewLine(1).append('"')
					.append(unEscapeString(post.get("text").toString()))
					.append("\");");
			source.appendNewLine();
			source.appendLine("request.payload(((String) payload).getBytes(\"UTF-8\"));");
		}
		source.appendNewLine();
		source.writeBuilder();
	}

	private void parseResponse(Map<String, Object> response) {
		String redirect = response.get("redirectURL").toString();

		if (options.smart && !redirect.isEmpty()) {
			redirectUrl = redirect;
			source.comment("ResponseRedirect", false);
		}
		source.appendLine("response = request.execute(networkHelper);");

		source.appendLine("System.out.println(response);");
		source.appendLine("response.close();");
		source.appendNewLine();
		source.writeBuilder();
	}

	private String decodedValue(String value) {
		String dec;
		try {
			dec = URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			source.log.error("Decode failure", e);
			dec = "!DECODE-FAILURE!";
		}
		return dec;
	}
}
