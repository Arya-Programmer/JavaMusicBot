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
