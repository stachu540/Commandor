package commandor.discord4j;

import commandor.api.Command;
import commandor.api.CommandEvent;
import commandor.api.DiscordCooldownScope;
import commandor.api.exception.CommandAccessException;
import commandor.api.exception.CommandException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public abstract class DiscordCommand extends Command<MessageReceivedEvent, IChannel, IUser, IDiscordClient, DiscordCommandEvent> {
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
            event.getApi().handleException(event, this, e);
        }
    }

    private String getCooldownKey(DiscordCommandEvent event) {
        IShard currentShard = event.getGuild().getShard();

        switch ((DiscordCooldownScope) getCooldown().getScope()) {
            case USER:         return getCooldown().getScope().getFormatKey(getName(), event.getSender().getLongID());
            case USER_GUILD:   return event.getGuild() !=null ?
                    getCooldown().getScope().getFormatKey(getName(), event.getSender().getLongID(), event.getGuild().getLongID()) :
                    DiscordCooldownScope.USER_CHANNEL.getFormatKey(getName(), event.getSender().getLongID(), event.getChannel().getLongID());
            case USER_CHANNEL: return getCooldown().getScope().getFormatKey(getName(), event.getSender().getLongID(),event.getChannel().getLongID());
            case GUILD:        return event.getGuild()!=null ?
                    getCooldown().getScope().getFormatKey(getName(), event.getGuild().getLongID()) :
                    DiscordCooldownScope.CHANNEL.getFormatKey(getName(), event.getChannel().getLongID());
            case CHANNEL:      return getCooldown().getScope().getFormatKey(getName(), event.getChannel().getLongID());
            case SHARD:        return currentShard != null ?
                    getCooldown().getScope().getFormatKey(getName(), currentShard.getInfo()[0]) :
                    DiscordCooldownScope.GLOBAL.getFormatKey(getName());
            case USER_SHARD:   return currentShard != null ?
                    getCooldown().getScope().getFormatKey(getName(), event.getSender().getLongID(), currentShard.getInfo()[0]) :
                    DiscordCooldownScope.USER.getFormatKey(getName(), event.getSender().getLongID());
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
