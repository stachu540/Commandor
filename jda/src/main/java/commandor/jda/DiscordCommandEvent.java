package commandor.jda;

import commandor.api.CommandEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DiscordCommandEvent implements CommandEvent<MessageReceivedEvent, MessageChannel, User, JDA> {
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
    public User getSender() {
        return event.getAuthor();
    }

    @Override
    public MessageChannel getChannel() {
        return event.getChannel();
    }

    @Override
    public JDA getClient() {
        return event.getJDA();
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public void respond(String content) {
        getChannel().sendMessage(content).queue();
    }

    public void respond(MessageEmbed embed) {
        getChannel().sendMessage(embed).queue();
    }

    public void respond(String content, MessageEmbed embed) {
        getChannel().sendMessage(new MessageBuilder().setContent(content).setEmbed(embed).build()).queue();
    }
}
