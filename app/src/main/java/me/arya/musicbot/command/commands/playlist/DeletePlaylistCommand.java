/*
 * Copyright (C) 2021 Arya K. O.
 *
 * MusicBot* is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * MusicBot* is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with *MusicBot*. If not, see http://www.gnu.org/licenses/
 */

package me.arya.musicbot.command.commands.playlist;

import me.arya.musicbot.Config;
import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.EmbedMessage;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.database.SQLiteDataSource;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DeletePlaylistCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeletePlaylistCommand.class);

    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        final EmbedMessage embedMessage = new EmbedMessage();

        if (ctx.getArgs().isEmpty()) {
            embedMessage.setDescription("Correct usage: " + Config.get("prefix") +
                    "deleteplaylist <Playlist Name>");
            channel.sendMessage(embedMessage.build()).queue();
            return;
        }

        final long userId = ctx.getAuthor().getIdLong();
        final String playlistName = ctx.getArgs().get(0);

        try {
            final Connection connection = SQLiteDataSource.getConnection();
            try (final PreparedStatement prepareStatement = connection
                    .prepareStatement("SELECT * FROM user_playlists WHERE user_id = ? AND playlist_name = ?")) {
                prepareStatement.setString(1, String.valueOf(userId));
                prepareStatement.setString(2, playlistName);

                try (final ResultSet resultSet = prepareStatement.executeQuery()) {
                    if (resultSet.next()) {
                        try (final PreparedStatement deleteStatement = connection
                                .prepareStatement("DELETE FROM user_playlists " +
                                             "WHERE user_id = ? AND playlist_name = ?")) {
                            deleteStatement.setString(1, String.valueOf(userId));
                            deleteStatement.setString(2, playlistName);

                            deleteStatement.execute();
                            embedMessage.setDescription("Successfully deleted **"+playlistName+"** " +
                                    "["+ctx.getAuthor().getAsMention()+"]");
                        }
                    } else {
                        embedMessage.setDescription("No playlist found named **"+playlistName+"** " +
                                "["+ctx.getAuthor().getAsMention()+"]");
                    }
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            embedMessage.setDescription("Some error occurred! please try again.");
        }

        LOGGER.info("Deleted Playlist name " + playlistName);

        channel.sendMessage(embedMessage.build()).queue();
    }

    @Override
    public String getName() {
        return "deleteplaylist";
    }

    @Override
    public String getHelp() {
        return "Deletes a playlist with a given name";
    }

    @Override
    public boolean isPremium() {
        return true;
    }
}
