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

package com.krobothsoftware.snc.sen.psn.us.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.krobothsoftware.commons.parse.HandlerJson;
import com.krobothsoftware.commons.parse.ParseException;
import com.krobothsoftware.snc.sen.psn.us.model.PsnGamerProfile;
import com.krobothsoftware.snc.sen.psn.us.model.PsnGamerProfile.Builder;

/**
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN-US 1.0
 */
public class HandlerJsonFriendProfile extends HandlerJson {
	private final List<PsnGamerProfile> list;
	private final PsnGamerProfile.Builder builder;

	public HandlerJsonFriendProfile() {
		list = new ArrayList<PsnGamerProfile>();
		builder = new Builder();
	}

	public List<PsnGamerProfile> getProfileList() {
		return list;
	}

	@Override
	public void tokenText(String name) throws ParseException,
			JsonParseException, IOException {
		super.tokenText(name);

		if (name.equals("avatar")) {
			builder.setAvatar(jsonParser.getText());
		} else if (name.equals("bronze")) {
			builder.setBronze(Integer.parseInt(jsonParser.getText()));
		} else if (name.equals("silver")) {
			builder.setSilver(Integer.parseInt(jsonParser.getText()));
		} else if (name.equals("gold")) {
			builder.setGold(Integer.parseInt(jsonParser.getText()));
		} else if (name.equals("platinum")) {
			builder.setPlatinum(Integer.parseInt(jsonParser.getText()));
		} else if (name.equals("level")) {
			builder.setLevel(Integer.parseInt(jsonParser.getText()));
		} else if (name.equals("progress")) {
			builder.setProgress(Integer.parseInt(jsonParser.getText()));
		} else if (name.equals("name")) {
			builder.setPsnId(jsonParser.getText());
			list.add(builder.build());
		}
	}
}
