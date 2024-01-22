import {Box, Card, CardContent, CardHeader, Grid, Stack, Typography,} from "@mui/material";
import {RelatedObject} from "Generated/Raidv2";
import {extractKeyFromIdUri} from "utils";

export default function ShowRelatedObjectComponent({relatedObject}: { relatedObject: RelatedObject[] | undefined }) {
  return (
      <Card className="raid-card">
        <CardHeader
          title={
            <Typography variant="h6" component="div">
              Related Objects
            </Typography>
          }
        />

        <CardContent>
          <Stack gap={3}>
            <Box>
              {relatedObject?.length === 0 && (
                <Typography
                  variant="body2"
                  color={"text.secondary"}
                  textAlign={"center"}
                >
                  No related objects defined
                </Typography>
              )}
            </Box>
            {relatedObject?.map((relatedObject, index) => {
              return (
                <Stack sx={{ paddingLeft: 2 }} spacing={2} key={index}>
                  <Box className="raid-card-well">
                    <Grid container spacing={2}>
                      <Grid item xs={12} sm={12} md={6}>
                        <Box>
                          <Typography variant="body2">ID</Typography>
                          <Typography color="text.secondary" variant="body1">
                            <a
                              href={relatedObject.id}
                              target="_blank"
                              rel="noopener noreferrer"
                            >
                              {relatedObject.id}
                            </a>
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={12} md={3}>
                        <Box>
                          <Typography variant="body2">Type</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {extractKeyFromIdUri(relatedObject.type?.id)}
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={12} md={3}>
                        <Box>
                          <Typography variant="body2">Category</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {relatedObject.category?.map(el=>extractKeyFromIdUri(el.id)).join(", ")}
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
