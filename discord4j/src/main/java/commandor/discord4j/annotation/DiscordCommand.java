package commandor.discord4j.annotation;

import commandor.api.DiscordCooldownScope;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscordCommand {
    String name();
    String[] alias() default {};
    String description() default "";
    Cooldown cooldown() default @Cooldown;
    Category category() default @Category;
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Cooldown {
        int time() default 0;
        DiscordCooldownScope scope() default DiscordCooldownScope.GLOBAL;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Category {
        String name() default "";
        Class<?> location() default Void.class;
    }
}
