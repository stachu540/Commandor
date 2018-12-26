package commandor.api;

public interface CommandEvent<EVENT, CHANNEL, SENDER, CLIENT> {
    String[] getArgs();

    EVENT getEvent();
    Commandor getApi();
    SENDER getSender();
    CHANNEL getChannel();
    CLIENT getClient();

    void respond(String content);
}
