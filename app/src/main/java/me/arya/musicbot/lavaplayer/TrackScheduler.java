package me.arya.musicbot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer player;
    public BlockingQueue<AudioTrack> queue;
    public boolean repeating = false;
    public boolean queueLoop = false;
    public boolean shuffle = false;
    public ArrayList<AudioTrack> loopingQueue;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.loopingQueue = new ArrayList<>();
    }

    public void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            if (this.repeating) {
                this.player.startTrack(track.makeClone(), false);
                return;
            }
            this.loopingQueue.add(track);
            //noinspection ResultOfMethodCallIgnored
            this.queue.offer(track);
        }
    }

    private AudioTrack getNextTrack() {
        AudioTrack nextTrackInQueue = this.queue.poll();
        if (nextTrackInQueue == null && this.loopingQueue.size() > 1) {
            if (queueLoop) {
                final AudioTrack audioTrack = loopingQueue.get(0);
                return audioTrack;
            }
            loopingQueue.clear();
        }
//        if (!queueLoop) nextTrackInQueue = this.queue.poll();
        return nextTrackInQueue;
    }

    public void nextTrack() {
        AudioTrack nextTrackInQueue = getNextTrack();
        try {
            this.player.startTrack(nextTrackInQueue, false);
        } catch (IllegalStateException e) {
            this.player.startTrack(nextTrackInQueue.makeClone(), false);
        }
    }

    public void jumpToTrack(int trackIndex) {
        if (trackIndex < this.loopingQueue.size()) {
            BlockingQueue<AudioTrack> skippedTracks = new LinkedBlockingQueue<>();
            this.queue.clear();
            this.queue.addAll(loopingQueue.subList(trackIndex-1, loopingQueue.size()));
            this.player.startTrack(loopingQueue.get(trackIndex-1), false);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
