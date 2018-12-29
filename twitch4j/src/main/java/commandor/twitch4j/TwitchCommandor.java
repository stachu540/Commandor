package commandor.twitch4j;

import commandor.api.CommandListener;
import commandor.api.Commandor;
import commandor.api.PrefixCache;
import commandor.api.annotation.AnnotatedCommandCompiler;
import java.util.Collection;
import me.philippheuer.twitch4j.TwitchClient;
import me.philippheuer.twitch4j.events.IListener;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.model.Channel;
import me.philippheuer.twitch4j.model.User;


public class TwitchCommandor extends Commandor<ChannelMessageEvent, Channel, User, TwitchClient, TwitchCommandEvent> implements IListener<ChannelMessageEvent> {

    TwitchCommandor(PrefixCache prefixCache,
                    CommandListener<ChannelMessageEvent, Channel, User, TwitchClient, TwitchCommandEvent> listener,
                    AnnotatedCommandCompiler<ChannelMessageEvent, Channel, User, TwitchClient, TwitchCommandEvent> annotationCompiler,
                    Collection<Object> commands) {
        super(prefixCache, listener, annotationCompiler, commands);
    }

    @Override
    public void handle(ChannelMessageEvent event) {
        String prefix = getDefaultPrefix(event.getChannel().getId());
        if (event.getMessage().startsWith(prefix)) {
            String cmd = event.getMessage().split(" ")[0].substring(prefix.length());
            String[] args = event.getMessage().substring(prefix.length() + cmd.length()).trim()
                    .split(" ");
            commands.stream()
                    .filter(c -> c.isCommandFor(cmd))
                    .findFirst()
                    .ifPresent(c -> {
                        TwitchCommandEvent e = new TwitchCommandEvent(event, this, args);
                        listener.onCommand(e, c);
                        c.accept(e);
                    });
        } else {
            listener.onNonCommandMessage(event);
        }
    }
}
