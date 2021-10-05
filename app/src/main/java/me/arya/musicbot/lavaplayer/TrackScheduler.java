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

    private AudioTrack getNextTrack() {
        AudioTrack nextTrackInQueue = this.queue.peek();
        if (nextTrackInQueue == null && this.queue.size() > 1 && queueLoop) {
            final List<AudioTrack> audioTracks = this.queue.stream().toList();
            this.queue.clear();
            this.queue.addAll(audioTracks);
        }
        if (!queueLoop) nextTrackInQueue = this.queue.poll();
        return nextTrackInQueue;
    }

    public void nextTrack() {
        AudioTrack nextTrackInQueue = getNextTrack();
        this.player.startTrack(nextTrackInQueue.makeClone(), false);
    }

    public void jumpToTrack(int trackIndex) {
        BlockingQueue<AudioTrack> skippedTracks = new LinkedBlockingQueue<>();
        this.queue.drainTo(skippedTracks, trackIndex);
        this.queue.addAll(skippedTracks);
        this.player.startTrack(getNextTrack(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
