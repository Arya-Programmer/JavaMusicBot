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

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    private static Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public static String get(String key) {
        final String s = key.toUpperCase();
        if (s.equals("TOKEN") && get("testing").equals("true")) {
            return dotenv.get("TOKEN_CANARY");
        }
        return dotenv.get(s, System.getenv().get(s) != null ? System.getenv().get(s) : "");
    }
}
