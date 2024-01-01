package au.org.raid.api.service;

import lombok.Data;

@Data
public class Handle {
    private final String prefix;
    private final String suffix;

    public Handle(final String id) {
        final var parts = id.split("/");
        prefix = parts[parts.length - 2];
        suffix = parts[parts.length - 1];
    }

    public Handle(final String prefix, final String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return "%s/%s".formatted(prefix, suffix);
    }
}
