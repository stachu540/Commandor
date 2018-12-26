package commandor.discord4j;

import commandor.api.CommandListener;
import commandor.api.Commandor;
import commandor.api.PrefixCache;
import commandor.api.annotation.AnnotatedCommandCompiler;
import java.util.Collection;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

class DiscordCommandor extends Commandor<MessageReceivedEvent, IChannel, IUser, IDiscordClient, DiscordCommandEvent> implements IListener<MessageReceivedEvent> {

    DiscordCommandor(PrefixCache prefixCache,
                     CommandListener<MessageReceivedEvent, IChannel, IUser, IDiscordClient, DiscordCommandEvent> listener,
                     AnnotatedCommandCompiler<MessageReceivedEvent, IChannel, IUser, IDiscordClient, DiscordCommandEvent> annotationCompiler,
                     Collection<Object> commands) {
        super(prefixCache, listener, annotationCompiler, commands);
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        String content = event.getMessage().getContent();
        String prefix = getDefaultPrefix(event.getGuild().getLongID());

        if (content.startsWith(prefix)) {
            String command = content.split(" ")[0].substring(prefix.length());
            String[] args = content.substring(prefix.length() + command.length()).trim().split(" ");

            commands.stream().filter(c -> c.isCommandFor(command))
                    .findFirst().ifPresent(c -> {
                        DiscordCommandEvent e = new DiscordCommandEvent(event, this, args);
                        listener.onCommand(e, c);
                        c.accept(e);
                    });
        } else {
            listener.onNonCommandMessage(event);
        }
    }
}
