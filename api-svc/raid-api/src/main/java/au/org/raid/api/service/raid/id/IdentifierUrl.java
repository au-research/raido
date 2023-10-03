package au.org.raid.api.service.raid.id;

public record IdentifierUrl(
        /** the url prefix is not validated, it's just a string, it doesn't have to
         start with a protocol or a domain. */
        String urlPrefix,
        IdentifierHandle handle
) implements IdentifierParser.ParseUrlResult {

    public IdentifierUrl(String urlPrefix, String prefix, String suffix) {
        this(urlPrefix, new IdentifierHandle(prefix, suffix));
    }

    public String formatUrl() {
        return handle.format(urlPrefix);
    }
}
