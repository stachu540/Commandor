package commandor.api;

public enum DiscordCooldownScope implements Command.Cooldown.Scope {
    USER("U:%d",""),
    CHANNEL("C:%d"," in this channel"),
    USER_CHANNEL("U:%d|C:%d", " in this channel"),
    GUILD("G:%d", " in this server"),
    USER_GUILD("U:%d|G:%d", " in this server"),
    SHARD("S:%d", " on this shard"),
    USER_SHARD("U:%d|S:%d", " on this shard"),
    GLOBAL("Global", " globally");

    private final String format;
    private final String errorSpecification;

    DiscordCooldownScope(String format, String errorSpecification) {
        this.format = format;
        this.errorSpecification = errorSpecification;
    }

    @Override
    public String getErrorMessage(int remaining) {
        if (remaining <= 0) {
            return null;
        } else {
            return String.format("That command is on cooldown for %s more seconds%s!", remaining, errorSpecification);
        }
    }

    @Override
    public String getFormatKey(String command, Object... args) {
        if(this.equals(GLOBAL))
            return command + "|" + format;
        else return command + "|" + String.format(format, args);
    }
}
