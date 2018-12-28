package commandor.glitch;

import commandor.api.Command;
import commandor.api.annotation.AnnotatedCommandCompiler;
import glitch.chat.GlitchChat;
import glitch.chat.events.ChannelMessageEvent;
import glitch.chat.object.entities.ChannelEntity;
import glitch.chat.object.entities.ChannelUserEntity;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class GlitchCommandCompiler implements AnnotatedCommandCompiler<ChannelMessageEvent, Mono<ChannelEntity>, Mono<ChannelUserEntity>, GlitchChat, GlitchCommandEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedCommandCompiler.class);

    @Override
    public List<Command<ChannelMessageEvent, Mono<ChannelEntity>, Mono<ChannelUserEntity>, GlitchChat, GlitchCommandEvent>> compile(Object object) {
        return Arrays.stream(object.getClass().getDeclaredMethods())
                .filter(method ->
                        method.isAnnotationPresent(commandor.glitch.annotation.GlitchCommand.class) &&
                                !(method.getParameterTypes().length > 1) &&
                                method.getParameterTypes()[0] == GlitchCommandEvent.class)
                .map(method -> compileMethod(object, method))
                .collect(Collectors.toList());
    }

    private GlitchCommand compileMethod(Object object, Method method) {
        commandor.glitch.annotation.GlitchCommand property = method.getAnnotation(commandor.glitch.annotation.GlitchCommand.class);

        GlitchCommand.Builder builder = new GlitchCommand.Builder()
                .setName(property.name())
                .setCooldown(new Command.Cooldown(property.cooldown().time(), property.cooldown().scope()));

        if (property.alias().length > 0) {
            builder.setAlias(property.alias());
        }

        if (!property.description().equals("")) {
            builder.setDescription(property.description());
        }

        if(property.category().location() != Void.class) {
            commandor.glitch.annotation.GlitchCommand.Category category = property.category();

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
