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
