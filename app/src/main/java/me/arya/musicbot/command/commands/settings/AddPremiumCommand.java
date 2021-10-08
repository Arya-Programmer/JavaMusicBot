package me.arya.musicbot.command.commands.settings;

import me.arya.musicbot.Config;
import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.EmbedMessage;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.database.SQLiteDataSource;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AddPremiumCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddPremiumCommand.class);

    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        if (!Config.get("owner_id").equals(ctx.getAuthor().getId())) {
            return;
        }

        final EmbedMessage embedMessage = new EmbedMessage();
        final List<Member> mentionedMembers = ctx.getMessage().getMentionedMembers();

        if (ctx.getArgs().isEmpty()) {
            embedMessage.setDescription("Master, please provide a user to add to premium");
            channel.sendMessage(embedMessage.build()).queue();
            return;
        }

        final User user;
        if (!mentionedMembers.isEmpty()) {
            user = mentionedMembers.get(0).getUser();
        } else {
            user = ctx.getJDA().getUserById(ctx.getArgs().get(0));
            if (user == null) {
                embedMessage.setDescription("Master, the provided Id is not valid");
                channel.sendMessage(embedMessage.build()).queue();
                return;
            }
        }

        final long userId = user.getIdLong();


        LOGGER.info(ctx.getArgs().get(0));
        LOGGER.info(mentionedMembers.toString());

        try {
            final Connection connection = SQLiteDataSource.getConnection();
            try (final PreparedStatement prepareStatement = connection
                    .prepareStatement("SELECT * FROM premium_users WHERE user_id = ?")) {
                prepareStatement.setString(1, String.valueOf(userId));

                try (final ResultSet resultSet = prepareStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        try (final PreparedStatement insertStatement = connection
                                .prepareStatement("INSERT INTO premium_users(user_id) VALUES(?)")) {
                            insertStatement.setString(1, String.valueOf(userId));
                            insertStatement.execute();

                            embedMessage.setDescription("User " + mentionedMembers.get(0).getAsMention() +
                                    " is now **VIP**, as you requested Master");
                        }
                    } else {
                        embedMessage.setDescription("User is already premium");
                    }
                }
                LOGGER.info(String.valueOf(prepareStatement.isClosed()));
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        channel.sendMessage(embedMessage.build()).queue();
    }

    @Override
    public String getName() {
        return "addpremium";
    }

    @Override
    public String getHelp() {
        return "Adds user to premium users";
    }
}
