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

    public void loadAndPlay(CommandContext ctx, TextChannel channel, String trackUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);

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
                if (playlist.getTracks().size() == 1 || playlist.isSearchResult()) {
                    trackLoaded(playlist.getTracks().get(0));
                } else if (playlist.getSelectedTrack() != null) {
                    final List<AudioTrack> tracks = playlist.getTracks();

                    final EmbedMessage embedMessage = new EmbedMessage();
                    final EmbedBuilder embedBuilder = embedMessage.setDescription(
                            String.format("Queued %s tracks [", tracks.size()) +
                                    ctx.getAuthor().getAsMention() +
                                    "]\n"
                    );

                    for (final AudioTrack track : tracks) {
                        musicManager.scheduler.queue(track);
                    }

                    channel.sendMessage(embedBuilder.build()).queue();
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

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
