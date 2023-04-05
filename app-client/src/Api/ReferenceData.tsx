import { FormControl, MenuItem, Select } from "@mui/material";
import {
  InputLabelWithProblem,
  labelWithProblem
} from "Component/InputLabelWithProblem";
import React from "react";
import { SelectProps } from "@mui/material/Select/Select";

export interface ControlledListItem{
  value: string,
  description: string,
}

/* IMPROVE: change items to be union with a query that returns controlled items,
so we can retrieve from server. */
export function ListFormControl({ 
  idPrefix, items, label, problem, value, ...selectProps
}:{
  idPrefix: string, 
  items: ControlledListItem[],
  label: string,
  problem?: string|undefined,
} & SelectProps<string>){
  return <FormControl>
    <InputLabelWithProblem id={idPrefix + "Label"}
      label={label} problem={problem}/>
    <Select {...selectProps}
      id={idPrefix + "Select"}
      labelId={idPrefix + "SelectLabel"}
      label={labelWithProblem(label, problem)}
      error={!!problem}
      value={value || ''}
    >
      {items.map(i =>
        <MenuItem key={i.value} value={i.value}>{i.description}</MenuItem>
      )}
    </Select>
  </FormControl>
}

export const traditionalKnowledgeLabelSchemeUris: ControlledListItem[] = [
  { value: "", description: "" },
  { value: "https://localcontexts.org/labels/traditional-knowledge-labels/", description: "Traditional Knowledge Labels" },
  { value: "https://localcontexts.org/labels/biocultural-labels/", description: "Biocultural Labels" },
]

export const relatedObjectCategories: ControlledListItem[] = [
  {"value": "", "description": ""},
  {"value": "Input", "description": "Input"},
  {"value": "Output", "description": "Output"},
  {"value": "Internal process document or artefact", "description": "Internal process document or artefact"}
];

export const relatedRaidTypes: ControlledListItem[] = [
  {"value": "", "description": ""},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/continues.json", "description": "Continues"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/has-part.json", "description": "HasPart"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/is-continued-by.json", "description": "IsContinuedBy"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/is-derived-from.json", "description": "IsDerivedFrom"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/is-identical-to.json", "description": "IsIdenticalTo"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/is-obsoleted-by.json", "description": "IsObsoletedBy"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/is-part-of.json", "description": "IsPartOf"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/is-source-of.json", "description": "IsSourceOf"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/obsoletes.json", "description": "Obsoletes"}
];


export const relatedObjectTypes: ControlledListItem[] = [
  {"value": "", "description": ""},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/audiovisual.json", "description": "Audiovisual"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/book-chapter.json", "description": "Book Chapter"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/book.json", "description": "Book"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/computational-notebook.json", "description": "Computational Notebook"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/conference-paper.json", "description": "Conference Paper"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/conference-poster.json", "description": "Conference Poster"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/conference-proceeding.json", "description": "Conference Proceeding"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/data-paper.json", "description": "Data Paper"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/dataset.json", "description": "Dataset"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/dissertation.json", "description": "Dissertation"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/educational-material.json", "description": "Educational Material"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/event.json", "description": "Event"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/funding.json", "description": "Funding"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/image.json", "description": "Image"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/instrument.json", "description": "Instrument"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/journal-article.json", "description": "Journal Article"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/model.json", "description": "Model"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/output-management-plan.json", "description": "Output Management Plan"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/physical-object.json", "description": "Physical Object"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/preprint.json", "description": "Preprint"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/prize.json", "description": "Prize"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/report.json", "description": "Report"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/service.json", "description": "Service"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/software.json", "description": "Software"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/sound.json", "description": "Sound"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/standard.json", "description": "Standard"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/text.json", "description": "Text"},
  {"value": "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/related-object-type/workflow.json", "description": "Workflow"}
];

