package au.org.raid.api.vocabularies.raid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RelatedObjectType {
    OUTPUT_MANAGEMENT_PLAN("https://vocabulary.raid.org/relatedObject.type.schema/247"),
    CONFERENCE_POSTER("https://vocabulary.raid.org/relatedObject.type.schema/248"),
    WORKFLOW("https://vocabulary.raid.org/relatedObject.type.schema/249"),
    JOURNAL_ARTICLE("https://vocabulary.raid.org/relatedObject.type.schema/250"),
    STANDARD("https://vocabulary.raid.org/relatedObject.type.schema/251"),
    REPORT("https://vocabulary.raid.org/relatedObject.type.schema/252"),
    DISSERTATION("https://vocabulary.raid.org/relatedObject.type.schema/253"),
    PREPRINT("https://vocabulary.raid.org/relatedObject.type.schema/254"),
    DATA_PAPER("https://vocabulary.raid.org/relatedObject.type.schema/255"),
    COMPUTATIONAL_NOTEBOOK("https://vocabulary.raid.org/relatedObject.type.schema/256"),
    IMAGE("https://vocabulary.raid.org/relatedObject.type.schema/257"),
    BOOK("https://vocabulary.raid.org/relatedObject.type.schema/258"),
    SOFTWARE("https://vocabulary.raid.org/relatedObject.type.schema/259"),
    EVENT("https://vocabulary.raid.org/relatedObject.type.schema/260"),
    SOUND("https://vocabulary.raid.org/relatedObject.type.schema/261"),
    CONFERENCE_PROCEEDING("https://vocabulary.raid.org/relatedObject.type.schema/262"),
    MODEL("https://vocabulary.raid.org/relatedObject.type.schema/263"),
    CONFERENCE_PAPER("https://vocabulary.raid.org/relatedObject.type.schema/264"),
    TEXT("https://vocabulary.raid.org/relatedObject.type.schema/265"),
    INSTRUMENT("https://vocabulary.raid.org/relatedObject.type.schema/266"),
    LEARNING_OBJECT("https://vocabulary.raid.org/relatedObject.type.schema/267"),
    PRIZE("https://vocabulary.raid.org/relatedObject.type.schema/268"),
    DATASET("https://vocabulary.raid.org/relatedObject.type.schema/269"),
    PHYSICAL_OBJECT("https://vocabulary.raid.org/relatedObject.type.schema/270"),
    BOOK_CHAPTER("https://vocabulary.raid.org/relatedObject.type.schema/271"),
    FUNDING("https://vocabulary.raid.org/relatedObject.type.schema/272"),
    AUDIO_VISUAL("https://vocabulary.raid.org/relatedObject.type.schema/273"),
    SERVICE("https://vocabulary.raid.org/relatedObject.type.schema/274");
    
    private final String uri;
}
