/*
 * Copyright (C) 2021 Arya K. O.
 *
 * MusicBot* is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * MusicBot* is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with *MusicBot*. If not, see http://www.gnu.org/licenses/
 */

package me.arya.musicbot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.lavaplayer.GuildMusicManager;
import me.arya.musicbot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QueueCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueCommand.class);
    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("You need to be in a voice channel for this command to work").queue();
            return;
        }

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel()) && selfVoiceState.getChannel() != null) {
            channel.sendMessage("You need to be in the same voice channel as me for this to work").queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final ArrayList<AudioTrack> queue = musicManager.scheduler.loopingQueue;

        if (queue.isEmpty()) {
            channel.sendMessage("```The queue is empty ;-;```").queue();
            return;
        }

        final AudioTrack currentTrack = musicManager.audioPlayer.getPlayingTrack();
        final int indexCurrentTrack = queue.indexOf(currentTrack);

        final int tracksNumBeforeCurrent = Math.max(indexCurrentTrack-5, 0);
        final int tracksNumAfterCurrent = Math.min(indexCurrentTrack+(10-Math.min(indexCurrentTrack, 5)), queue.size());
        final MessageAction messageAction = channel.sendMessage("**Current Queue:**\n").append("```haskell\n");

        LOGGER.info(currentTrack != null ? currentTrack.getInfo().toString() : "Current track is null");
        LOGGER.info(queue.toString());
        LOGGER.info(String.format("\nBefore current -> %s\nCurrent -> %s\nAfter current -> %s", tracksNumBeforeCurrent, indexCurrentTrack, tracksNumAfterCurrent));

        for (int i=tracksNumBeforeCurrent; i < tracksNumAfterCurrent; i++) {
            StringBuilder trackString = new StringBuilder();
            final AudioTrack track = queue.get(i);
            final AudioTrackInfo info = track.getInfo();

            trackString.append(i + 1).append(") ");
            
            if (info.title.length() < 40) {
                trackString.append(info.title);
                trackString.append(" ".repeat((42 - info.title.length())));
            } else {
                trackString.append(info.title, 0, 39).append("   ");
            }

            if (i == indexCurrentTrack) {
                //noinspection ConstantConditions
                trackString.append(formatTime(currentTrack.getDuration() - currentTrack.getPosition()))
                        .append(" left\n");
                trackString = wrapCurrentTrack(trackString);

            } else trackString.append(formatTime(track.getDuration())).append("\n");

            messageAction.append(trackString.toString());
        }

        messageAction.append("\n    ");
        if (queue.size() > tracksNumAfterCurrent) {
            messageAction.append(String.valueOf(queue.size() - tracksNumAfterCurrent))
                    .append(" more track(s)");
        } else {
            messageAction.append("   This is the end of the queue!");
        }

        messageAction.append("```").queue();
    }

    private String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        if (hours > 0) return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private StringBuilder wrapCurrentTrack(StringBuilder trackStringBuilder) {
        trackStringBuilder.insert(0, "     ⬐ current track\n");
        return trackStringBuilder.append("     ⬑ current track\n");
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getHelp() {
        return "Shows the queued up songs";
    }

    @Override
    public List<String> getAliases() {
        return List.of("q", "songs");
    }
}
