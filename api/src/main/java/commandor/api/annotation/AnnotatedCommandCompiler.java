package commandor.api.annotation;

import commandor.api.Command;
import commandor.api.CommandEvent;
import java.util.List;

@FunctionalInterface
public interface AnnotatedCommandCompiler<EVENT, CHANNEL, SENDER, CLIENT, E extends CommandEvent<EVENT, CHANNEL, SENDER, CLIENT>> {
    List<Command<EVENT, CHANNEL, SENDER, CLIENT, E>> compile(Object o);
}
