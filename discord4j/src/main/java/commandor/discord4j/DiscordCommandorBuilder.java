package commandor.discord4j;

import commandor.api.Commandor;
import commandor.api.DefaultPrefixCache;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class DiscordCommandorBuilder extends Commandor.Builder<MessageReceivedEvent, IChannel, IUser, IDiscordClient, DiscordCommandEvent> {

    public DiscordCommandorBuilder() {
        super();
        this.compiler = new DiscordCommandCompiler();
        this.listener = new DiscordListener();
    }

    @Override
    public Commandor<MessageReceivedEvent, IChannel, IUser, IDiscordClient, DiscordCommandEvent> build() {
        return new DiscordCommandor(new DefaultPrefixCache(defaultPrefix), this.listener, this.compiler, commands);
    }
}
