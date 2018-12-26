package commandor.discord4j;

import commandor.api.Command;
import commandor.api.annotation.AnnotatedCommandCompiler;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class DiscordCommandCompiler implements AnnotatedCommandCompiler<MessageReceivedEvent, IChannel, IUser, IDiscordClient, DiscordCommandEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedCommandCompiler.class);

    @Override
    public List<Command<MessageReceivedEvent, IChannel, IUser, IDiscordClient, DiscordCommandEvent>> compile(Object object) {
        return Arrays.stream(object.getClass().getDeclaredMethods())
                .filter(method ->
                        method.isAnnotationPresent(commandor.discord4j.annotation.DiscordCommand.class) &&
                                !(method.getParameterTypes().length > 1) &&
                                method.getParameterTypes()[0] == DiscordCommandEvent.class)
                .map(method -> compileMethod(object, method))
                .collect(Collectors.toList());
    }

    private commandor.discord4j.DiscordCommand compileMethod(Object object, Method method) {
        commandor.discord4j.annotation.DiscordCommand property = method.getAnnotation(commandor.discord4j.annotation.DiscordCommand.class);

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
            commandor.discord4j.annotation.DiscordCommand.Category category = property.category();

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
