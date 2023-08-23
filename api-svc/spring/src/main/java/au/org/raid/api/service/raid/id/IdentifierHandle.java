package au.org.raid.api.service.raid.id;

import au.org.raid.api.util.Nullable;

import java.util.Optional;

import static au.org.raid.api.util.RestUtil.normaliseWithTrailingSlash;

public record IdentifierHandle(
  String prefix,
  String suffix,
  /** we expect that deep paths that LOOK like prefixes but actually map
   to API stuff will have already been mapped to their endpoint method, so 
   the parser chooses to assume the extra stuff is in the nature of a 
   "description" (like StackOverflow question titles). 
   */
  Optional<String> description
) implements IdentifierParser.ParseHandleResult {

  public IdentifierHandle(String prefix, String suffix) {
    this(prefix, suffix, Optional.empty());
  }

  public IdentifierHandle(String prefix, String suffix, String description) {
    this(prefix, suffix, Optional.of(description));
  }

  public String format(@Nullable String urlPrefix) {
    StringBuilder url = new StringBuilder();
    if( urlPrefix != null ){
      url.append(normaliseWithTrailingSlash(urlPrefix));
    }
    
    url.append(prefix).
      append(IdentifierParser.HANDLE_SEPARATOR_CHAR).append(suffix);

    description.ifPresent(i->
      url.append(IdentifierParser.HANDLE_SEPARATOR_CHAR).append(i)
    );

    return url.toString();
  }

  public String format() {
    return format(null);
  }
}
