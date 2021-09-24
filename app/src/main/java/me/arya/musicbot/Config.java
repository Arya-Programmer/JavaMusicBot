package me.arya.musicbot;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String key) {
        key = key.toUpperCase();
        if (System.getenv().get(key) == null) {
            return dotenv.get(key);
        }
        return System.getenv().get(key);
    }
}
