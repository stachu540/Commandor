package commandor.glitch;

import commandor.api.CommandEvent;
import glitch.chat.GlitchChat;
import glitch.chat.events.ChannelMessageEvent;
import glitch.chat.object.entities.ChannelEntity;
import glitch.chat.object.entities.ChannelUserEntity;
import reactor.core.publisher.Mono;

public class GlitchCommandEvent implements CommandEvent<ChannelMessageEvent, Mono<ChannelEntity>, Mono<ChannelUserEntity>, GlitchChat> {
    private final ChannelMessageEvent event;
    private final GlitchCommandor api;
    private final String[] args;

    public GlitchCommandEvent(ChannelMessageEvent event, GlitchCommandor api, String[] args) {
        this.event = event;
        this.api = api;
        this.args = args;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public ChannelMessageEvent getEvent() {
        return event;
    }

    @Override
    public GlitchCommandor getApi() {
        return api;
    }

    @Override
    public Mono<ChannelUserEntity> getSender() {
        return event.getUser();
    }

    @Override
    public Mono<ChannelEntity> getChannel() {
        return event.getChannel();
    }

    @Override
    public GlitchChat getClient() {
        return event.getClient();
    }

    @Override
    public void respond(String content) {
        getChannel().flatMap(c -> c.sendMessage(Mono.just(content))).then();
    }
}
