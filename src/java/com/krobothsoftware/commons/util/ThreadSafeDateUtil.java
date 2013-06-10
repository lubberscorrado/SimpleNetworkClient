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

/**
 * Thread safe {@link SimpleDateFormat} methods for parsing and formatting.
 * {@link ThreadLocal} holds a map of <code>String</code>(Date pattern) to
 * {@link SimpleDateFormat} instance.
 * 
 * <p>
 * Methods with <code>TimeZone</code> and <code>Locale</code> do not set values
 * permanently, but for each call. Only way to use <code>Locale</code> is
 * setting {@link Calendar#setFirstDayOfWeek(int)} and
 * {@link Calendar#setMinimalDaysInFirstWeek(int)}. Those values are retrieved
 * internally and need reflection or access to non-public classes.
 * {@link #getWeekData(Locale)} <i>should</i> get them.
 * </p>
 * 
 * @author Kyle Kroboth
 * @since 1.1.0
 */
public final class ThreadSafeDateUtil {

	/**
	 * Logger isn't used a lot, so kept null until needed. {@link #getLogger()}.
	 */
	private static Logger log;

	/**
	 * Thread Local HashMap.
	 */
	private static final Cache cache = new Cache();

	/**
	 * Static field in Calendar class.
	 */
	private static ConcurrentMap<Locale, int[]> cachedLocaleData;

	/**
	 * If reflection fails, don't want to keep on trying.
	 */
	private static volatile boolean tried = false;

	private ThreadSafeDateUtil() {

	}

	/**
	 * Thread safe {@link SimpleDateFormat#parse(String)}.
	 * 
	 * @param pattern
	 *            <code>SimpleDateFormat</code> pattern
	 * @param source
	 *            <code>String</code> date to parse
	 * @return A Date parsed from the string
	 * @throws ParseException
	 *             if the beginning of the specified string cannot be parsed.
	 * @since 1.1.0
	 */
	public static Date parse(String pattern, String source)
			throws ParseException {
		return cache.get(pattern).parse(source);
	}

	/**
	 * Thread safe {@link SimpleDateFormat#parse(String)}. Uses Locale in
	 * formatter. Use {@link #getWeekData(Locale)} to get data array.
	 * 
	 * @param pattern
	 *            <code>SimpleDateFormat</code> pattern
	 * @param source
	 *            <code>String</code> date to parse
	 * @param localeData
	 *            array of first day of week and minimal days in first week in
	 *            <code>Locale</code>
	 * @return A Date parsed from the string
	 * @throws ParseException
	 *             if the beginning of the specified string cannot be parsed.
	 * @since 1.1.0
	 */
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

	/**
	 * Thread safe {@link SimpleDateFormat#parse(String)}. Uses TimeZone in
	 * formatter.
	 * 
	 * @param pattern
	 *            <code>SimpleDateFormat</code> pattern
	 * @param source
	 *            <code>String</code> date to parse
	 * @param zone
	 *            <code>TimeZone</code> to use
	 * @return A Date parsed from the string
	 * @throws ParseException
	 *             if the beginning of the specified string cannot be parsed.
	 * @since 1.1.0
	 */
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

	/**
	 * Thread safe {@link SimpleDateFormat#parse(String)}. Uses TimeZone and
	 * Locale in formatter.
	 * 
	 * @param pattern
	 *            <code>SimpleDateFormat</code> pattern
	 * @param source
	 *            <code>String</code> date to parse
	 * @param zone
	 *            <code>TimeZone</code> to use
	 * @param localeData
	 *            array of first day of week and minimal days in first week in
	 *            <code>Locale</code>
	 * @return A Date parsed from the string
	 * @throws ParseException
	 *             if the beginning of the specified string cannot be parsed.
	 * @since 1.1.0
	 */
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

	/**
	 * Thread safe {@link SimpleDateFormat#format(Date)}.
	 * 
	 * @param pattern
	 *            <code>SimpleDateFormat</code> pattern
	 * @param date
	 *            the time value to be formatted into a time string
	 * @return the time value to be formatted into a time string.
	 * @since 1.1.0
	 */
	public static String format(String pattern, Date date) {
		return cache.get(pattern).format(date);
	}

	/**
	 * Thread safe {@link SimpleDateFormat#format(Date)}. Uses Locale in
	 * formatter. Use {@link #getWeekData(Locale)} to get data array.
	 * 
	 * @param pattern
	 *            <code>SimpleDateFormat</code> pattern
	 * @param date
	 *            the time value to be formatted into a time string
	 * @param localeData
	 *            array of first day of week and minimal days in first week in
	 *            <code>Locale</code>
	 * @return the time value to be formatted into a time string.
	 * @since 1.1.0
	 */
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

	/**
	 * Thread safe {@link SimpleDateFormat#format(Date)}. Uses TimeZone and
	 * Locale in formatter.
	 * 
	 * @param pattern
	 *            <code>SimpleDateFormat</code> pattern
	 * @param date
	 *            the time value to be formatted into a time string
	 * @param zone
	 *            <code>TimeZone</code> to use
	 * @return the time value to be formatted into a time string.
	 * @since 1.1.0
	 */
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

	/**
	 * Thread safe {@link SimpleDateFormat#format(Date)}. Uses TimeZone and
	 * Locale in formatter.
	 * 
	 * @param pattern
	 *            <code>SimpleDateFormat</code> pattern
	 * @param date
	 *            the time value to be formatted into a time string
	 * @param zone
	 *            <code>TimeZone</code> to use
	 * @param localeData
	 *            array of first day of week and minimal days in first week in
	 *            <code>Locale</code>
	 * @return the time value to be formatted into a time string.
	 * @since 1.1.0
	 */
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

	/**
	 * Retrieves Locale data regarding first day of week and minimal days in a
	 * week.
	 * 
	 * <p>
	 * <b>Java SE:</b> Uses reflection to get cached Map inside
	 * <i>Calendar.class</i>. If not found, will get <code>ResourceBundle</code>
	 * for Locale. Should not be a problem calling this method many times since
	 * getting the data is <i>O(1)</i>.
	 * </p>
	 * 
	 * <p>
	 * <b>Android:</b> Calls <i>libcore.icu.LocaleData.get(Locale)</i> through
	 * reflection and gets values. If performance is an issue, call this method
	 * once and store value or use <i>Android-Frameworks</i>.
	 * </p>
	 * 
	 * <p>
	 * <b>Android-Framework:</b> Calls same method above but without reflection.
	 * </p>
	 * 
	 * <p>
	 * A good idea is to retrieve the values and store them in an int array.
	 * That way there is no need to call this method every time, or ever.
	 * </p>
	 * 
	 * @param locale
	 *            to get calendar values
	 * @return array of week data for calendar. Will return {1, 1} if any errors
	 *         occur.
	 * @since 1.1.0
	 */
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
					} catch (IllegalArgumentException e) {
						getLogger().error("getWeekData", e.getMessage());
					} catch (IllegalAccessException e) {
						getLogger().error("getWeekData", e.getMessage());
					} catch (SecurityException e) {
						getLogger().error("getWeekData", e.getMessage());
					} catch (NoSuchFieldException e) {
						// this method won't work now
						cachedLocaleData = null;
					} catch (ClassNotFoundException e) {
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
