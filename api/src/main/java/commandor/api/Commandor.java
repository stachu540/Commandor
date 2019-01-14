package commandor.api;

import commandor.api.annotation.AnnotatedCommandCompiler;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public abstract class Commandor<EVENT, CHANNEL, SENDER, CLIENT, E extends CommandEvent<EVENT, CHANNEL, SENDER, CLIENT>> {
    private final AnnotatedCommandCompiler<EVENT, CHANNEL, SENDER, CLIENT, E> annotationCompiler;
    protected final Set<Command<EVENT, CHANNEL, SENDER, CLIENT, E>> commands = new LinkedHashSet<>();
    private final PrefixCache prefixCache;
    protected final CommandListener<EVENT, CHANNEL, SENDER, CLIENT, E> listener;

    private final Map<String, OffsetDateTime> cooldown = new LinkedHashMap<>();

    protected Commandor(
            PrefixCache prefixCache,
            CommandListener<EVENT, CHANNEL, SENDER, CLIENT, E> listener,
            AnnotatedCommandCompiler<EVENT, CHANNEL, SENDER, CLIENT, E> annotationCompiler,
            Collection<Object> commands
    ) {
        this.listener = listener;
        this.prefixCache = prefixCache;
        this.annotationCompiler = annotationCompiler;

        if (commands != null && commands.size() > 0) {
            commands.forEach(o -> {
                if (o instanceof Command) {
                    registerCommand((Command) o);
                } else {
                    registerAnnotatedCommand(o);
                }
            });
        }
    }

    public String getDefaultPrefix(long sourceId) {
        return prefixCache.getDefaultPrefix(sourceId);
    }

    public <C extends Command<EVENT, CHANNEL, SENDER, CLIENT, E>> Collection<C> getCommands() {
        return (Collection<C>) commands;
    }

    public void registerCommand(Command<EVENT, CHANNEL, SENDER, CLIENT, E> command) {
        this.commands.add(command);
    }

    public void registerAnnotatedCommand(Object command) {
        this.annotationCompiler.compile(command).forEach(this::registerCommand);
    }

    public void unregisterCommand(String name) {
        this.commands.removeIf(command -> command.isCommandFor(name));
    }

    public OffsetDateTime getCooldown(String name) {
        return this.cooldown.get(name);
    }

    public int getRemainingCooldowns(String name) {
        if (cooldown.containsKey(name)) {
            int time = (int) OffsetDateTime.now().until(cooldown.get(name), ChronoUnit.SECONDS);
            if (time <= 0) {
                cooldown.remove(name);
                return 0;
            }
            return time;
        }
        return 0;
    }

    public void applyCooldown(String key, int seconds) {
        cooldown.put(key, OffsetDateTime.now().plusSeconds(seconds));
    }

    public CommandListener<EVENT, CHANNEL, SENDER, CLIENT, E> getListener() {
        return listener;
    }

    @SuppressWarnings("rawtypes")
    public static abstract class Builder<EVENT, CHANNEL, SENDER, CLIENT, E extends CommandEvent<EVENT, CHANNEL, SENDER, CLIENT>> {
        protected String defaultPrefix = "!";
        protected final Set<Object> commands = new LinkedHashSet<>();
        protected CommandListener<EVENT, CHANNEL, SENDER, CLIENT, E> listener;
        protected AnnotatedCommandCompiler<EVENT, CHANNEL, SENDER, CLIENT, E> compiler;

        protected Builder() {}

        public Builder<EVENT, CHANNEL, SENDER, CLIENT, E> setDefaultPrefix(String defaultPrefix) {
            this.defaultPrefix = defaultPrefix;
            return this;
        }

        public Builder<EVENT, CHANNEL, SENDER, CLIENT, E> setListener(CommandListener<EVENT, CHANNEL, SENDER, CLIENT, E> listener) {
            this.listener = listener;
            return this;
        }

        public Builder<EVENT, CHANNEL, SENDER, CLIENT, E> setAnnotatedCommandCompiler(AnnotatedCommandCompiler<EVENT, CHANNEL, SENDER, CLIENT, E> compiler) {
            this.compiler = compiler;
            return this;
        }

        public Builder<EVENT, CHANNEL, SENDER, CLIENT, E> addCommands(Object... commands) {
            this.commands.addAll(Arrays.asList(commands));
            return this;
        }

        public abstract Commandor<EVENT, CHANNEL, SENDER, CLIENT, E> build();
    }
}
