package me.arya.musicbot.command.commands.playlist;

import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.database.SQLiteDataSource;
import me.arya.musicbot.lavaplayer.GuildMusicManager;
import me.arya.musicbot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SavePlaylistCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(SavePlaylistCommand.class);

    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

        final long userId = ctx.getAuthor().getIdLong();
        final String playlistName = ctx.getArgs().get(0);
        final List<String> trackTitles = musicManager.scheduler.loopingQueue.stream().map(
                (track) -> track.getInfo().title
        ).toList();

        try (final PreparedStatement prepareStatement = SQLiteDataSource
                .getConnection()
                .prepareStatement("SELECT * FROM user_playlists WHERE user_id = ? AND playlist_name = ?")) {
            prepareStatement.setString(1, String.valueOf(userId));
            prepareStatement.setString(2, playlistName);

            try (final ResultSet resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    final String playlistItems = resultSet.getString("items");
                } else {
                    try (final PreparedStatement insertStatement = SQLiteDataSource
                            .getConnection()
                            .prepareStatement("INSERT INTO user_playlists(user_id, playlist_name, items) " +
                                    "VALUES(?,?,?)")) {
                        insertStatement.setString(1, String.valueOf(userId));
                        insertStatement.setString(2, String.valueOf(playlistName));
                        insertStatement.setString(3, String.valueOf(trackTitles));

                        insertStatement.execute();
                    }
                }
            }

            LOGGER.info("Playlist name " + playlistName);
            LOGGER.info("Playlist items " + trackTitles);

            channel.sendMessage("hello").queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "saveplaylist";
    }

    @Override
    public String getHelp() {
        return "Saves the current queue under the specified name";
    }
}
