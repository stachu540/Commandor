# Commandor

## Usage

### Import

#### Gradle - Groovy DSL

```groovy
plugins {
    // Gradle < 5.0
  id "io.spring.dependency-management" version "1.0.6.RELEASE"
}

dependencyManagement {
     imports {
          mavenBom "com.github.stachu540.Commandor:Commandor-bom:master-SNAPSHOT"
     }
}

repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    // Gradle >= 5.0
    compile enforcedPlatform("com.github.stachu540.Commandor:Commandor-bom:master-SNAPSHOT")
    
    // Any Gradle version
    compile "com.github.stachu540.Commandor:Commandor-discordj4"
    compile "com.github.stachu540.Commandor:Commandor-glitch"
    // etc...
}
```

#### Gradle - Kotlin DSL

```kotlin
plugins {
    // Gradle < 5.0
  id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

dependencyManagement {
     imports {
          mavenBom("com.github.stachu540.Commandor:Commandor-bom:master-SNAPSHOT")
     }
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    // Gradle >= 5.0
    compile(enforcedPlatform("com.github.stachu540.Commandor:Commandor-bom:master-SNAPSHOT"))
    
    // Any Gradle version
    compile("com.github.stachu540.Commandor:Commandor-discordj4")
    compile("com.github.stachu540.Commandor:Commandor-glitch")
    // etc...
}
```

#### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.github.stachu540.Commandor</groupId>
            <artifactId>Commandor-bom</artifactId>
            <version>master-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
<dependencies>
    <dependency>
        <groupId>com.github.stachu540.Commandor</groupId>
        <artifactId>Commandor-discordj4</artifactId>
        <scope>compile</scope>
    </dependency>
    <dependency>
        <groupId>com.github.stachu540.Commandor</groupId>
        <artifactId>Commandor-glitch</artifactId>
        <scope>compile</scope>
    </dependency>
</dependencies>

```

### Get started

#### [Discord4J](https://github.com/Discord4J/Discord4J)

```java
public class Bot {
    public static void main(String[] args) {
        IDiscordClient client = new ClientBuilder()
            .withToken("<token>")
            .build();
        
        client.getDispatcher().registerListener(new DiscordCommandorBuilder()
            .setDefaultPrefix("c?")
            .addCommands(new SimpleCommand(), new AnnotatedCommands()) // you choose what register command
            .build());
    }
}
```

```java
public class Miscellaneous extends Category {
    public Miscellaneous() {
        super("Miscellaneous");
    }
}
```

```java
public class SimpleCommand extends DiscordCommand {
    public SimpleCommand() {
        super("ping", null, "Ping bot", new Cooldown(0, DiscordCooldownScope.SHARD), new Miscellaneous());
    }
    
    public void run(DiscordCommandEvent event) {
        event.response("Pong!");
    }
}
```

```java
public class AnnotatedCommands {
    
    @DiscordCommand(name = "pong", 
                    description = "Pong bot",
                    cooldown = @Cooldown(time = 0, scope = DiscordCooldownScope.GLOBAL), 
                    category = @Category(location = Miscellaneous.class))
    public void pong(DiscordCommandEvent event) {
        event.response("Ping!");
    }
}
```