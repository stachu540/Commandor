package commandor.glitch;

import commandor.api.Command;
import commandor.api.CommandListener;
import glitch.chat.GlitchChat;
import glitch.chat.events.ChannelMessageEvent;
import glitch.chat.object.entities.ChannelEntity;
import glitch.chat.object.entities.ChannelUserEntity;
import reactor.core.publisher.Mono;

public class GlitchListener implements CommandListener<ChannelMessageEvent, Mono<ChannelEntity>, Mono<ChannelUserEntity>, GlitchChat, GlitchCommandEvent> {
    @Override
    public void onExceptionCommand(GlitchCommandEvent event, Command<ChannelMessageEvent, Mono<ChannelEntity>, Mono<ChannelUserEntity>, GlitchChat, GlitchCommandEvent> command, Throwable exception) {
        event.getChannel().flatMap(e -> e.sendMessage(Mono.just(exception.getClass().getSimpleName() + ": " + exception.getMessage()))).subscribe();
    }

    @Override
    public void onUsageCommand(GlitchCommandEvent event, Command<ChannelMessageEvent, Mono<ChannelEntity>, Mono<ChannelUserEntity>, GlitchChat, GlitchCommandEvent> command) {
        event.getChannel().zipWhen(e -> e.getData().map(c -> event.getApi().getDefaultPrefix(c.getId())))
                .flatMap(t -> {
                    StringBuilder sb = new StringBuilder(t.getT2() + command.getName());
                    if (command.getDescription() != null) {
                        sb.append(" - ").append(command.getDescription());
                    }

                    return t.getT1().sendMessage(Mono.just(sb.toString()));
                }).subscribe();
    }
}
