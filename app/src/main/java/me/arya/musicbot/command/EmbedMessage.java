package me.arya.musicbot.command;

import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class EmbedMessage extends EmbedBuilder {
    private Color defaultColor = Color.red;

    public EmbedMessage() {
        this.setColor(defaultColor);
    }
}
