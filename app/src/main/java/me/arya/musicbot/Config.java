package me.arya.musicbot;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String key) {
        key = key.toUpperCase();
        try {
            return dotenv.get(key);
        } catch (ExceptionInInitializerError e) {
            return System.getenv().get(key);
        }
    }
}
