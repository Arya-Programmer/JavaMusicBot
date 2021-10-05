package me.arya.musicbot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.lavaplayer.GuildMusicManager;
import me.arya.musicbot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueCommand.class);
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        if (queue.isEmpty()) {
            channel.sendMessage("```The queue is empty ;-;```").queue();
            return;
        }

        final List<AudioTrack> trackList = new ArrayList<>(queue);

        final AudioTrack currentTrack = musicManager.audioPlayer.getPlayingTrack();
        final int indexCurrentTrack = trackList.indexOf(currentTrack);

        final int tracksNumBeforeCurrent = Math.max(indexCurrentTrack-5, 0);
        final int tracksNumAfterCurrent = Math.min(indexCurrentTrack+5, queue.size());
        final MessageAction messageAction = channel.sendMessage("**Current Queue:**\n").append("```haskell\n");

        LOGGER.info(currentTrack.getInfo().toString());

        for (int i=tracksNumBeforeCurrent; i < tracksNumAfterCurrent; i++) {
            final AudioTrack track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();

            messageAction.append(String.valueOf(i+1))
                    .append(") ");

            if (info.title.length() < 40) {
                messageAction.append(info.title);
                for (int j=0; j < (42-info.title.length()); j++) messageAction.append(" ");
            } else {
                messageAction.append(info.title.substring(0, 39))
                        .append("   ");
            }
            messageAction.append(formatTime(track.getDuration()))
                    .append("\n");
        }

        messageAction.append("\n    ");
        if (trackList.size() > tracksNumAfterCurrent) {
            messageAction.append(String.valueOf(trackList.size() - tracksNumAfterCurrent))
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
