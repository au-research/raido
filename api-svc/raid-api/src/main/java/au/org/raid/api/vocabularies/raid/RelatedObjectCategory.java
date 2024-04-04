package au.org.raid.api.vocabularies.raid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RelatedObjectCategory {
    OUTPUT("https://vocabulary.raid.org/relatedObject.category.id/190"),
    INPUT("https://vocabulary.raid.org/relatedObject.category.id/191"),
    INTERNAL_PROCESS_DOCUMENT("https://vocabulary.raid.org/relatedObject.category.id/192");

    private final String uri;
}
