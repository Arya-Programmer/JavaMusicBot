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

import com.google.gson.Gson;
import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.database.SQLiteDataSource;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ListPlaylistCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListPlaylistCommand.class);

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        final MessageAction messageAction = channel.sendMessage("**User Playlists:**\n").append("```haskell\n");
        LOGGER.info("Listing playlists");

        final ArrayList<ArrayList<String>> playlists = getPlaylists(ctx);
        if (playlists.isEmpty()) {
            channel.sendMessage("```No saved playlists ;-;```").queue();
            return;
        }

        final int showingPlaylists = Math.min(playlists.size(), 10);

        for (int i=0; i < showingPlaylists; i++) {
            StringBuilder trackString = new StringBuilder();
            final ArrayList<String> playlist = playlists.get(i);

            final String playlistName = playlist.get(0);
            final String playlistItems = playlist.get(1);

            final Gson gson = new Gson();
            String[] items = gson.fromJson(playlistItems, String[].class);

            trackString.append(i + 1).append(") ");

            if (playlistName.length() < 40) {
                trackString.append(playlistName);
                trackString.append(" ".repeat((42 - playlistName.length())));
            } else {
                trackString.append(playlistName, 0, 39).append("   ");
            }

            trackString.append(items.length).append(" tracks\n");

            messageAction.append(trackString.toString());
        }

        messageAction.append("\n    ");
        if (playlists.size() > showingPlaylists) {
            messageAction.append(String.valueOf(playlists.size() - showingPlaylists))
                    .append(" more playlists(s)");
        } else {
            messageAction.append("   This is the end of the queue!");
        }



        messageAction.append("```").queue();
    }

    private ArrayList<ArrayList<String>> getPlaylists(CommandContext ctx) {
        final long userId = ctx.getAuthor().getIdLong();

        try {
            final Connection connection = SQLiteDataSource.getConnection();
            try (final PreparedStatement prepareStatement = connection
                    .prepareStatement("SELECT playlist_name, items FROM user_playlists WHERE user_id = ?")) {
                prepareStatement.setString(1, String.valueOf(userId));

                try (final ResultSet resultSet = prepareStatement.executeQuery()) {
                    ArrayList<ArrayList<String>> resultList = new ArrayList<>();
                    while (resultSet.next()) {
                        ArrayList<String> result = new ArrayList<>();
                        String column1 = resultSet.getString(1);
                        String column2 = resultSet.getString(2);
                        result.add(column1);
                        result.add(column2);
                        resultList.add(result);
                    }
                    connection.close();
                    return resultList;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getName() {
        return "listplaylist";
    }

    @Override
    public String getHelp() {
        return "Shows all personal playlists or tracks inside the specified playlist";
    }
}
