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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import libcore.icu.LocaleData;

public final class ThreadSafeDateUtil {
	private static final Cache cache = new Cache();

	private ThreadSafeDateUtil() {

	}

	public static Date parse(String pattern, String source)
			throws ParseException {
		return cache.get(pattern).parse(source);
	}

	public static Date parse(String pattern, String source, int[] localeData)
			throws ParseException {
		SimpleDateFormat df = cache.get(pattern);
		Calendar calendar = df.getCalendar();
		// hold old values
		int fd = calendar.getFirstDayOfWeek();
		int md = calendar.getMinimalDaysInFirstWeek();
		calendar.setFirstDayOfWeek(localeData[0]);
		calendar.setMinimalDaysInFirstWeek(localeData[1]);
		try {
			return df.parse(source);
		} finally {
			calendar.setFirstDayOfWeek(fd);
			calendar.setMinimalDaysInFirstWeek(md);
		}
	}

	public static Date parse(String pattern, String source, TimeZone zone)
			throws ParseException {
		SimpleDateFormat df = cache.get(pattern);
		// hold old value
		TimeZone oldZone = df.getTimeZone();
		df.setTimeZone(zone);
		try {
			return df.parse(source);
		} finally {
			df.setTimeZone(oldZone);
		}

	}

	public static Date parse(String pattern, String source, TimeZone zone,
			int[] localeData) throws ParseException {
		SimpleDateFormat df = cache.get(pattern);
		Calendar calendar = df.getCalendar();
		// hold old values
		TimeZone oldZone = df.getTimeZone();
		int fd = calendar.getFirstDayOfWeek();
		int md = calendar.getMinimalDaysInFirstWeek();
		calendar.setFirstDayOfWeek(localeData[0]);
		calendar.setMinimalDaysInFirstWeek(localeData[1]);
		calendar.setTimeZone(zone);
		try {
			return df.parse(source);
		} finally {
			calendar.setFirstDayOfWeek(fd);
			calendar.setMinimalDaysInFirstWeek(md);
			calendar.setTimeZone(oldZone);
		}
	}

	public static String format(String pattern, Date date) {
		return cache.get(pattern).format(date);
	}

	public static String format(String pattern, Date date, int[] localeData) {
		SimpleDateFormat df = cache.get(pattern);
		Calendar calendar = df.getCalendar();
		// hold old values
		int fd = calendar.getFirstDayOfWeek();
		int md = calendar.getMinimalDaysInFirstWeek();
		calendar.setFirstDayOfWeek(localeData[0]);
		calendar.setMinimalDaysInFirstWeek(localeData[1]);
		try {
			return df.format(date);
		} finally {
			calendar.setFirstDayOfWeek(fd);
			calendar.setMinimalDaysInFirstWeek(md);
		}

	}

	public static String format(String pattern, Date date, TimeZone zone) {
		SimpleDateFormat df = cache.get(pattern);
		// hold old value
		TimeZone oldZone = df.getTimeZone();
		df.setTimeZone(zone);
		try {
			return df.format(date);
		} finally {
			df.setTimeZone(oldZone);
		}

	}

	public static String format(String pattern, Date date, TimeZone zone,
			int[] localeData) {
		SimpleDateFormat df = cache.get(pattern);
		Calendar calendar = df.getCalendar();
		// hold old values
		TimeZone oldZone = df.getTimeZone();
		int fd = calendar.getFirstDayOfWeek();
		int md = calendar.getMinimalDaysInFirstWeek();
		calendar.setFirstDayOfWeek(localeData[0]);
		calendar.setMinimalDaysInFirstWeek(localeData[1]);
		calendar.setTimeZone(zone);
		try {
			return df.format(date);
		} finally {
			calendar.setFirstDayOfWeek(fd);
			calendar.setMinimalDaysInFirstWeek(md);
			calendar.setTimeZone(oldZone);
		}
	}

	public static int[] getWeekData(Locale locale) {
		int[] data = new int[2];
		LocaleData localeData = LocaleData.get(locale);
		data[0] = localeData.firstDayOfWeek.intValue();
		data[1] = localeData.minimalDaysInFirstWeek.intValue();
		return data;
	}

	static class Cache extends ThreadLocal<Map<String, SimpleDateFormat>> {

		@Override
		protected Map<String, SimpleDateFormat> initialValue() {
			return new HashMap<String, SimpleDateFormat>();
		}

		public SimpleDateFormat get(String pattern) {
			Map<String, SimpleDateFormat> map = get();
			SimpleDateFormat df = map.get(pattern);
			if (df == null) {
				df = new SimpleDateFormat(pattern);
				map.put(pattern, df);
			}
			return df;
		}
	}

}
