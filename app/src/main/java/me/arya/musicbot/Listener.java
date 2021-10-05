package me.arya.musicbot;


import me.arya.musicbot.lavaplayer.GuildMusicManager;
import me.arya.musicbot.lavaplayer.PlayerManager;
import me.duncte123.botcommons.BotCommons;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;


public class Listener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager manager = new CommandManager();

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String prefix = Config.get("prefix");
        String raw = event.getMessage().getContentRaw();

        if (raw.equalsIgnoreCase(prefix+"shutdown")
                && event.getAuthor().getId().equals(Config.get("owner_id"))) {
            LOGGER.info("Shutting Down");
            event.getJDA().shutdown();
            BotCommons.shutdown(event.getJDA());

            return;
        }

        if (raw.startsWith(prefix)) {
            manager.handle(event);
        }
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        if (!event.getMember().getId().equals(event.getJDA().getSelfUser().getId())) {
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        musicManager.scheduler.repeating = false;
        musicManager.scheduler.shuffle = false;
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.loopingQueue.clear();
        musicManager.audioPlayer.stopTrack();
    }
}
