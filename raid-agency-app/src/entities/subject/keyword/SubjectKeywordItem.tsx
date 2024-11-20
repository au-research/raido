import DisplayItem from "@/components/DisplayItem";
import { useMapping } from "@/contexts/mapping/useMapping";
import { SubjectKeyword } from "@/generated/raid";
import { Grid } from "@mui/material";
import { memo, useMemo } from "react";

const SubjectKeywordItem = memo(
  ({ subjectKeyword }: { subjectKeyword: SubjectKeyword }) => {
    const { languageMap } = useMapping();

    const languageMappedValue = useMemo(
      () => languageMap.get(String(subjectKeyword.language?.id)) ?? "",
      [subjectKeyword.language?.id]
    );
    return (
      <Grid container spacing={2}>
        <DisplayItem label="Keyword" value={subjectKeyword.text} width={8} />
        <DisplayItem label="Language" value={languageMappedValue} width={4} />
      </Grid>
    );
  }
);

SubjectKeywordItem.displayName = "SubjectKeywordItem";

export default SubjectKeywordItem;
