package commandor.api;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Command<EVENT, CHANNEL, SENDER, CLIENT, E extends CommandEvent<EVENT, CHANNEL, SENDER, CLIENT>> implements Consumer<E> {
    private static final Cooldown DEFAULT_COOLDOWN = new Cooldown(0, null);

    private final String name;
    private final String[] alias;
    private final String description;
    private final Cooldown cooldown;
    private final Category category;

    protected Command(String name, String[] alias, String description, Cooldown cooldown, Category category) {
        this.name = name;
        this.alias = alias;
        this.description = description;
        this.cooldown = cooldown;
        this.category = category;
    }

    protected Command(String name, String description) {
        this(name, new String[0], description, DEFAULT_COOLDOWN, null);
    }

    protected Command(String name) {
        this(name, new String[0], null, DEFAULT_COOLDOWN, null);
    }

    public String getName() {
        return name;
    }

    public String[] getAlias() {
        return alias;
    }

    public String getDescription() {
        return description;
    }

    public Cooldown getCooldown() {
        return cooldown;
    }

    public Category getCategory() {
        return category;
    }

    public abstract void run(E event);

    public boolean isCommandFor(String name) {
        return this.name.equalsIgnoreCase(name) ||
                Arrays.stream(alias).anyMatch(s -> s.equalsIgnoreCase(name));
    }

    @SuppressWarnings("unchecked")
    protected void handleUsage(E event) {
        event.getApi().getListener().onUsageCommand(event, this);
    }

    public static class Cooldown {
        private final int time;
        private final Scope scope;

        public Cooldown(int time, Scope scope) {
            this.time = time;
            this.scope = scope;
        }

        public int getTime() {
            return time;
        }

        public Scope getScope() {
            return scope;
        }

        public interface Scope {
            String name();

            String getErrorMessage(int remaining);

            String getFormatKey(String command, Object... args);
        }
    }

    public static abstract class Category<EVENT, CHANNEL, SENDER, CLIENT, E extends CommandEvent<EVENT, CHANNEL, SENDER, CLIENT>> implements Predicate<E> {

        private final String name;
        private final String failResponse;
        private final Predicate<E> predicate;

        public Category(String name)
        {
            this.name = name;
            this.failResponse = null;
            this.predicate = null;
        }

        public Category(String name, Predicate<E> predicate)
        {
            this.name = name;
            this.failResponse = null;
            this.predicate = predicate;
        }

        public Category(String name, String failResponse, Predicate<E> predicate)
        {
            this.name = name;
            this.failResponse = failResponse;
            this.predicate = predicate;
        }

        public String getName() {
            return name;
        }

        public String getFailResponse() {
            return failResponse;
        }

        @Override
        public boolean test(E event)
        {
            return predicate==null || predicate.test(event);
        }

        @Override
        public boolean equals(Object obj)
        {
            if(!(obj instanceof Category))
                return false;
            Category other = (Category)obj;
            return Objects.equals(name, other.name) && Objects.equals(predicate, other.predicate) && Objects.equals(failResponse, other.failResponse);
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 17 * hash + Objects.hashCode(this.name);
            hash = 17 * hash + Objects.hashCode(this.failResponse);
            hash = 17 * hash + Objects.hashCode(this.predicate);
            return hash;
        }
    }
}
