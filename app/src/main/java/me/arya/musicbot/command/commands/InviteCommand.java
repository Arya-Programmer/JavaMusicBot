/*
 * Copyright (C) 2021 Arya K. O.
 *
 * MusicBot* is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * MusicBot* is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with *MusicBot*. If not, see http://www.gnu.org/licenses/
 */

package me.arya.musicbot.command.commands;

import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.EmbedMessage;
import me.arya.musicbot.command.ICommand;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.InviteAction;

public class InviteCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final EmbedMessage embedMessage = new EmbedMessage();

        final String invite = channel.getJDA().getInviteUrl();
        embedMessage.setDescription(String.format("[Click here](%s)", invite));

        channel.sendMessage(embedMessage.build()).queue();
    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getHelp() {
        return "Creates instant invite";
    }
}
