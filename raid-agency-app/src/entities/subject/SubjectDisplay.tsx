import { DisplayCard } from "@/components/display-card";
import { DisplayItem } from "@/components/display-item";
import { useMapping } from "@/mapping";
import SubjectKeywordItem from "@/entities/subject/keyword/SubjectKeywordItem";
import { Subject } from "@/generated/raid";
import { Divider, Grid, Stack, Typography } from "@mui/material";

import { memo, useMemo } from "react";

const NoItemsMessage = memo(() => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No subjects defined
  </Typography>
));

const SubjectItem = memo(({ subject, i }: { subject: Subject; i: number }) => {
  const { subjectMap } = useMapping();

  const extractedSubjectId = useMemo(
    () =>
      subject?.id?.replace(
        "https://linked.data.gov.au/def/anzsrc-for/2020/",
        ""
      ) || "",
    [subject?.id]
  );

  const subjectMappedValue = useMemo(
    () => subjectMap.get(String(extractedSubjectId)) ?? "",
    [subject?.id]
  );

  return (
    <Stack gap={2}>
      <Typography variant="body1">Subject #{i + 1}</Typography>

      <Grid container spacing={2}>
        <DisplayItem label="Subject" value={subjectMappedValue} width={12} />
      </Grid>
      <Stack gap={2} sx={{ pl: 3 }}>
        <Stack direction="row" alignItems="baseline">
          <Typography variant="body1" sx={{ mr: 0.5 }}>
            Keywords
          </Typography>
          <Typography variant="caption" color="text.disabled">
            Subject #{i + 1}
          </Typography>
        </Stack>
        <Stack gap={2} divider={<Divider />}>
          {subject?.keyword?.map((subjectKeyword) => (
            <SubjectKeywordItem
              subjectKeyword={subjectKeyword}
              key={crypto.randomUUID()}
            />
          ))}
        </Stack>
      </Stack>
    </Stack>
  );
});

const SubjectDisplay = memo(({ data }: { data: Subject[] }) => {
  return (
    <DisplayCard
      data={data}
      labelPlural="Subjects"
      children={
        <>
          {data.length === 0 && <NoItemsMessage />}
          <Stack gap={2} divider={<Divider />}>
            {data?.map((subject, i) => (
              <SubjectItem subject={subject} key={crypto.randomUUID()} i={i} />
            ))}
          </Stack>
        </>
      }
    />
  );
});

NoItemsMessage.displayName = "NoItemsMessage";
SubjectItem.displayName = "SubjectItem";
SubjectDisplay.displayName = "SubjectDisplay";

export default SubjectDisplay;
