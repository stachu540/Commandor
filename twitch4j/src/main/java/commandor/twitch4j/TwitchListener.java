package commandor.twitch4j;

import commandor.api.Command;
import commandor.api.CommandListener;
import me.philippheuer.twitch4j.TwitchClient;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.model.Channel;
import me.philippheuer.twitch4j.model.User;

public class TwitchListener implements CommandListener<ChannelMessageEvent, Channel, User, TwitchClient, TwitchCommandEvent> {
    @Override
    public void onExceptionCommand(TwitchCommandEvent event, Command<ChannelMessageEvent, Channel, User, TwitchClient, TwitchCommandEvent> command, Throwable exception) {
        event.respond(exception.getClass().getSimpleName() + ": " + exception.getMessage());
    }

    @Override
    public void onUsageCommand(TwitchCommandEvent event, Command<ChannelMessageEvent, Channel, User, TwitchClient, TwitchCommandEvent> command) {
        StringBuilder sb = new StringBuilder(event.getApi().getDefaultPrefix(event.getChannel().getId()) + command.getName());

        if (command.getDescription() != null) {
            sb.append(" - ").append(command.getDescription());
        }

        event.respond(sb.toString());
    }
}
