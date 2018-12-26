package commandor.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultPrefixCache implements PrefixCache {
    private final String defaultPrefix;
    private final Map<Long, String> prefixes = new LinkedHashMap<>(Integer.MAX_VALUE);

    public DefaultPrefixCache(String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
    }

    @Override
    public String getDefaultPrefix(long id) {
        return prefixes.entrySet().stream().filter(e -> e.getKey().equals(id))
                .map(Map.Entry::getValue).findFirst().orElse(defaultPrefix);
    }
}
