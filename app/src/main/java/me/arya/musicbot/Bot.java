/*
 * Copyright (C) 2021 Arya K. O.
 *
 * MusicBot* is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * MusicBot* is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with *MusicBot*. If not, see http://www.gnu.org/licenses/
 */

package me.arya.musicbot;

import me.arya.musicbot.database.SQLiteDataSource;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.EnumSet;

public class Bot {
    private Bot() throws LoginException, SQLException {
        SQLiteDataSource.getConnection();

        JDABuilder.createDefault(Config.get("token"))
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .disableCache(EnumSet.of(
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ACTIVITY,
                        CacheFlag.EMOTE
                ))
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(new Listener())
                .setActivity(Activity.watching(Config.get("prefix")))
                .build();
    }

    public static void main(String[] args) throws LoginException, SQLException {
        new Bot();
    }
}
