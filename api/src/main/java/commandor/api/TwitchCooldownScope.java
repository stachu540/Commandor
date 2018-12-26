package commandor.api;

public enum TwitchCooldownScope implements Command.Cooldown.Scope {
    USER("U:%d",""),
    CHANNEL("C:%d"," in this channel"),
    USER_CHANNEL("U:%d|C:%d", " in this channel"),
    GLOBAL("Global", " globally");

    private final String format;
    private final String errorSpecification;

    TwitchCooldownScope(String format, String errorSpecification) {
        this.format = format;
        this.errorSpecification = errorSpecification;
    }

    @Override
    public String getErrorMessage(int remaining) {
        return String.format("That command is on cooldown for %s more seconds%s!", remaining, errorSpecification);
    }

    @Override
    public String getFormatKey(String command, Object... args) {
        if(this.equals(GLOBAL))
            return command + "|" + format;
        else return command + "|" + String.format(format, args);
    }
}
