package commandor.discord4j;

import commandor.api.Command;
import commandor.api.CommandListener;
import java.awt.Color;
import java.util.Arrays;
import java.util.stream.Collectors;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class DiscordListener implements CommandListener<MessageReceivedEvent, IChannel, IUser, IDiscordClient, DiscordCommandEvent> {
    @Override
    public void onExceptionCommand(DiscordCommandEvent event, Command command, Throwable exception) {
        event.getChannel().sendMessage(new EmbedBuilder()
                .withColor(Color.RED)
                .withTitle(exception.getClass().getSimpleName())
                .withDescription(exception.getMessage())
                .build());
    }

    @Override
    public void onUsageCommand(DiscordCommandEvent event, Command command) {
        String defaultPrefix = event.getApi().getDefaultPrefix(event.getEvent().getGuild().getLongID());

        EmbedBuilder embed = new EmbedBuilder()
                .withColor(Color.ORANGE)
                .withTitle(defaultPrefix + command.getName());

        if (command.getDescription() != null) {
            embed.withDescription(command.getDescription());
        }

        if (command.getCategory() != null) {
            embed.appendField("Category", command.getCategory().getName(), false);
        }

        if (command.getAlias().length > 0) {
            embed.appendField("Aliases",
                    Arrays.stream(command.getAlias())
                            .collect(Collectors.joining("\n" + defaultPrefix,
                                    defaultPrefix, "")), false);
        }

        if (command.getCooldown().getScope() != null) {
            embed.appendField("Cooldown", command.getCooldown().getScope().name().toLowerCase() + ": "
                    + command.getCooldown().getTime() + " seconds", false);
        }

        event.getChannel().sendMessage(embed.build());
    }
}
