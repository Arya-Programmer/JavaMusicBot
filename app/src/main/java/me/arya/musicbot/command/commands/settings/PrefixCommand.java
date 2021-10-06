package me.arya.musicbot.command.commands.settings;

import me.arya.musicbot.Config;
import me.arya.musicbot.Constants;
import me.arya.musicbot.command.CommandContext;
import me.arya.musicbot.command.EmbedMessage;
import me.arya.musicbot.command.ICommand;
import me.arya.musicbot.database.SQLiteDataSource;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrefixCommand implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrefixCommand.class);

    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        final EmbedMessage embedMessage = new EmbedMessage();
        long guildId = ctx.getGuild().getIdLong();

        String prefix = Config.get("prefix");

        if (ctx.getArgs().isEmpty()) {
            prefix = getPrefix(guildId, prefix);

            embedMessage.setDescription("Current **Prefix** is `"+ prefix +"`");
        } else {
            prefix = ctx.getArgs().get(0);
            updatePrefix(guildId, prefix);

            embedMessage.setDescription("Setting **Prefix** to `" + prefix + "`");
        }

        channel.sendMessage(embedMessage.build()).queue();
    }

    private String getPrefix(long guildId, String prefix) {
        try (final PreparedStatement prepareStatement = SQLiteDataSource
                .getConnection()
                .prepareStatement("SELECT prefix FROM guild_settings WHERE guild_id = ?")) {

            prepareStatement.setString(1, String.valueOf(guildId));

            try (final ResultSet resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    LOGGER.info(prefix);
                    prefix = resultSet.getString("prefix");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prefix;
    }

    private void updatePrefix(long guildId, String newPrefix) {
        Constants.PREFIXES.put(guildId, newPrefix);
        LOGGER.info(Constants.PREFIXES.toString());

        try (final PreparedStatement prepareStatement = SQLiteDataSource
                .getConnection()
                .prepareStatement("SELECT prefix FROM guild_settings WHERE guild_id = ?")) {

            prepareStatement.setString(1, String.valueOf(guildId));

            try (final PreparedStatement insertStatement = SQLiteDataSource
                    .getConnection()
                    .prepareStatement("UPDATE guild_settings SET prefix = ? WHERE guild_id = ?")) {
                insertStatement.setString(1, newPrefix);
                insertStatement.setString(2, String.valueOf(guildId));
                LOGGER.info("New prefix is "+newPrefix);

                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "prefix";
    }

    @Override
    public String getHelp() {
        return "Changes the current prefix";
    }
}
