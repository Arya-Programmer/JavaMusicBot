package me.arya.musicbot;


import me.arya.musicbot.database.SQLiteDataSource;
import me.arya.musicbot.lavaplayer.GuildMusicManager;
import me.arya.musicbot.lavaplayer.PlayerManager;
import me.duncte123.botcommons.BotCommons;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Listener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager manager = new CommandManager();

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        User user = event.getAuthor();
        event.getJDA().getUserById(Config.get("owner_id")).openPrivateChannel().complete()
                .sendMessageFormat("%s: %s", user.getAsTag(), event.getMessage()).queue();

        if (user.isBot() || event.isWebhookMessage()) {
            return;
        }

        final long guildId = event.getGuild().getIdLong();
        String prefix = Constants.PREFIXES.computeIfAbsent(guildId, this::getPrefix);
        LOGGER.info("Current prefix is "+prefix);
        String raw = event.getMessage().getContentRaw();

        if (raw.equalsIgnoreCase(prefix+"shutdown")
                && event.getAuthor().getId().equals(Config.get("owner_id"))) {
            LOGGER.info("Shutting Down");
            event.getJDA().shutdown();
            BotCommons.shutdown(event.getJDA());

            return;
        }

        if (raw.startsWith(prefix)) {
            manager.handle(event, prefix);
        }
    }

    private String getPrefix(long guildId) {
        try {
            final Connection connection = SQLiteDataSource.getConnection();
            try (final PreparedStatement prepareStatement = connection
                    .prepareStatement("SELECT prefix FROM guild_settings WHERE guild_id = ?")) {

                prepareStatement.setString(1, String.valueOf(guildId));

                try (final ResultSet resultSet = prepareStatement.executeQuery()) {
                    if (resultSet.next()) {
                        final String prefix = resultSet.getString("prefix");
                        connection.close();
                        return prefix;
                    }
                }

                try (final PreparedStatement insertStatement = connection
                        .prepareStatement("INSERT INTO guild_settings(guild_id) VALUES(?)")) {
                    insertStatement.setString(1, String.valueOf(guildId));

                    insertStatement.execute();
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Config.get("prefix");
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        if (!event.getMember().getId().equals(event.getJDA().getSelfUser().getId())) {
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        musicManager.scheduler.repeating = false;
        musicManager.scheduler.shuffle = false;
        musicManager.scheduler.loopingQueue.clear();
        musicManager.audioPlayer.stopTrack();
    }
}
