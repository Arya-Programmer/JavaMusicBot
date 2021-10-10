/*
 * Copyright (C) 2021 Arya K. O.
 *
 * MusicBot* is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * MusicBot* is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with *MusicBot*. If not, see http://www.gnu.org/licenses/
 */

package me.arya.musicbot.command.commands.music;

import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.EmbedMessage;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.lavaplayer.GuildMusicManager;
import me.arya.musicbot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class LoopCommand implements ICommand {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inVoiceChannel()) {
            channel.sendMessage("I need to be in a voice channel for this to work").queue();
            return;
        }

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("You need to be in a voice channel for this command to work").queue();
            return;
        }

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel()) && selfVoiceState.getChannel() != null) {
            channel.sendMessage("You need to be in the same voice channel as me for this to work").queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final EmbedMessage embedMessage = new EmbedMessage();

        final String repeatingOption = ctx.getArgs().isEmpty() ? "" : ctx.getArgs().get(0);

        if (repeatingOption.isEmpty() || repeatingOption.isBlank() || repeatingOption.equals("queue")) {
            boolean newQueueLoop = !musicManager.scheduler.queueLoop;
            musicManager.scheduler.queueLoop = newQueueLoop;

            embedMessage.setDescription(newQueueLoop ? "Now looping the **queue**." : "Looping is now **disabled**.");
        } else if (repeatingOption.equals("track")) {
            final boolean newRepeating = !musicManager.scheduler.repeating;
            musicManager.scheduler.repeating = newRepeating;

            embedMessage.setDescription(newRepeating ? "Now looping the **current track**." : "Looping is now **disabled**.");
        } else if (repeatingOption.equals("off")) {
            musicManager.scheduler.repeating = false;
            musicManager.scheduler.queueLoop = false;

            embedMessage.setDescription("Looping is now **disabled**.");
        }

        channel.sendMessage(embedMessage.build()).queue();
    }

    @Override
    public String getName() {
        return "loop";
    }

    @Override
    public String getHelp() {
        return "Repeats currently playing song";
    }

    @Override
    public List<String> getAliases() {
        return List.of("loop", "ql", "queueloop");
    }
}
