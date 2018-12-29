package commandor.twitch4j;

import commandor.api.CommandEvent;
import me.philippheuer.twitch4j.TwitchClient;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.model.Channel;
import me.philippheuer.twitch4j.model.User;

public class TwitchCommandEvent implements CommandEvent<ChannelMessageEvent, Channel, User, TwitchClient> {
    private final ChannelMessageEvent event;
    private final TwitchCommandor api;
    private final String[] args;

    public TwitchCommandEvent(ChannelMessageEvent event, TwitchCommandor api, String[] args) {
        this.event = event;
        this.api = api;
        this.args = args;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public ChannelMessageEvent getEvent() {
        return event;
    }

    @Override
    public TwitchCommandor getApi() {
        return api;
    }

    @Override
    public User getSender() {
        return event.getUser();
    }

    @Override
    public Channel getChannel() {
        return event.getChannel();
    }

    @Override
    public TwitchClient getClient() {
        return event.getClient();
    }

    @Override
    public void respond(String content) {
        event.sendMessage(content);
    }
}
