package commandor.javacord;

import commandor.api.CommandListener;
import commandor.api.Commandor;
import commandor.api.PrefixCache;
import commandor.api.annotation.AnnotatedCommandCompiler;
import java.util.Collection;
import java.util.function.Consumer;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

class DiscordCommandor extends Commandor<MessageCreateEvent, TextChannel, User, DiscordApi, DiscordCommandEvent> implements Consumer<MessageCreateEvent> {

    DiscordCommandor(PrefixCache prefixCache,
                     CommandListener<MessageCreateEvent, TextChannel, User, DiscordApi, DiscordCommandEvent> listener,
                     AnnotatedCommandCompiler<MessageCreateEvent, TextChannel, User, DiscordApi, DiscordCommandEvent> annotationCompiler,
                     Collection<Object> commands) {
        super(prefixCache, listener, annotationCompiler, commands);
    }

    @Override
    public void accept(MessageCreateEvent event) {
        String content = event.getMessage().getContent();
        String prefix = getDefaultPrefix(event.getServer().map(DiscordEntity::getId).orElse(0L));

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
