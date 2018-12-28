package commandor.jda;

import commandor.api.Command;
import commandor.api.annotation.AnnotatedCommandCompiler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordCommandCompiler implements AnnotatedCommandCompiler<MessageReceivedEvent, MessageChannel, User, JDA, DiscordCommandEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedCommandCompiler.class);

    @Override
    public List<Command<MessageReceivedEvent, MessageChannel, User, JDA, DiscordCommandEvent>> compile(Object object) {
        return Arrays.stream(object.getClass().getDeclaredMethods())
                .filter(method ->
                        method.isAnnotationPresent(commandor.jda.annotation.DiscordCommand.class) &&
                                !(method.getParameterTypes().length > 1) &&
                                method.getParameterTypes()[0] == DiscordCommandEvent.class)
                .map(method -> compileMethod(object, method))
                .collect(Collectors.toList());
    }

    private DiscordCommand compileMethod(Object object, Method method) {
        commandor.jda.annotation.DiscordCommand property = method.getAnnotation(commandor.jda.annotation.DiscordCommand.class);

        DiscordCommand.Builder builder = new DiscordCommand.Builder()
                .setName(property.name())
                .setCooldown(new Command.Cooldown(property.cooldown().time(), property.cooldown().scope()));

        if (property.alias().length > 0) {
            builder.setAlias(property.alias());
        }

        if (!property.description().equals("")) {
            builder.setDescription(property.description());
        }

        if(property.category().location() != Void.class) {
            commandor.jda.annotation.DiscordCommand.Category category = property.category();

            Arrays.stream(category.location().getDeclaredFields())
                    .filter(f -> Modifier.isStatic(f.getModifiers()) && f.getType().equals(Command.Category.class))
                    .forEach(f -> {
                        if (category.name().equalsIgnoreCase(f.getName())) {
                            try {
                                builder.setCategory((Command.Category) f.get(null));
                            } catch (IllegalAccessException e) {
                                LOG.error("Encountered Exception ", e);
                            }
                        }
                    });
        }

        return builder.build(event -> {
            try {
                method.invoke(object, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOG.error("Encountered Exception ", e);
            }
        });
    }
}
