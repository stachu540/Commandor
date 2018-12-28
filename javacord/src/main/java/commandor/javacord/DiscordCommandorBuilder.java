package commandor.javacord;

import commandor.api.Commandor;
import commandor.api.DefaultPrefixCache;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class DiscordCommandorBuilder extends Commandor.Builder<MessageCreateEvent, TextChannel, User, DiscordApi, DiscordCommandEvent> {

    public DiscordCommandorBuilder() {
        super();
        this.compiler = new DiscordCommandCompiler();
        this.listener = new DiscordListener();
    }

    @Override
    public DiscordCommandor build() {
        return new DiscordCommandor(new DefaultPrefixCache(defaultPrefix), this.listener, this.compiler, commands);
    }
}
