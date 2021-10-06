package me.arya.musicbot.command.commands.playlist;

import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.database.SQLiteDataSource;
import me.arya.musicbot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.TextChannel;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class PlayPlaylistCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayPlaylistCommand.class);

    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        final long userId = ctx.getAuthor().getIdLong();
        final String playlistName = ctx.getArgs().get(0);

        try (final PreparedStatement prepareStatement = SQLiteDataSource
                .getConnection()
                .prepareStatement("SELECT * FROM user_playlists WHERE user_id = ? AND playlist_name = ?")) {
            prepareStatement.setString(1, String.valueOf(userId));
            prepareStatement.setString(2, playlistName);

            try (final ResultSet resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    final String playlistItems = resultSet.getString("items");
                    final Gson gson = new Gson();
                    String[] items = gson.fromJson(playlistItems, String[].class);

                    LOGGER.info("Array of playlist items"+ Arrays.toString(items));
                    LOGGER.info("String of playlist items"+ playlistItems);
                    for (final String track : items) {
                        PlayerManager.getInstance()
                                .loadAndPlayQuietly(ctx, channel, track);
                    }
                } else {
                    //
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getName() {
        return "playplaylist";
    }

    @Override
    public String getHelp() {
        return "Plays the saved playlist";
    }
}
