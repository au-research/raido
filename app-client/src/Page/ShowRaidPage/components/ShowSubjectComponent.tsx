import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Chip,
  Grid,
  List,
  ListItem,
  ListItemText,
  Stack,
  Typography,
} from "@mui/material";
import { RaidDto } from "Generated/Raidv2";
import { dateDisplayFormatter } from "date-utils";
import { extractKeyFromIdUri } from "utils";
import language from "References/language.json";
import subjectType from "References/subject_type.json";

export default function ShowSubjectComponent({
  raid,
  color,
}: {
  raid: RaidDto;
  color: string;
}) {
  return (
    <Box sx={{ paddingLeft: 2 }}>
      <Card
        variant="outlined"
        sx={{
          borderLeft: "solid",
          borderLeftColor: color,
          borderLeftWidth: 3,
        }}
      >
        <CardHeader
          title={
            <Typography variant="h6" component="div">
              Subjects
            </Typography>
          }
        />

        <CardContent>
          <Stack gap={3}>
            <Box>
              {raid?.subject?.length === 0 && (
                <Typography
                  variant="body2"
                  color={"text.secondary"}
                  textAlign={"center"}
                >
                  No subjects defined
                </Typography>
              )}
            </Box>
            {raid?.subject?.map((subject, index) => {
              const subjectTitle = subjectType.find(
                (el) => el.id === subject.id
              );

              return (
                <Stack spacing={2} key={index}>
                  <Box
                    sx={{
                      bgcolor: "rgba(0, 0, 0, 0.02)",
                      p: 2,
                      borderRadius: 2,
                    }}
                    className="animated-tile animated-tile-reverse"
                  >
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
                            {subject?.keyword?.map((el) => (
                              <ListItem>
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
    </Box>
  );
}
