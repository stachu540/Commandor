package commandor.twitch4j.annotation;

import commandor.api.TwitchCooldownScope;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TwitchCommand {
    String name();
    String[] alias() default {};
    String description() default "";
    Cooldown cooldown() default @Cooldown;
    Category category() default @Category;
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Cooldown {
        int time() default 0;
        TwitchCooldownScope scope() default TwitchCooldownScope.GLOBAL;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Category {
        String name() default "";
        Class<?> location() default Void.class;
    }
}
