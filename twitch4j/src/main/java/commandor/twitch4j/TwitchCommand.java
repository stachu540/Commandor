package commandor.twitch4j;

import commandor.api.Command;
import commandor.api.TwitchCooldownScope;
import commandor.api.exception.CommandAccessException;
import commandor.api.exception.CommandException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import me.philippheuer.twitch4j.TwitchClient;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.model.Channel;
import me.philippheuer.twitch4j.model.User;

public abstract class TwitchCommand extends Command<ChannelMessageEvent, Channel, User, TwitchClient, TwitchCommandEvent> {
    protected TwitchCommand(String name, String[] alias, String description, Cooldown cooldown, Category category) {
        super(name, alias, description, cooldown, category);
    }

    protected TwitchCommand(String name, String description) {
        super(name, description);
    }

    protected TwitchCommand(String name) {
        super(name);
    }

    @Override
    public void accept(TwitchCommandEvent event) {

        if (getCategory() != null && !getCategory().test(event)) {
            throw new CommandAccessException(getCategory().getFailResponse());
        }

        if (getCooldown().getTime() > 0) {
            String cooldownKey = getCooldownKey(event);
            int remaining = event.getApi().getRemainingCooldowns(cooldownKey);

            if (remaining > 0) {
                String err = getCooldownException(remaining);

                if (err != null) {
                    throw new CommandException(err);
                }
            } else event.getApi().applyCooldown(cooldownKey, getCooldown().getTime());
        }

        run(event);
        event.getApi().getListener().onCompletedCommand(event, this);
    }

    private String getCooldownKey(TwitchCommandEvent event) {
        switch ((TwitchCooldownScope) getCooldown().getScope()) {
            case USER:         return getCooldown().getScope().getFormatKey(getName(), event.getSender().getId());
            case USER_CHANNEL: return getCooldown().getScope().getFormatKey(getName(), event.getSender().getId(), event.getChannel().getId());
            case CHANNEL:      return getCooldown().getScope().getFormatKey(getName(), event.getChannel().getId());
            case GLOBAL:       return getCooldown().getScope().getFormatKey(getName());
            default:           return "";
        }
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

        public TwitchCommand build(Consumer<TwitchCommandEvent> ec) {
            return new TwitchCommand(
                    Objects.requireNonNull(name, "Required command name"),
                    alias, description, cooldown, category
            ) {
                @Override
                public void run(TwitchCommandEvent event) {
                    ec.accept(event);
                }
            };
        }
    }
}
