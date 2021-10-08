package me.arya.musicbot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;


public class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer player;
    public int currentIndex = 0;
    public boolean repeating = false;
    public boolean queueLoop = false;
    public boolean shuffle = false;
    public ArrayList<AudioTrack> loopingQueue;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.loopingQueue = new ArrayList<>();
    }

    public void queue(AudioTrack track, boolean playNow) {
        this.player.startTrack(track, !playNow);
        if (this.repeating) {
            this.player.startTrack(track.makeClone(), false);
            return;
        }

        if (playNow) {
            final ArrayList<AudioTrack> currentLoop = this.loopingQueue;
            this.loopingQueue.clear();
            this.loopingQueue.add(track);
            this.loopingQueue.addAll(currentLoop);
        } else {
            this.loopingQueue.add(track);
        }
    }

    private AudioTrack getNextTrack() {
        AudioTrack nextTrackInQueue = this.loopingQueue.get(currentIndex);
        if (nextTrackInQueue == null && !this.loopingQueue.isEmpty()) {
            if (queueLoop) {
                currentIndex = 0;
            }
        }

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
            currentIndex = trackIndex;
            nextTrack();
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            currentIndex = shuffle ? (int) (Math.random() * this.loopingQueue.size()) : ++currentIndex;
            nextTrack();
        }
    }
}
