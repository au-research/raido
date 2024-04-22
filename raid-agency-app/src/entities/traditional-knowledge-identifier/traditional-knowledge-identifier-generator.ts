import { TraditionalKnowledgeLabel } from "@/generated/raid";
export const traditionalKonwledgeIdentifierGenerator =
  (): TraditionalKnowledgeLabel => {
    return {
      id: "https://localcontexts.org/label/bc-provenance/",
      schemaUri: "https://localcontexts.org/labels/biocultural-labels/",
    };
  };
