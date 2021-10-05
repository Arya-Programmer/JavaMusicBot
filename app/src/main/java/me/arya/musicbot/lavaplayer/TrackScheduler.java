package me.arya.musicbot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer player;
    public BlockingQueue<AudioTrack> queue;
    public boolean repeating = false;
    public boolean queueLoop = false;
    private BlockingQueue<AudioTrack> loopingQueue;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            if (this.repeating) {
                this.player.startTrack(track.makeClone(), false);
                return;
            }
            //noinspection ResultOfMethodCallIgnored
            this.queue.offer(track);
        }
    }

    public void nextTrack() {
        AudioTrack nextTrackInQueue = this.queue.peek();
        if (nextTrackInQueue == null && this.queue.size() > 1 && queueLoop) {
            final List<AudioTrack> audioTracks = this.queue.stream().toList();
            this.queue.clear();
            this.queue.addAll(audioTracks);
        }
        this.player.startTrack(queueLoop ? nextTrackInQueue.makeClone() : this.queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
