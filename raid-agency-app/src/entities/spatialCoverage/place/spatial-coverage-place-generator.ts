import { Language, SpatialCoveragePlace } from "@/generated/raid";
import languageSchema from "@/references/language_schema.json";

const spatialCoveragePlaceLanguageGenerator = (): Language => {
  return {
    id: "eng",
    schemaUri: languageSchema[0].uri,
  };
};

const spatialCoveragePlaceGenerator = (): SpatialCoveragePlace => {
  return {
    text: "Canberra",
    language: spatialCoveragePlaceLanguageGenerator(),
  };
};

export default spatialCoveragePlaceGenerator;
