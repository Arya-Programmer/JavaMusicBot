package me.arya.musicbot;

import io.github.cdimascio.dotenv.DotEnvException;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;

public class Config {

    private static Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public static String get(String key) {
        final String s = key.toUpperCase();
        return dotenv.get(s, System.getenv().get(s));
    }
}
