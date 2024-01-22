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
import {Subject} from "Generated/Raidv2";
import language from "References/language.json";
import subjectType from "References/subject_type.json";

export default function ShowSubjectComponent({subject,
}: {
    subject: Subject[] | undefined
}) {
  return (
      <Card className="raid-card">
        <CardHeader title="Subjects" />
        <CardContent>
          <Stack gap={3}>
            <Box>
              {subject?.length === 0 || subject === undefined && (
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
              const subjectTitle = subjectType.find(
                (el) => el.id === subject.id
              );

              return (
                <Stack spacing={2} key={index}>
                  <Box className="raid-card-well">
                    <Grid container spacing={2}>
                      <Grid item xs={12} sm={12} md={12}>
                        <Box>
                          <Typography variant="body2">Subject</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {subjectTitle?.name}
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
                                      (lang) => lang.id === el?.language?.id
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
