import { TraditionalKnowledgeLabel } from "@/generated/raid";

const traditionalKnowledgeLabelGenerator = (): TraditionalKnowledgeLabel => {
  return {
    id: "https://localcontexts.org/label/bc-provenance/",
    schemaUri: "https://localcontexts.org/labels/biocultural-labels/",
  };
};

export default traditionalKnowledgeLabelGenerator;
