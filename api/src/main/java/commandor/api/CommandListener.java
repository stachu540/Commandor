package commandor.api;

public interface CommandListener<EVENT, CHANNEL, SENDER, CLIENT, E extends CommandEvent<EVENT, CHANNEL, SENDER, CLIENT>> {
    default void onCommand(E event, Command<EVENT, CHANNEL, SENDER, CLIENT, E> command) {}

    default void onUsageCommand(E event, Command<EVENT, CHANNEL, SENDER, CLIENT, E> command) {}

    default void onCompletedCommand(E event, Command<EVENT, CHANNEL, SENDER, CLIENT, E> command) {}

    default void onExceptionCommand(E event, Command<EVENT, CHANNEL, SENDER, CLIENT, E> command, Throwable exception) {}

    default void onNonCommandMessage(EVENT event) {}
}
