package commandor.twitch4j;

import commandor.api.Commandor;
import commandor.api.DefaultPrefixCache;
import me.philippheuer.twitch4j.TwitchClient;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.model.Channel;
import me.philippheuer.twitch4j.model.User;

public class TwitchCommandorBuilder extends Commandor.Builder<ChannelMessageEvent, Channel, User, TwitchClient, TwitchCommandEvent>{

    public TwitchCommandorBuilder() {
        super();
        this.compiler = new TwitchCommandCompiler();
        this.listener = new TwitchListener();
    }

    @Override
    public TwitchCommandor build() {
        return new TwitchCommandor(new DefaultPrefixCache(defaultPrefix), listener, compiler, commands);
    }
}
