package commandor.jda;

import commandor.api.Command;
import commandor.api.CommandListener;
import java.awt.Color;
import java.util.Arrays;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DiscordListener implements CommandListener<MessageReceivedEvent, MessageChannel, User, JDA, DiscordCommandEvent> {
    @Override
    public void onExceptionCommand(DiscordCommandEvent event, Command command, Throwable exception) {
        event.getChannel().sendMessage(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(exception.getClass().getSimpleName())
                .setDescription(exception.getMessage())
                .build()).queue();
    }

    @Override
    public void onUsageCommand(DiscordCommandEvent event, Command command) {
        String defaultPrefix = event.getApi().getDefaultPrefix(event.getEvent().getGuild().getIdLong());

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle(defaultPrefix + command.getName());

        if (command.getDescription() != null) {
            embed.setDescription(command.getDescription());
        }

        if (command.getCategory() != null) {
            embed.addField("Category", command.getCategory().getName(), false);
        }

        if (command.getAlias().length > 0) {
            embed.addField("Aliases",
                    Arrays.stream(command.getAlias())
                            .collect(Collectors.joining("\n" + defaultPrefix,
                                    defaultPrefix, "")), false);
        }

        if (command.getCooldown().getScope() != null) {
            embed.addField("Cooldown", command.getCooldown().getScope().name().toLowerCase() + ": "
                    + command.getCooldown().getTime() + " seconds", false);
        }

        event.getChannel().sendMessage(embed.build()).queue();
    }
}
