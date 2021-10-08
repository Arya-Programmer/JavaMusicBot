package me.arya.musicbot;

import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.EmbedMessage;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.command.commands.*;
import me.arya.musicbot.command.commands.music.*;
import me.arya.musicbot.command.commands.playlist.*;
import me.arya.musicbot.command.commands.settings.*;
import me.arya.musicbot.database.SQLiteDataSource;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<ICommand> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new PingCommand());
        addCommand(new HelpCommand(this));
        addCommand(new JoinCommand());
        addCommand(new PlayCommand());
        addCommand(new StopCommand());
        addCommand(new PauseCommand());
        addCommand(new SkipCommand());
        addCommand(new NowPlayingCommand());
        addCommand(new QueueCommand());
        addCommand(new LeaveCommand());
        addCommand(new LoopCommand());
        addCommand(new JumpCommand());
        addCommand(new ShuffleCommand());
        addCommand(new PlayNowCommand());
        addCommand(new InviteCommand());
        addCommand(new PrefixCommand());
        addCommand(new SavePlaylistCommand());
        addCommand(new PlayPlaylistCommand());
        addCommand(new AddPremiumCommand());
        addCommand(new PreviousCommand());
    }

    private void addCommand(ICommand cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound) {
            throw new IllegalArgumentException("A command with this name is already present");
        }

        commands.add(cmd);
    }

    public List<ICommand> getCommands() {
        return commands;
    }

    @Nullable
    public ICommand getCommand(String search) {
        String searchLower = search.toLowerCase();

        for (ICommand cmd : this.commands) {
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                return cmd;
            }
        }

        return null;
    }

    void handle(GuildMessageReceivedEvent event, String prefix) {
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(prefix), "")
                .split("\\s+");

        String invoke = split[0].toLowerCase();
        ICommand cmd = this.getCommand(invoke);

        if (cmd != null) {
            if (cmd.isPremium() && !isUserPremium(event.getAuthor())) {
                final EmbedMessage embedMessage = new EmbedMessage();
                embedMessage.setDescription("You need to be a premium user to be able to use this command\n" +
                        "Ask master to become a premium user");
                event.getChannel().sendMessage(embedMessage.build()).queue();
                return;
            }
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }
    }

    private boolean isUserPremium(User author) {
        try {
            final Connection connection = SQLiteDataSource.getConnection();
            try (final PreparedStatement prepareStatement = connection
                    .prepareStatement("SELECT * FROM premium_users WHERE user_id = ?")) {

                prepareStatement.setString(1, author.getId());

                try (final ResultSet resultSet = prepareStatement.executeQuery()) {
                    if (resultSet.next()) {
                        connection.close();
                        return true;
                    }
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
