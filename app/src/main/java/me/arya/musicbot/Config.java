package me.arya.musicbot;

// Uncomment this to run app locally
// import io.github.cdimascio.dotenv.Dotenv;

public class Config {

//    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String key) {
        return System.getenv().get(key.toUpperCase());
    }
}
