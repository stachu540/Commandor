package commandor.jda;

import commandor.api.Command;
import commandor.api.DiscordCooldownScope;
import commandor.api.exception.CommandAccessException;
import commandor.api.exception.CommandException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class DiscordCommand extends Command<MessageReceivedEvent, MessageChannel, User, JDA, DiscordCommandEvent> {
    protected DiscordCommand(String name, String[] alias, String description, Cooldown cooldown, Category category) {
        super(name, alias, description, cooldown, category);
    }

    protected DiscordCommand(String name, String description) {
        super(name, description);
    }

    protected DiscordCommand(String name) {
        super(name);
    }

    @Override
    public void accept(DiscordCommandEvent event) {
        try {
            if (getCategory() != null && !getCategory().test(event)) {
                throw new CommandAccessException(getCategory().getFailResponse());
            }

            if (getCooldown().getTime() > 0) {
                String key = getCooldownKey(event);
                int remaining = event.getApi().getRemainingCooldowns(key);
                if (remaining > 0) {
                    String err = getCooldownException(remaining);
                    if (err != null) {
                        throw new CommandException(err);
                    }
                } else event.getApi().applyCooldown(key, getCooldown().getTime());
            }
        } catch (Throwable e) {
            event.getApi().getListener().onExceptionCommand(event, this, e);
        }
    }

    private String getCooldownKey(DiscordCommandEvent event) {
        long currentShard = (event.getGuild().getIdLong() >> 22) % event.getClient().getShardInfo().getShardTotal();

        switch ((DiscordCooldownScope) getCooldown().getScope()) {
            case USER:         return getCooldown().getScope().getFormatKey(getName(), event.getSender().getIdLong());
            case USER_GUILD:   return event.getGuild() !=null ?
                    getCooldown().getScope().getFormatKey(getName(), event.getSender().getIdLong(), event.getGuild().getIdLong()) :
                    DiscordCooldownScope.USER_CHANNEL.getFormatKey(getName(), event.getSender().getIdLong(), event.getChannel().getIdLong());
            case USER_CHANNEL: return getCooldown().getScope().getFormatKey(getName(), event.getSender().getIdLong(),event.getChannel().getIdLong());
            case GUILD:        return event.getGuild()!=null ?
                    getCooldown().getScope().getFormatKey(getName(), event.getGuild().getIdLong()) :
                    DiscordCooldownScope.CHANNEL.getFormatKey(getName(), event.getChannel().getIdLong());
            case CHANNEL:      return getCooldown().getScope().getFormatKey(getName(), event.getChannel().getIdLong());
            case SHARD:        return currentShard > 0 ?
                    getCooldown().getScope().getFormatKey(getName(), currentShard) :
                    DiscordCooldownScope.GLOBAL.getFormatKey(getName());
            case USER_SHARD:   return currentShard > 0 ?
                    getCooldown().getScope().getFormatKey(getName(), event.getSender().getIdLong(), currentShard) :
                    DiscordCooldownScope.USER.getFormatKey(getName(), event.getSender().getIdLong());
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

        public DiscordCommand build(Consumer<DiscordCommandEvent> ec) {
            return new DiscordCommand(
                    Objects.requireNonNull(name, "Required command name"),
                    alias, description, cooldown, category
            ) {
                @Override
                public void run(DiscordCommandEvent event) {
                    ec.accept(event);
                }
            };
        }
    }
}
