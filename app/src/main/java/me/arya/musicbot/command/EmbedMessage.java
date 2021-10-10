/*
 * Copyright (C) 2021 Arya K. O.
 *
 * MusicBot* is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * MusicBot* is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with *MusicBot*. If not, see http://www.gnu.org/licenses/
 */

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
