package com.krobothsoftware.tool.chrome;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krobothsoftware.commons.network.Method;
import com.krobothsoftware.commons.network.NetworkHelper;
import com.krobothsoftware.commons.network.RequestBuilder;
import com.krobothsoftware.commons.network.Response;
import com.krobothsoftware.commons.util.CommonUtils;

public class SourceFile extends RandomAccessFile {
	public final Logger log = LoggerFactory.getLogger(SourceFile.class);
	private StringBuilder builder;
	private int tabs;

	public SourceFile(File file) throws IOException {
		super(file, "rw");
		this.builder = new StringBuilder();

		// put header and class
		comment("Generated from KrobothSoftware HAR Tool "
				+ new Date().toString() + "\n", false);
		addImport(NetworkHelper.class);
		addImport(RequestBuilder.class);
		addImport(Response.class);
		addImport(Method.class);
		addImport(CommonUtils.class);
		addImport(URL.class);
		addImport(IOException.class);
	}

	public void startClass() {
		builder.append("\npublic class Requests {");
		tabs += 1;
		appendNewLine();
		writeBuilder();
	}

	public long endClass() {
		tabs = 0;
		appendNewLine().append('}');
		return writeBuilder();
	}

	public void addTabs(int tabs) {
		this.tabs += tabs;
	}

	public int getTabs(int tabs) {
		return tabs;
	}

	public void addImport(Class<?> clazz) {
		addImport(clazz.getName());
	}

	public void addImport(String clazz) {
		builder.append("import ").append(clazz).append(';').append('\n');
		writeBuilder();
	}

	public void addField(String field) {
		builder.append(field);
		appendNewLine();
		writeBuilder();
	}

	public long addMethod(String method) {
		appendNewLine().append(method).append(" {");
		tabs += 1;
		appendNewLine();
		return getLength();
	}

	public long endMethod() {
		tabs = 1;
		appendNewLine().append('}');
		return writeBuilder();
	}

	public long comment(String comment, boolean multi) {
		if (!multi) {
			builder.append("// ").append(comment);
		} else {
			builder.append("/*").append('\n').append("* ").append(comment)
					.append('\n').append("*/");
		}
		appendNewLine();
		writeBuilder();

		return getLength();
	}

	public StringBuilder appendNewLine() {
		return appendNewLine(0);
	}

	public StringBuilder appendNewLine(int tabs) {
		builder.append('\n');
		for (int i = 0; i < this.tabs + tabs; i++) {
			builder.append('\t');
		}
		return builder;
	}

	public long appendLine(String text) {
		builder.append(text);
		appendNewLine();
		return writeBuilder();
	}

	public StringBuilder builder() {
		return builder;
	}

	public long writeBuilder() {
		int len = 0;
		try {
			byte[] b = builder.toString().getBytes();
			len = b.length;
			write(b);
		} catch (IOException e) {
			log.error("Append Error", e);
		}
		builder = new StringBuilder();
		return len;
	}

	private long getLength() {
		long length = 0;
		try {
			length = length();
		} catch (IOException e) {
			log.error("Length error", e);
		}

		return length;
	}

	public static String unEscapeString(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++)
			switch (s.charAt(i)) {
				case '\n':
					sb.append("\\n");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\'':
					sb.append("\\'");
					break;
				case '\"':
					sb.append("\\\"");
					break;
				case '\\':
					sb.append('\\');
					break;
				default:
					sb.append(s.charAt(i));
			}
		return sb.toString();
	}

}
