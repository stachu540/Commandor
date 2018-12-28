package commandor.jda;

import commandor.api.CommandListener;
import commandor.api.Commandor;
import commandor.api.PrefixCache;
import commandor.api.annotation.AnnotatedCommandCompiler;
import java.util.Collection;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

class DiscordCommandor extends Commandor<MessageReceivedEvent, MessageChannel, User, JDA, DiscordCommandEvent> implements EventListener {

    DiscordCommandor(PrefixCache prefixCache,
                     CommandListener<MessageReceivedEvent, MessageChannel, User, JDA, DiscordCommandEvent> listener,
                     AnnotatedCommandCompiler<MessageReceivedEvent, MessageChannel, User, JDA, DiscordCommandEvent> annotationCompiler,
                     Collection<Object> commands) {
        super(prefixCache, listener, annotationCompiler, commands);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReceivedEvent) {
            handle((MessageReceivedEvent) event);
        }
    }

    private void handle(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        String prefix = getDefaultPrefix(event.getGuild().getIdLong());

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
