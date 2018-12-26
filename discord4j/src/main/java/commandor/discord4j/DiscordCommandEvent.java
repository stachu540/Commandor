package commandor.discord4j;

import commandor.api.CommandEvent;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class DiscordCommandEvent implements CommandEvent<MessageReceivedEvent, IChannel, IUser, IDiscordClient> {
    private final MessageReceivedEvent event;
    private final DiscordCommandor api;
    private final String[] args;

    DiscordCommandEvent(MessageReceivedEvent event, DiscordCommandor api, String[] args) {
        this.event = event;
        this.api = api;
        this.args = args;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public MessageReceivedEvent getEvent() {
        return event;
    }

    @Override
    public DiscordCommandor getApi() {
        return api;
    }

    @Override
    public IUser getSender() {
        return event.getAuthor();
    }

    @Override
    public IChannel getChannel() {
        return event.getChannel();
    }

    @Override
    public IDiscordClient getClient() {
        return event.getClient();
    }

    public IGuild getGuild() {
        return event.getGuild();
    }

    public void respond(String content) {
        getChannel().sendMessage(content);
    }

    public void respond(EmbedObject embed) {
        getChannel().sendMessage(embed);
    }

    public void respond(String content, EmbedObject embed) {
        getChannel().sendMessage(content, embed);
    }
}
