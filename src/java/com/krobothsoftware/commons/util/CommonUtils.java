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

package com.krobothsoftware.commons.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common Utility methods.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public final class CommonUtils {

	/**
	 * Logger isn't used a lot, so kept null until needed.
	 * {@link #createLogger()}
	 */
	private static Logger log;

	private CommonUtils() {

	}

	/**
	 * Gets string from InputStream. Uses Scanner with delimiter
	 * <code>\\A</code>.
	 * 
	 * @param inputStream
	 * @param charset
	 * @return String from streaml, or an empty string
	 * @throws IOException
	 * @since SNC 1.0
	 */
	public static String toString(InputStream inputStream, String charset)
			throws IOException {
		Scanner s = new Scanner(inputStream, charset);
		s.useDelimiter("\\A");
		String text = s.hasNext() ? s.next() : "";
		s.close();
		return text;
	}

	/**
	 * Trims all whitespace off including special characters and non breaking
	 * space. Sometimes {@link String#trim()} doesn't do the job.
	 * 
	 * @param str
	 *            text to trim
	 * @return trimmed string
	 * @since SNC 1.0
	 */
	public static String trim(String str) {
		int count = str.length();
		int len = str.length();
		int st = 0;
		int off = 0;
		char[] val = str.toCharArray();

		while ((st < len) && (isWhiteSpace(val[off + st])))
			st++;
		while ((st < len) && (isWhiteSpace(val[off + len - 1])))
			len--;

		return ((st > 0) || (len < count)) ? str.substring(st, len) : str;
	}

	/**
	 * Trims string for character.
	 * 
	 * @param str
	 *            text to trim
	 * @param ch
	 *            trim character
	 * @return trimmed string
	 * @since SNC 1.0
	 */
	public static String trim(String str, char ch) {
		int count = str.length();
		int len = str.length();
		int st = 0;
		int off = 0;
		char[] val = str.toCharArray();

		while ((st < len) && (val[off + st] <= ch)) {
			st++;
		}
		while ((st < len) && (val[off + len - 1] <= ch)) {
			len--;
		}

		return ((st > 0) || (len < count)) ? str.substring(st, len) : str;
	}

	/**
	 * Trims string with first and last characters.
	 * 
	 * @param str
	 *            text to trim
	 * @param chF
	 *            first trim character
	 * @param chL
	 *            last trim character
	 * @return trimmed string
	 * @since SNC 1.0
	 */
	public static String trim(String str, char chF, char chL) {
		int count = str.length();
		int len = str.length();
		int st = 0;
		int off = 0;
		char[] val = str.toCharArray();

		while ((st < len) && (val[off + st] <= chF)) {
			st++;
		}
		while ((st < len) && (val[off + len - 1] <= chL)) {
			len--;
		}

		return ((st > 0) || (len < count)) ? str.substring(st, len) : str;
	}

	/**
	 * Checks if inputstream has the <code>contains</code> string.
	 * 
	 * @param input
	 *            inputstream to be checked
	 * @param charset
	 *            encoding charset
	 * @param contains
	 *            contains string
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @since SNC 1.0
	 */
	public static boolean streamingContains(InputStream input, String charset,
			String contains) throws IOException {
		BufferedReader in = null;
		int count = 0;
		char[] content = contains.toCharArray();
		try {
			in = new BufferedReader(new InputStreamReader(input, charset));

			int ic;
			while ((ic = in.read()) != -1) {
				if ((char) ic == content[count]) {
					count++;
					if (count == contains.length()) return true;
				} else
					count = 0;
			}

		} catch (UnsupportedEncodingException e) {
			createLogger();
			log.error("streamingContains UnsupportedEncodingException {}",
					e.getMessage());
			return false;
		} finally {
			closeQuietly(in);
		}

		return false;
	}

	/**
	 * Checks if inputstream contains a set of strings. Only one will be found.
	 * 
	 * @param input
	 *            inputstream to be checked
	 * @param charset
	 *            encoding charset
	 * @param contains
	 *            array of strings to check if inputstream contains
	 * @return index of found contains, or -1 if none
	 * @throws IOException
	 */
	public static int streamingContains(InputStream input, String charset,
			String... contains) throws IOException {
		BufferedReader in = null;
		int len = contains.length;
		int[] conLen = new int[len];
		int[] countArray = new int[len];
		char[][] contentArray = new char[len][];
		for (int i = 0; i < len; i++) {
			contentArray[i] = contains[i].toCharArray();
			conLen[i] = contains[i].length();
		}
		try {
			in = new BufferedReader(new InputStreamReader(input, charset));

			int ic;
			while ((ic = in.read()) != -1) {
				char c = (char) ic;
				for (int i = 0; i < len; i++) {
					if (c == contentArray[i][countArray[i]]) {
						countArray[i]++;
						if (countArray[i] == conLen[i]) return i;
					} else
						countArray[i] = 0;
				}
			}

		} catch (UnsupportedEncodingException e) {
			createLogger();
			log.error("streamingContains UnsupportedEncodingException {}",
					e.getMessage());
			return -1;
		} finally {
			closeQuietly(in);
		}

		return -1;
	}

	/**
	 * Get random through range.
	 * 
	 * @param rand
	 * @param min
	 * @param max
	 * @return random in range
	 * @since SNC 1.0
	 */
	public static int randomRange(Random rand, int min, int max) {
		return rand.nextInt(max - min + 1) + min;
	}

	/**
	 * Quietly closes <code>Closeable</code> instances and ignores exceptions.
	 * 
	 * @param closeable
	 *            to close
	 * @since SNC 1.0
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) try {
			closeable.close();
		} catch (IOException ignore) {

		}
	}

	private static boolean isWhiteSpace(char ch) {
		if ('\u00A0' == ch) return true;
		else if ('\u2007' == ch) return true;
		else if ('\u202F' == ch) return true;
		else if (Character.isWhitespace(ch)) return true;
		else
			return false;
	}

	private static void createLogger() {
		if (log == null) log = LoggerFactory.getLogger(CommonUtils.class);
	}
}