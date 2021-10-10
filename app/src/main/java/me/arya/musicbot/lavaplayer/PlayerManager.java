/*
 * Copyright (C) 2021 Arya K. O.
 *
 * MusicBot* is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * MusicBot* is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with *MusicBot*. If not, see http://www.gnu.org/licenses/
 */

package me.arya.musicbot.lavaplayer;


import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.EmbedMessage;
import me.arya.musicbot.command.commands.music.PlayCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayCommand.class);

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(CommandContext ctx, TextChannel channel, String trackUrl, boolean playNow) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                LOGGER.info("Loading track");
                musicManager.scheduler.queue(track, playNow);

                final AudioTrackInfo info = track.getInfo();

                final EmbedMessage embedMessage = new EmbedMessage();
                final EmbedBuilder embedBuilder = embedMessage.setDescription(
                        String.format("Queued [%s](%s) [", info.title, info.uri) +
                                ctx.getAuthor().getAsMention() +
                                "]\n"
                );

                channel.sendMessage(embedBuilder.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                LOGGER.info("Loading playlist");
                if (playlist.getTracks().size() == 1 || playlist.isSearchResult()) {
                    LOGGER.info("One track loaded from link");
                    trackLoaded(playlist.getTracks().get(0));
                } else if (playlist.getSelectedTrack() != null || playlist.getTracks().size() > 0) {
                    LOGGER.info("Playlist loaded");
                    final List<AudioTrack> tracks = playlist.getTracks();

                    final EmbedMessage embedMessage = new EmbedMessage();
                    final EmbedBuilder embedBuilder = embedMessage.setDescription(
                            String.format("Queued %s tracks [", tracks.size()) +
                                    ctx.getAuthor().getAsMention() +
                                    "]\n"
                    );

                    for (final AudioTrack track : tracks) {
                        musicManager.scheduler.queue(track, false);
                    }

                    channel.sendMessage(embedBuilder.build()).queue();
                }
            }

            @Override
            public void noMatches() {
                LOGGER.info("No Match");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                LOGGER.info("Load failed");
            }
        });
    }

    public void loadAndPlayQuietly(CommandContext ctx, String trackUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(ctx.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                LOGGER.info("Loading track");
                musicManager.scheduler.queue(track, false);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // not going to use
            }

            @Override
            public void noMatches() {
                // not going to use
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                LOGGER.info("Load failed");
            }
        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}
