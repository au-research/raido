package au.org.raid.api.vocabularies.datacite;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResourceTypeGeneral {
    AUDIOVISUAL("Audiovisual"),
    BOOK("Book"),
    BOOK_CHAPTER("BookChapter"),
    COLLECTION("Collection"),
    COMPUTATIONAL_NOTEBOOK("ComputationalNotebook"),
    CONFERENCE_PAPER("ConferencePaper"),
    CONFERENCE_PROCEEDING("ConferenceProceeding"),
    DATA_PAPER("DataPaper"),
    DATASET("Dataset"),
    DISSERTATION("Dissertation"),
    EVENT("Event"),
    IMAGE("Image"),
    INTERACTIVE_RESOURCE("InteractiveResource"),
    INSTRUMENT("Instrument"),
    JOURNAL("Journal"),
    JOURNAL_ARTICLE("JournalArticle"),
    MODEL("Model"),
    OUTPUT_MANAGEMENT_PLAN("OutputManagementPlan"),
    PEER_REVIEW("PeerReview"),
    PHYSICAL_OBJECT("PhysicalObject"),
    PREPRINT("Preprint"),
    REPORT("Report"),
    SERVICE("Service"),
    SOFTWARE("Software"),
    SOUND("Sound"),
    STANDARD("Standard"),
    STUDY_REGISTRATION("StudyRegistration"),
    TEXT("Text"),
    WORKFLOW("Workflow"),
    OTHER("Other");

    private final String name;
}
