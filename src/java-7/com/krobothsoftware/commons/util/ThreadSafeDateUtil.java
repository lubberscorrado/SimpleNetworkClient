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

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.util.resources.LocaleData;

public final class ThreadSafeDateUtil {

	private static Logger log;

	private static final Cache cache = new Cache();

	private static ConcurrentMap<Locale, int[]> cachedLocaleData;

	private static volatile boolean tried = false;

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

	@SuppressWarnings("unchecked")
	public static int[] getWeekData(Locale locale) {
		int[] data = { 1, 1 };

		if (cachedLocaleData == null && !tried) {
			synchronized (ThreadSafeDateUtil.class) {
				if (!tried) {
					tried = true;
					try {
						Field field = Calendar.class
								.getDeclaredField("cachedLocaleData");
						field.setAccessible(true);
						cachedLocaleData = (ConcurrentMap<Locale, int[]>) field
								.get(null);
						// check if LocaleData exists
						Class.forName("sun.util.resources.LocaleData");
					} catch (SecurityException | IllegalArgumentException
							| IllegalAccessException e) {
						getLogger().error("getWeekData", e);
					} catch (NoSuchFieldException | ClassNotFoundException
							| LinkageError e) {
						// this method won't work now
						cachedLocaleData = null;
					} finally {
						if (cachedLocaleData == null) return data;
					}
				} else if (cachedLocaleData == null) return data;
			}
		} else if (cachedLocaleData == null) {
			getLogger().error("Can not get week data");
			return data;
		}

		if (!cachedLocaleData.containsKey(locale)) {
			// from java.util.Calendar.setWeekCountData(Locale)
			ResourceBundle bundle = LocaleData.getCalendarData(locale);
			data[0] = Integer.parseInt(bundle.getString("firstDayOfWeek"));
			data[1] = Integer.parseInt(bundle
					.getString("minimalDaysInFirstWeek"));
			cachedLocaleData.putIfAbsent(locale, data);
		} else
			data = cachedLocaleData.get(locale);

		return data;

	}

	private static Logger getLogger() {
		return (log != null) ? log : (log = LoggerFactory
				.getLogger(CommonUtils.class));
	}

	static class Cache extends ThreadLocal<Map<String, SimpleDateFormat>> {

		@Override
		protected Map<String, SimpleDateFormat> initialValue() {
			return new HashMap<>();
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
