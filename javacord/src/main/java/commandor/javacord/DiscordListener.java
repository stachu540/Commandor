package commandor.javacord;

import commandor.api.Command;
import commandor.api.CommandListener;
import java.awt.Color;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class DiscordListener implements CommandListener<MessageCreateEvent, TextChannel, User, DiscordApi, DiscordCommandEvent> {
    @Override
    public void onExceptionCommand(DiscordCommandEvent event, Command command, Throwable exception) {
        event.getChannel().sendMessage(new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(exception.getClass().getSimpleName())
                .setDescription(exception.getMessage()));
    }

    @Override
    public void onUsageCommand(DiscordCommandEvent event, Command command) {
        String defaultPrefix = event.getApi().getDefaultPrefix(event.getGuild().getId());

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle(defaultPrefix + command.getName());

        if (command.getDescription() != null) {
            embed.setDescription(command.getDescription());
        }

        if (command.getCategory() != null) {
            embed.addField("Category", command.getCategory().getName());
        }

        if (command.getAlias().length > 0) {
            embed.addField("Aliases",
                    Arrays.stream(command.getAlias())
                            .collect(Collectors.joining("\n" + defaultPrefix,
                                    defaultPrefix, "")));
        }

        if (command.getCooldown().getScope() != null) {
            embed.addField("Cooldown", command.getCooldown().getScope().name().toLowerCase() + ": "
                    + command.getCooldown().getTime() + " seconds");
        }

        event.getChannel().sendMessage(embed);
    }
}
