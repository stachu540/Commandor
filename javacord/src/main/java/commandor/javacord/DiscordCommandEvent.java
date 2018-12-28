package commandor.javacord;

import commandor.api.CommandEvent;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class DiscordCommandEvent implements CommandEvent<MessageCreateEvent, TextChannel, User, DiscordApi> {
    private final MessageCreateEvent event;
    private final DiscordCommandor api;
    private final String[] args;

    DiscordCommandEvent(MessageCreateEvent event, DiscordCommandor api, String[] args) {
        this.event = event;
        this.api = api;
        this.args = args;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public MessageCreateEvent getEvent() {
        return event;
    }

    @Override
    public DiscordCommandor getApi() {
        return api;
    }

    @Override
    public User getSender() {
        return event.getMessageAuthor().asUser().orElse(null);
    }

    @Override
    public TextChannel getChannel() {
        return event.getChannel();
    }

    @Override
    public DiscordApi getClient() {
        return event.getApi();
    }

    public Server getGuild() {
        return event.getServer().orElse(null);
    }

    public void respond(String content) {
        getChannel().sendMessage(content);
    }

    public void respond(EmbedBuilder embed) {
        getChannel().sendMessage(embed);
    }

    public void respond(String content, EmbedBuilder embed) {
        getChannel().sendMessage(content, embed);
    }
}
