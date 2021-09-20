package me.arya.musicbot;

import net.dv8tion.jda.api.JDABuilder;

public class Bot {
    private Bot() {
        new JDABuilder()
            .setToken("ODg5MjQxNjgyMTM3MTk4NjYy.YUeY2g.0rpIej0TtcLnjGRGSDnDUy6ZV_k")
            .addEventListener(new Listener())
            .build();
    }

    public static void main(String[] args) {
        new Bot();
    }
}
