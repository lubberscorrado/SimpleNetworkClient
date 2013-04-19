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

import com.fasterxml.jackson.core.JsonParseException;
import com.krobothsoftware.commons.parse.HandlerJson;
import com.krobothsoftware.commons.parse.ParseException;
import com.krobothsoftware.snc.sen.psn.model.FriendStatus;
import com.krobothsoftware.snc.sen.psn.us.model.PsnFriendGamerProfile;
import com.krobothsoftware.snc.sen.psn.us.model.PsnGamerProfile;

/**
 * 
 * @author Kyle Kroboth
 * @since SEN-PSN-US 1.0
 */
public class HandlerJsonGamerProfile extends HandlerJson {
	private String psnId;
	private String aboutMe;
	private String avatar;
	private int bronze;
	private int silver;
	private int gold;
	private int platinum;
	private int level;
	private int progress;

	private String comment;
	private FriendStatus status;
	private String title;
	private String titleStatus;
	private String lastSeen;

	private boolean failed;

	public HandlerJsonGamerProfile() {
		status = FriendStatus.ONLINE;
	}

	@Override
	public void parse() throws JsonParseException, IOException, ParseException {
		try {
			super.parse();
		} catch (JsonParseException e) {
			// check if unexpected character is '<' or begin html tag. Error
			// messages are in html which will cause problems in json parser
			String msg = e.getMessage();
			if (msg.contains("'<'")) {
				failed = true;
			} else
				throw e;
		}
	}

	@Override
	public void tokenText(String name) throws ParseException,
			JsonParseException, IOException {
		super.tokenText(name);
		if (name.equals("aboutMe")) {
			aboutMe = jsonParser.getText();
		} else if (name.equals("avatar")) {
			avatar = jsonParser.getText();
		} else if (name.equals("bronze")) {
			bronze = Integer.parseInt(jsonParser.getText());
		} else if (name.equals("silver")) {
			silver = Integer.parseInt(jsonParser.getText());
		} else if (name.equals("gold")) {
			gold = Integer.parseInt(jsonParser.getText());
		} else if (name.equals("platinum")) {
			platinum = Integer.parseInt(jsonParser.getText());
		} else if (name.equals("level")) {
			level = Integer.parseInt(jsonParser.getText());
		} else if (name.equals("progress")) {
			progress = Integer.parseInt(jsonParser.getText());
		} else if (name.equals("userName")) {
			psnId = jsonParser.getText();
		} else if (name.equals("comment")) {
			comment = jsonParser.getText();
			if (comment.isEmpty()) comment = null;
		} else if (name.equals("status")) {
			titleStatus = jsonParser.getText();
		} else if (name.equals("title")) {
			title = jsonParser.getText();
		} else if (name.equals("stamp")) {
			lastSeen = jsonParser.getText();
			status = FriendStatus.OFFLINE;
		}
	}

	public PsnGamerProfile getProfile() {
		return new PsnGamerProfile.Builder().setPsnId(psnId)
				.setAboutMe(aboutMe).setAvatar(avatar).setBronze(bronze)
				.setSilver(silver).setGold(gold).setPlatinum(platinum)
				.setLevel(level).setProgress(progress).build();
	}

	public PsnFriendGamerProfile getFriendProfile() {
		PsnGamerProfile.Builder builder = new PsnFriendGamerProfile.Builder()
				.setComment(comment).setStatus(status).setLastSeen(lastSeen)
				.setTitle(title).setTitleStatus(titleStatus).setPsnId(psnId)
				.setAboutMe(aboutMe).setAvatar(avatar).setBronze(bronze)
				.setSilver(silver).setGold(gold).setPlatinum(platinum)
				.setLevel(level).setProgress(progress);
		return (PsnFriendGamerProfile) builder.build();
	}

	public boolean failed() {
		return failed;
	}
}
