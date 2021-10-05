package me.arya.musicbot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.arya.musicbot.Config;
import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.EmbedMessage;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.lavaplayer.GuildMusicManager;
import me.arya.musicbot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ShuffleCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShuffleCommand.class);

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inVoiceChannel()) {
            new JoinCommand().handle(ctx);
        }

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

        final ArrayList<AudioTrack> loopingQueue = musicManager.scheduler.loopingQueue;
        final boolean shuffle = musicManager.scheduler.shuffle;
        if (shuffle) {
            musicManager.scheduler.shuffle = !shuffle;

            musicManager.scheduler.queue.clear();
            musicManager.scheduler.queue.addAll(loopingQueue);
        } else {
            musicManager.scheduler.shuffle = !shuffle;
            Collections.shuffle(loopingQueue);

            musicManager.scheduler.queue.clear();
            musicManager.scheduler.queue.addAll(loopingQueue);
        }
        final EmbedMessage embedMessage = new EmbedMessage();

        embedMessage.setDescription(String.format("Shuffle mode has been **%s**", shuffle ? "enabled" : "disabled"));
        channel.sendMessage(embedMessage.build()).queue();
    }

    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public String getHelp() {
        return "Shuffles the current queue";
    }
}
