package au.org.raid.api.service.apids.model;

import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 Used to send pre-formatted XML across the wire instead of having 
 RestTemplate map it through an ObjectMapper (which causes the xml to
 be serialised inside a `<String></String>` wrapper).
 */
public record RawXml(@JsonRawValue String value) {
}
