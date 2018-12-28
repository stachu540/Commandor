package commandor.glitch;

import commandor.api.CommandListener;
import commandor.api.Commandor;
import commandor.api.PrefixCache;
import commandor.api.annotation.AnnotatedCommandCompiler;
import glitch.chat.GlitchChat;
import glitch.chat.events.ChannelMessageEvent;
import glitch.chat.object.entities.AbstractEntity;
import glitch.chat.object.entities.ChannelEntity;
import glitch.chat.object.entities.ChannelUserEntity;
import java.util.Collection;
import java.util.function.Consumer;
import reactor.core.publisher.Mono;


public class GlitchCommandor extends Commandor<ChannelMessageEvent, Mono<ChannelEntity>, Mono<ChannelUserEntity>, GlitchChat, GlitchCommandEvent> implements Consumer<ChannelMessageEvent> {

    GlitchCommandor(PrefixCache prefixCache,
                    CommandListener<ChannelMessageEvent, Mono<ChannelEntity>, Mono<ChannelUserEntity>, GlitchChat, GlitchCommandEvent> listener,
                    AnnotatedCommandCompiler<ChannelMessageEvent, Mono<ChannelEntity>, Mono<ChannelUserEntity>, GlitchChat, GlitchCommandEvent> annotationCompiler,
                    Collection<Object> commands) {
        super(prefixCache, listener, annotationCompiler, commands);
    }

    @Override
    public void accept(ChannelMessageEvent event) {
        event.getChannel().zipWhen(AbstractEntity::getData)
                .subscribe(t -> {
                    String prefix = getDefaultPrefix(t.getT2().getId());
                    if (event.getContent().startsWith(prefix)) {
                        String cmd = event.getContent().split(" ")[0].substring(prefix.length());
                        String[] args = event.getContent().substring(prefix.length() + cmd.length()).trim()
                                .split(" ");
                        commands.stream()
                                .filter(c -> c.isCommandFor(cmd))
                                .findFirst()
                                .ifPresent(c -> {
                                    GlitchCommandEvent e = new GlitchCommandEvent(event, this, args);
                                    listener.onCommand(e, c);
                                    c.accept(e);
                                });
                    } else {
                        listener.onNonCommandMessage(event);
                    }
                });
    }
}
