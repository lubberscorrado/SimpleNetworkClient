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

package com.krobothsoftware.snc.sen.psn.us.model;

import java.io.Serializable;
import java.util.StringTokenizer;

import com.krobothsoftware.snc.sen.OnlineId;

/**
 * 
 * Holds all personal Info in an US account. Not all data here will be filled,
 * depends on account. Relies on cookie <code>userinfo</code>.
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN-US 1.0
 */
public class PsnUser implements OnlineId, Serializable {
	private static final long serialVersionUID = 5112139106790377237L;
	private final String psnId;
	private final String dob;
	private final int age;
	private final String aboutMe;
	private final String email;
	private final String firstName;
	private final String lastName;
	private final String gender;
	private final String location;
	private final String city;
	private final String stateInitial;
	private final String countryInitial;
	private final String avatar;

	PsnUser(Builder builder) {
		psnId = builder.psnId;
		dob = builder.dob;
		age = builder.age;
		aboutMe = builder.aboutMe;
		email = builder.email;
		firstName = builder.firstName;
		lastName = builder.lastName;
		gender = builder.gender;
		location = builder.location;
		city = builder.city;
		stateInitial = builder.stateInitial;
		countryInitial = builder.countryInitial;
		avatar = builder.avatar;
	}

	/**
	 * Returns PsnId.
	 * 
	 * @since SEN-PSN-US 1.0
	 */
	@Override
	public String getOnlineId() {
		return psnId;
	}

	/**
	 * Gets Date of Birth. May not be entirely correct.
	 * 
	 * @return date of birth
	 * @since SEN-PSN-US 1.0
	 */
	public String getDob() {
		return dob;
	}

	/**
	 * Gets age of user.
	 * 
	 * @return age
	 * @since SEN-PSN-US 1.0
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Gets about me text.
	 * 
	 * @return about me text
	 * @since SEN-PSN-US 1.0
	 */
	public String getAboutMe() {
		return aboutMe;
	}

	/**
	 * gets email of user.
	 * 
	 * @return email
	 * @since SEN-PSN-US 1.0
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Gets first name of user.
	 * 
	 * @return first name
	 * @since SEN-PSN-US 1.0
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Gets last name of user.
	 * 
	 * @return last name
	 * @since SEN-PSN-US 1.0
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Gets gender of user.
	 * 
	 * @return Male or Female, of user
	 * @since SEN-PSN-US 1.0
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * Gets location of user.
	 * 
	 * @return location
	 * @since SEN-PSN-US 1.0
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Gets city of user.
	 * 
	 * @return city
	 * @since SEN-PSN-US 1.0
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Gets state initial of user.
	 * 
	 * @return state initial
	 * @since SEN-PSN-US 1.0
	 */
	public String getStateInitial() {
		return stateInitial;
	}

	/**
	 * Gets country initial of user.
	 * 
	 * @return country initial
	 * @since SEN-PSN-US 1.0
	 */
	public String getCountryInitial() {
		return countryInitial;
	}

	/**
	 * Gets avatar url of user.
	 * 
	 * @return avatar url
	 * @since SEN-PSN-US 1.0
	 */
	public String getAvatar() {
		return avatar;
	}

	/**
	 * Returns string in format "PsnUser [psnId='id']".
	 * 
	 * @since SEN-PSN-US 1.0
	 */
	@Override
	public String toString() {
		return String.format("PsnUser [psnId=%s]", psnId);
	}

	/**
	 * Returns a new PsnPersonalInfo Instance.
	 * 
	 * @param userinfo
	 *            the cookie data
	 * @return the personal info
	 * @since SEN-PSN-US 1.0
	 */
	public static PsnUser newInstance(String userinfo) {
		Builder builder = new Builder();
		StringTokenizer tokens = new StringTokenizer(userinfo, ",");
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			String name = token.split("=")[0];
			if ((name + "=").length() == token.length()) continue;
			String value = token.split("=")[1];

			if (name.equalsIgnoreCase("handle")) builder.setUserId(value);
			else if (name.equalsIgnoreCase("birthDate")) builder.setDob(value);
			else if (name.equalsIgnoreCase("aboutMe")) builder
					.setAboutMe(value);
			else if (name.equalsIgnoreCase("email")) builder.setEmail(value);
			else if (name.equalsIgnoreCase("firstName")) builder
					.setFirstName(value);
			else if (name.equalsIgnoreCase("age")) builder.setAge(Integer
					.parseInt(value));
			else if (name.equalsIgnoreCase("avatar")) builder.setAvatar(value);
			else if (name.equalsIgnoreCase("location")) builder
					.setLocation(value);
			else if (name.equalsIgnoreCase("gender")) builder.setGender(value
					.equals("M") ? "Male" : "Female");
			else if (name.equalsIgnoreCase("lastName")) builder
					.setLastName(value);
			else if (name.equalsIgnoreCase("city")) builder.setCity(value);
			else if (name.equalsIgnoreCase("state")) builder
					.setStateInitial(value);
			else if (name.equalsIgnoreCase("country")) builder
					.setCountryInitial(value);
		}

		return builder.build();
	}

	/**
	 * Builder for user info.
	 * 
	 * @author Kyle Kroboth
	 * @since SEN-PSN-US 1.0
	 */
	public static class Builder {
		String psnId;
		String dob;
		int age;
		String aboutMe;
		String email;
		String firstName;
		String lastName;
		String gender;
		String location;
		String city;
		String stateInitial;
		String countryInitial;
		String avatar;

		public Builder setUserId(String psnId) {
			this.psnId = psnId;
			return this;
		}

		public Builder setDob(String dob) {
			this.dob = dob;
			return this;
		}

		public Builder setAge(int age) {
			this.age = age;
			return this;
		}

		public Builder setAboutMe(String aboutMe) {
			this.aboutMe = aboutMe;
			return this;
		}

		public Builder setEmail(String email) {
			this.email = email;
			return this;
		}

		public Builder setFirstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder setLastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder setGender(String gender) {
			this.gender = gender;
			return this;
		}

		public Builder setLocation(String location) {
			this.location = location;
			return this;
		}

		public Builder setCity(String city) {
			this.city = city;
			return this;
		}

		public Builder setStateInitial(String stateInitial) {
			this.stateInitial = stateInitial;
			return this;
		}

		public Builder setCountryInitial(String countryInitial) {
			this.countryInitial = countryInitial;
			return this;
		}

		public Builder setAvatar(String avatar) {
			this.avatar = avatar;
			return this;
		}

		public PsnUser build() {
			return new PsnUser(this);
		}

	}

}
