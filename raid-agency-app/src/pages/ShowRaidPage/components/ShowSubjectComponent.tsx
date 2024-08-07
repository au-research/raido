import { Subject } from "@/generated/raid";
import language from "@/references/language.json";
import subjectTypeReference from "@/references/subject_type.json";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  List,
  ListItem,
  ListItemText,
  Stack,
  Typography,
} from "@mui/material";

export default function ShowSubjectComponent({
  subject,
}: {
  subject: Subject[] | undefined;
}) {
  return (
    <Card>
      <CardHeader title="Subjects" />
      <CardContent>
        <Stack gap={3}>
          <Box>
            {(subject?.length === 0 || subject === undefined) && (
              <Typography
                variant="body2"
                color={"text.secondary"}
                textAlign={"center"}
              >
                No subjects defined
              </Typography>
            )}
          </Box>
          {subject?.map((subject, index) => {
            const extractedSubjectId = subject?.id
              ?.toString()
              .replace("https://linked.data.gov.au/def/anzsrc-for/2020/", "");

            const subjectReferenceResult = subjectTypeReference.find(
              (el) => el.id.toString() === extractedSubjectId
            );

            const subjectTitle = `${subjectReferenceResult?.name} (${subjectReferenceResult?.id})`;

            return (
              <Stack spacing={2} key={index}>
                <Box className="raid-card-well">
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={12} md={12}>
                      <Box>
                        <Typography variant="body2">Subject</Typography>
                        <Typography color="text.secondary" variant="body1">
                          {subjectTitle}
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid item xs={12} sm={12} md={12}>
                      <Box>
                        <Typography variant="body2">Keywords</Typography>
                        <List dense>
                          {subject?.keyword?.map((el, index) => (
                            <ListItem key={index}>
                              <ListItemText
                                primary={el.text}
                                secondary={
                                  language.find(
                                    (lang) =>
                                      lang.id.toString() ===
                                      el?.language?.id?.toString()
                                  )?.name
                                }
                              />
                            </ListItem>
                          ))}
                        </List>
                      </Box>
                    </Grid>
                  </Grid>
                </Box>
              </Stack>
            );
          })}
        </Stack>
      </CardContent>
    </Card>
  );
}
