package com.krobothsoftware.tool.chrome;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import com.krobothsoftware.commons.util.CommonUtils;

public class HarExport {
	public final InputStream input;
	public SourceFile source;
	public Option options;

	public HarExport(InputStream input, Option options) {
		this.input = input;
		this.options = options;
		// debug purposes
		// options.acceptMimes.add("text/html");
		// options.acceptMimes.add("text/plain");
	}

	public HarExport parse() throws IOException {
		File sourceFile = new File(options.outFile);
		sourceFile.createNewFile();
		sourceFile.delete();
		source = new SourceFile(sourceFile);

		ParseJsonHar handler = new ParseJsonHar(this);
		source.close();
		return this;
	}

	public static void main(String[] args) throws IOException {
		Option options = new Option();
		String har = null;

		for (int i = 0; i < args.length; i++) {
			String str = args[i];
			if (str.charAt(0) == '-') {
				switch (str.charAt(1)) {
					case 'S':
					case 's':
						options.smart = true;
						break;
					case 'B':
					case 'b':
						options.buildParams = true;
						break;
					case 'C':
					case 'c':
						char c = str.charAt(2);
						if (c == 'h') options.cookies = 0;
						else if (c == 'b') options.cookies = 1;
						else if (c == 't') options.cookies = 2;
						else
							throw new RuntimeException(
									"invalid cookie option [" + c + "]");
						break;
					case 'A':
					case 'a':
						String[] headers = str.substring(2).split(",");
						for (String header : headers) {
							options.acceptMimes.add(header);
						}
						break;
					case 'O':
					case 'o':
						options.outFile = str.substring(1);
						break;
				}
			} else {
				// must be har file
				har = str;
			}

		}

		if (har == null) throw new RuntimeException("No chrome har file");
		if (options.outFile == null) options.outFile = "Requests.java";

		InputStream input = new FileInputStream(har);
		HarExport export = new HarExport(input, options);
		try {
			export.parse();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuietly(input);
		}

	}

	private static void showHelp() {

	}

	public static class Option {
		public String outFile;
		public boolean smart = false;
		public boolean buildParams = false;
		// 0 header, 1 - build cookies, 2 - token
		public int cookies = 2;
		public final Set<String> acceptMimes = new HashSet<String>();
	}

}
