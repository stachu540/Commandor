package commandor.jda;

import commandor.api.Commandor;
import commandor.api.DefaultPrefixCache;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DiscordCommandorBuilder extends Commandor.Builder<MessageReceivedEvent, MessageChannel, User, JDA, DiscordCommandEvent> {

    public DiscordCommandorBuilder() {
        super();
        this.compiler = new DiscordCommandCompiler();
        this.listener = new DiscordListener();
    }

    @Override
    public Commandor<MessageReceivedEvent, MessageChannel, User, JDA, DiscordCommandEvent> build() {
        return new DiscordCommandor(new DefaultPrefixCache(defaultPrefix), this.listener, this.compiler, commands);
    }
}
