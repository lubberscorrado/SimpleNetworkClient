/*
 * Copyright 2013 Kroboth Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.krobothsoftware.commons.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommonUtils {

	private static Logger log;

	private CommonUtils() {

	}

	public static String toString(InputStream inputStream, String charset)
			throws IOException {
		try (Scanner s = new Scanner(inputStream, charset)) {
			s.useDelimiter("\\A");
			String text = s.hasNext() ? s.next() : "";
			return text;
		}
	}

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

	public static boolean streamingContains(InputStream input, String charset,
			String contains) throws IOException {
		int count = 0;
		char[] content = contains.toCharArray();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				input, charset))) {

			int ic;
			while ((ic = in.read()) != -1) {
				if ((char) ic == content[count]) {
					count++;
					if (count == contains.length()) return true;
				} else
					count = 0;
			}

		} catch (UnsupportedEncodingException e) {
			getLogger().error(
					"streamingContains UnsupportedEncodingException {}",
					e.getMessage());
			return false;
		}

		return false;
	}

	public static int streamingContains(InputStream input, String charset,
			String... contains) throws IOException {
		int len = contains.length;
		int[] conLen = new int[len];
		int[] countArray = new int[len];
		char[][] contentArray = new char[len][];
		for (int i = 0; i < len; i++) {
			contentArray[i] = contains[i].toCharArray();
			conLen[i] = contains[i].length();
		}
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				input, charset))) {

			int ic;
			while ((ic = in.read()) != -1) {
				for (int i = 0; i < len; i++) {
					if ((char) ic == contentArray[i][countArray[i]]) {
						countArray[i]++;
						if (countArray[i] == conLen[i]) return i;
					} else
						countArray[i] = 0;
				}
			}

		} catch (UnsupportedEncodingException e) {
			getLogger().error(
					"streamingContains UnsupportedEncodingException {}",
					e.getMessage());
			return -1;
		}

		return -1;
	}

	public static String streamingSubString(InputStream input, String charset,
			String beginIndex, String endIndex) throws IOException {
		StringBuilder sb;
		int count = 0;
		int len = beginIndex.length();
		char[] content = beginIndex.toCharArray();
		boolean found = false;

		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				input, charset))) {

			int ic;
			// find beginIndex
			while ((ic = in.read()) != -1) {
				if ((char) ic == content[count]) {
					count++;
					if (count == len) {
						found = true;
						break;
					}
				} else
					count = 0;
			}
			if (found) {
				sb = new StringBuilder();
				count = 0;
				len = endIndex.length();
				content = endIndex.toCharArray();
				// find endIndex
				while ((ic = in.read()) != -1) {
					char c = (char) ic;
					sb.append(c);
					if (c == content[count]) {
						count++;
						if (count == len) {
							return sb.substring(0, sb.length() - len);
						}
					} else
						count = 0;
				}
			}

		} catch (UnsupportedEncodingException e) {
			getLogger().error(
					"streamingContains UnsupportedEncodingException {}",
					e.getMessage());
		}

		return null;

	}

	public static int randomRange(Random rand, int min, int max) {
		return rand.nextInt(max - min + 1) + min;
	}

	public static void closeQuietly(AutoCloseable closeable) {
		if (closeable != null) try {
			closeable.close();
		} catch (Exception e) {
			// ignore exception
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

	private static Logger getLogger() {
		return (log != null) ? log : (log = LoggerFactory
				.getLogger(CommonUtils.class));
	}
}
