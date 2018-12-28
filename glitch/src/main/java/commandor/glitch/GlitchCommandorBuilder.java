package commandor.glitch;

import commandor.api.Commandor;
import commandor.api.DefaultPrefixCache;
import glitch.chat.GlitchChat;
import glitch.chat.events.ChannelMessageEvent;
import glitch.chat.object.entities.ChannelEntity;
import glitch.chat.object.entities.ChannelUserEntity;
import reactor.core.publisher.Mono;

public class GlitchCommandorBuilder extends Commandor.Builder<ChannelMessageEvent, Mono<ChannelEntity>, Mono<ChannelUserEntity>, GlitchChat, GlitchCommandEvent>{

    public GlitchCommandorBuilder() {
        super();
        this.compiler = new GlitchCommandCompiler();
        this.listener = new GlitchListener();
    }

    @Override
    public GlitchCommandor build() {
        return new GlitchCommandor(new DefaultPrefixCache(defaultPrefix), listener, compiler, commands);
    }
}
