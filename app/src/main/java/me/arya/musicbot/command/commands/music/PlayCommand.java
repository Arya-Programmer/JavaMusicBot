package me.arya.musicbot.command.commands.music;

import me.arya.musicbot.Config;
import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.lavaplayer.GuildMusicManager;
import me.arya.musicbot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class PlayCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayCommand.class);

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

        if (ctx.getArgs().isEmpty()) {
            if (musicManager.scheduler.player.isPaused() && !musicManager.scheduler.queue.isEmpty()) {
                musicManager.scheduler.player.setPaused(false);
                channel.sendMessage("Player resumed").queue();
                return;
            }
            channel.sendMessage("Correct usage is `" +
                    Config.get("prefix") +
                    "play <YoutubeURL>`").queue();
            return;
        }

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
        LOGGER.info(String.valueOf(memberVoiceState.getChannel()));
        LOGGER.info(String.valueOf(selfVoiceState.getChannel()));
        LOGGER.info(String.valueOf(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())));

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel()) && selfVoiceState.getChannel() != null) {
            channel.sendMessage("You need to be in the same voice channel as me for this to work").queue();
            return;
        }

        String link = String.join(" ", ctx.getArgs());

        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance()
                .loadAndPlay(channel, link);
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "Plays a song\n" +
                "Usage: `" +
                Config.get("prefix") +
                "play <YoutubeURL>`";
    }

    @Override
    public List<String> getAliases() {
        return List.of("sing", "p");
    }

    private boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
