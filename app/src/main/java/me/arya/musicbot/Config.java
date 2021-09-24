package me.arya.musicbot;

import io.github.cdimascio.dotenv.DotEnvException;
import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    private static Object dotenv;

    public Config() {
        try {
            dotenv = Dotenv.load();
        } catch DotEnvException e {
            dotenv = System.getenv();
        }
    }

    public static String get(String key) {
        return dotenv.get(key.toUpperCase());
    }
}
