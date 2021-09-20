package me.arya.musicbot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Bot {
    private Bot() throws LoginException {
        new JDABuilder()
            .setToken(Config.get("TOKEN"))
            .addEventListeners(new Listener())
            .setActivity(Activity.watching("."))
            .build();
    }

    public static void main(String[] args) throws LoginException {
        new Bot();
    }
}
