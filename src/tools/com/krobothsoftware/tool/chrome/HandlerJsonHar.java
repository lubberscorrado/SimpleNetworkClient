package com.krobothsoftware.tool.chrome;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.krobothsoftware.commons.parse.HandlerJson;
import com.krobothsoftware.commons.parse.ParseException;

public class HandlerJsonHar extends HandlerJson {
	private final List<String> ignoreHeaders;

	private final SourceFile source;
	private String objectName = "";
	private String arrayName = "";
	private final int num = 0;

	public HandlerJsonHar(HarExport har) {
		this.source = har.source;
		ignoreHeaders = new ArrayList<String>();
		ignoreHeaders.add("ACCEPT");
		ignoreHeaders.add("ACCEPT-CHARSET");
		ignoreHeaders.add("ACCEPT-ENCODING");
		ignoreHeaders.add("ACCEPT-LANGUAGE");
		ignoreHeaders.add("HOST");
		ignoreHeaders.add("USER-AGENT");
		ignoreHeaders.add("COOKIE");
		ignoreHeaders.add("CONNECTION");
	}

	@Override
	public void startObject(String name) throws ParseException,
			JsonParseException, IOException {
		if (name != null) objectName = name;
	}

	@Override
	protected void startArray(String name) {
		arrayName = name;
	}

	@Override
	public void tokenText(String name) throws ParseException,
			JsonParseException, IOException {
		super.tokenText(name);
		String str;
		if (arrayName.equals("headers")) {
			if (name.equals("name")) {
				str = jsonParser.getText();
				if (ignoreHeaders.contains(str.toUpperCase())) return;
				source.builder().append(".header(\"").append(str)
						.append("\", ");
				return;
			} else if (name.equals("value")) {
				if (source.builder().length() > 0) {
					source.builder().append('"').append(jsonParser.getText())
							.append("\")");
					source.appendNewLine();
					source.writeBuilder();
				}
				return;
			}
		}

		if (objectName.equals("request")) {
			if (name.equals("method")) {
				source.builder().append("request = new RequestBuilder(Method.")
						.append(jsonParser.getText()).append(", ");
				return;
			} else if (name.equals("url")) {
				source.builder().append("new URL(");
				source.addTabs(1);
				source.appendNewLine().append('"').append(jsonParser.getText())
						.append("\"))");
				source.appendNewLine();
				source.writeBuilder();
			}
		}

		// lazy to enhance. Just kept in back
		if (name.equals("startedDateTime")) source.comment("Start Date Time "
				+ jsonParser.getText(), false);
		else if (name.equals("time")) source.comment("Time elapsed "
				+ jsonParser.getText(), false);
	}

	// static {

	// // TODO if ignore network helper headers
	// if (true) {
	// IGNORE_HEADER = new String[] { "Accept", "Accept-Charset",
	// "Accept-Encoding", "Accept-Language", "Cookie", "Host",
	// "User-Agent" };
	// } else {
	// IGNORE_HEADER = new String[] { "Accept", "Cookie", "Host" };
	// }
	// }
}
