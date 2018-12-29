package commandor.twitch4j;

import commandor.api.Command;
import commandor.api.annotation.AnnotatedCommandCompiler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.philippheuer.twitch4j.TwitchClient;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.model.Channel;
import me.philippheuer.twitch4j.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitchCommandCompiler implements AnnotatedCommandCompiler<ChannelMessageEvent, Channel, User, TwitchClient, TwitchCommandEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedCommandCompiler.class);

    @Override
    public List<Command<ChannelMessageEvent, Channel, User, TwitchClient, TwitchCommandEvent>> compile(Object object) {
        return Arrays.stream(object.getClass().getDeclaredMethods())
                .filter(method ->
                        method.isAnnotationPresent(commandor.twitch4j.annotation.TwitchCommand.class) &&
                                !(method.getParameterTypes().length > 1) &&
                                method.getParameterTypes()[0] == TwitchCommandEvent.class)
                .map(method -> compileMethod(object, method))
                .collect(Collectors.toList());
    }

    private TwitchCommand compileMethod(Object object, Method method) {
        commandor.twitch4j.annotation.TwitchCommand property = method.getAnnotation(commandor.twitch4j.annotation.TwitchCommand.class);

        TwitchCommand.Builder builder = new TwitchCommand.Builder()
                .setName(property.name())
                .setCooldown(new Command.Cooldown(property.cooldown().time(), property.cooldown().scope()));

        if (property.alias().length > 0) {
            builder.setAlias(property.alias());
        }

        if (!property.description().equals("")) {
            builder.setDescription(property.description());
        }

        if(property.category().location() != Void.class) {
            commandor.twitch4j.annotation.TwitchCommand.Category category = property.category();

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
