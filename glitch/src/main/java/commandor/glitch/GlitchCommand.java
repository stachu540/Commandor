package commandor.glitch;

import commandor.api.Command;
import commandor.api.TwitchCooldownScope;
import commandor.api.exception.CommandAccessException;
import commandor.api.exception.CommandException;
import glitch.chat.GlitchChat;
import glitch.chat.events.ChannelMessageEvent;
import glitch.chat.object.entities.ChannelEntity;
import glitch.chat.object.entities.ChannelUserEntity;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

public abstract class GlitchCommand extends Command<ChannelMessageEvent, Mono<ChannelEntity>, Mono<ChannelUserEntity>, GlitchChat, GlitchCommandEvent> {
    protected GlitchCommand(String name, String[] alias, String description, Cooldown cooldown, Category category) {
        super(name, alias, description, cooldown, category);
    }

    protected GlitchCommand(String name, String description) {
        super(name, description);
    }

    protected GlitchCommand(String name) {
        super(name);
    }

    @Override
    public void accept(GlitchCommandEvent event) {
        event.getChannel().zipWhen(ChannelEntity::getData)
                .doOnError(t -> event.getApi().getListener().onExceptionCommand(event, this, t))
                .zipWith(getCooldownKey(event), (t, key) -> Tuples.of(t.getT1(), t.getT2(), key))
                .handle((t3, sink) -> {
                   if (getCategory() != null && !getCategory().test(event)) {
                       sink.error(new CommandAccessException(getCategory().getFailResponse()));
                   }

                   if (getCooldown().getTime() > 0) {
                       int remaining = event.getApi().getRemainingCooldowns(t3.getT3());
                       if (remaining > 0) {
                           String err = getCooldownException(remaining);

                           if (err != null) {
                               sink.error(new CommandException(err));
                           }
                       } else event.getApi().applyCooldown(t3.getT3(), getCooldown().getTime());
                   }
                   run(event);
                   event.getApi().getListener().onCompletedCommand(event, this);
                    sink.complete();
                }).subscribe();
    }

    private Mono<String> getCooldownKey(GlitchCommandEvent event) {
        return event.getSender().flatMap(ChannelUserEntity::getData)
                .zipWith(event.getChannel().flatMap(ChannelEntity::getData))
                .map(t -> {
                    switch ((TwitchCooldownScope) getCooldown().getScope()) {
                        case USER:         return getCooldown().getScope().getFormatKey(getName(), t.getT1().getId());
                        case USER_CHANNEL: return getCooldown().getScope().getFormatKey(getName(), t.getT1().getId(), t.getT2().getId());
                        case CHANNEL:      return getCooldown().getScope().getFormatKey(getName(), t.getT2().getId());
                        case GLOBAL:       return getCooldown().getScope().getFormatKey(getName());
                        default:           return "";
                    }
                });
    }

    private String getCooldownException(int remaining) {
        if (remaining <= 0) {
            return null;
        } else {
            return getCooldown().getScope().getErrorMessage(remaining);
        }
    }

    public static class Builder {
        private String name;
        private String[] alias = null;
        private String description = null;
        private Cooldown cooldown = null;
        private Category category = null;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAlias(String... alias) {
            this.alias = Arrays.copyOf(alias, alias.length);
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setCooldown(Cooldown cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        public Builder setCooldown(int time, Cooldown.Scope scope) {
            return setCooldown(new Cooldown(time, scope));
        }

        public Builder setCategory(Category category) {
            this.category = category;
            return this;
        }

        public GlitchCommand build(Consumer<GlitchCommandEvent> ec) {
            return new GlitchCommand(
                    Objects.requireNonNull(name, "Required command name"),
                    alias, description, cooldown, category
            ) {
                @Override
                public void run(GlitchCommandEvent event) {
                    ec.accept(event);
                }
            };
        }
    }
}
