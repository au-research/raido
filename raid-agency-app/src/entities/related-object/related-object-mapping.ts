const Categories = {
  OUTPUT: "https://vocabulary.raid.org/relatedObject.category.id/190",
  INPUT: "https://vocabulary.raid.org/relatedObject.category.id/191",
  INTERNAL_PROCESS_DOCUMENT_OR_ARTEFACT:
    "https://vocabulary.raid.org/relatedObject.category.id/192",
};

const Types = {
  OUTPUT_MANAGEMENT_PLAN:
    "https://vocabulary.raid.org/relatedObject.type.schema/247",
  CONFERENCE_POSTER:
    "https://vocabulary.raid.org/relatedObject.type.schema/248",
  WORKFLOW: "https://vocabulary.raid.org/relatedObject.type.schema/249",
  JOURNAL_ARTICLE: "https://vocabulary.raid.org/relatedObject.type.schema/250",
  STANDARD: "https://vocabulary.raid.org/relatedObject.type.schema/251",
  REPORT: "https://vocabulary.raid.org/relatedObject.type.schema/252",
  DISSERTATION: "https://vocabulary.raid.org/relatedObject.type.schema/253",
  PREPRINT: "https://vocabulary.raid.org/relatedObject.type.schema/254",
  DATA_PAPER: "https://vocabulary.raid.org/relatedObject.type.schema/255",
  COMPUTATIONAL_NOTEBOOK:
    "https://vocabulary.raid.org/relatedObject.type.schema/256",
  IMAGE: "https://vocabulary.raid.org/relatedObject.type.schema/257",
  BOOK: "https://vocabulary.raid.org/relatedObject.type.schema/258",
  SOFTWARE: "https://vocabulary.raid.org/relatedObject.type.schema/259",
  EVENT: "https://vocabulary.raid.org/relatedObject.type.schema/260",
  SOUND: "https://vocabulary.raid.org/relatedObject.type.schema/261",
  CONFERENCE_PROCEEDING:
    "https://vocabulary.raid.org/relatedObject.type.schema/262",
  MODEL: "https://vocabulary.raid.org/relatedObject.type.schema/263",
  CONFERENCE_PAPER: "https://vocabulary.raid.org/relatedObject.type.schema/264",
  TEXT: "https://vocabulary.raid.org/relatedObject.type.schema/265",
  INSTRUMENT: "https://vocabulary.raid.org/relatedObject.type.schema/266",
  LEARNING_OBJECT: "https://vocabulary.raid.org/relatedObject.type.schema/267",
  PRIZE: "https://vocabulary.raid.org/relatedObject.type.schema/268",
  DATASET: "https://vocabulary.raid.org/relatedObject.type.schema/269",
  PHYSICAL_OBJECT: "https://vocabulary.raid.org/relatedObject.type.schema/270",
  BOOK_CHAPTER: "https://vocabulary.raid.org/relatedObject.type.schema/271",
  FUNDING: "https://vocabulary.raid.org/relatedObject.type.schema/272",
  AUDIOVISUAL: "https://vocabulary.raid.org/relatedObject.type.schema/273",
  SERVICE: "https://vocabulary.raid.org/relatedObject.type.schema/274",
} as const;

type RelatedObjectTypeIdValues = {
  [key in (typeof Types)[keyof typeof Types]]: string;
};

type RelatedObjectCategoryIdValues = {
  [key in (typeof Categories)[keyof typeof Categories]]: string;
};

export const relatedObjectTypeMapping: RelatedObjectTypeIdValues = {
  [Types.OUTPUT_MANAGEMENT_PLAN]: "Output Management Plan",
  [Types.CONFERENCE_POSTER]: "Conference Poster",
  [Types.WORKFLOW]: "Workflow",
  [Types.JOURNAL_ARTICLE]: "Journal Article",
  [Types.STANDARD]: "Standard",
  [Types.REPORT]: "Report",
  [Types.DISSERTATION]: "Dissertation",
  [Types.PREPRINT]: "Preprint",
  [Types.DATA_PAPER]: "Data Paper",
  [Types.COMPUTATIONAL_NOTEBOOK]: "Computational Notebook",
  [Types.IMAGE]: "Image",
  [Types.BOOK]: "Book",
  [Types.SOFTWARE]: "Software",
  [Types.EVENT]: "Event",
  [Types.SOUND]: "Sound",
  [Types.CONFERENCE_PROCEEDING]: "Conference Proceeding",
  [Types.MODEL]: "Model",
  [Types.CONFERENCE_PAPER]: "Conference Paper",
  [Types.TEXT]: "Text",
  [Types.INSTRUMENT]: "Instrument",
  [Types.LEARNING_OBJECT]: "Learning Object",
  [Types.PRIZE]: "Prize",
  [Types.DATASET]: "Dataset",
  [Types.PHYSICAL_OBJECT]: "Physical Object",
  [Types.BOOK_CHAPTER]: "Book Chapter",
  [Types.FUNDING]: "Funding",
  [Types.AUDIOVISUAL]: "Audiovisual",
  [Types.SERVICE]: "Service",
};

export const relatedObjectCategoryMapping: RelatedObjectCategoryIdValues = {
  [Categories.INPUT]: "Input",
  [Categories.OUTPUT]: "Output",
  [Categories.INTERNAL_PROCESS_DOCUMENT_OR_ARTEFACT]:
    " Internal process document or artefact",
};
