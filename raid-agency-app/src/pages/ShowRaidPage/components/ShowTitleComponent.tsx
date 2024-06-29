import { type Title } from "@/generated/raid";
import mapping from "@/mapping.json";
import language from "@/references/language.json";
import { MappingElement } from "@/types";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  Stack,
  Typography,
} from "@mui/material";

export default function ShowTitleComponent({
  titles,
}: {
  titles: Title[] | undefined;
}) {
  return (
    <Card sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}>
      <CardHeader title="Titles" />
      <CardContent>
        <Stack gap={3}>
          <Box>
            {(titles?.length === 0 || titles === undefined) && (
              <Typography
                variant="body2"
                color={"text.secondary"}
                textAlign={"center"}
              >
                No titles defined
              </Typography>
            )}
          </Box>
          {titles?.map((title, index) => {
            const lang = language.find(
              (language) =>
                language.code.toString() === title?.language?.id?.toString()
            );

            return (
              <Stack spacing={2} key={index}>
                <Box className="raid-card-well">
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={12} md={12}>
                      <Box>
                        <Typography variant="body2">Title</Typography>
                        <Typography color="text.secondary" variant="body1">
                          {title.text}
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid item xs={12} sm={6} md={6}>
                      <Box>
                        <Typography variant="body2">Type</Typography>
                        <Typography color="text.secondary" variant="body1">
                          {
                            mapping.find(
                              (el: MappingElement) => el.id === title.type.id
                            )?.value
                          }
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                      <Box>
                        <Typography variant="body2">Language</Typography>
                        <Typography color="text.secondary" variant="body1">
                          {lang?.name}
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid item xs={12} sm={6} md={6}>
                      <Box>
                        <Typography variant="body2">Start Date</Typography>
                        <Typography color="text.secondary" variant="body1">
                          {dateDisplayFormatter(title.startDate)}
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                      <Box>
                        <Typography variant="body2">End Date</Typography>
                        <Typography color="text.secondary" variant="body1">
                          {dateDisplayFormatter(title.endDate)}
                        </Typography>
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
