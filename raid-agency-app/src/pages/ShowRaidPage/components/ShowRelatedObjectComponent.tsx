import { relatedObjectCategoryMapping, relatedObjectTypeMapping } from "@/entities/related-object/related-object-mapping";
import { RelatedObject } from "@/generated/raid";
import {
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import { Link } from "react-router-dom";

export default function ShowRelatedObjectComponent({
  relatedObject,
}: {
  relatedObject: RelatedObject[] | undefined;
}) {
  return (
    <Card sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}>
      <CardHeader title="Related Objects" />
      <CardContent>
        <Stack gap={3}>
          <Box>
            {(relatedObject?.length === 0 || relatedObject === undefined) && (
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
                          <Button
                            variant="text"
                            component={Link}
                            to={relatedObject.id || ""}
                            target="_blank"
                            rel="noopener noreferrer"
                          >
                            {relatedObject.id}
                          </Button>
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid item xs={12} sm={12} md={3}>
                      <Box>
                        <Typography variant="body2">Type</Typography>
                        <Typography color="text.secondary" variant="body1">
                          {
                            relatedObjectTypeMapping[
                              relatedObject.type
                                ?.id as keyof typeof relatedObjectTypeMapping
                            ]
                          }
                        </Typography>
                      </Box>
                    </Grid>
                    <Grid item xs={12} sm={12} md={3}>
                      <Box>
                        <Typography variant="body2">Category</Typography>
                        <Typography color="text.secondary" variant="body1">
                          {relatedObject?.category &&
                            relatedObjectCategoryMapping[
                              relatedObject?.category[0]
                                .id as keyof typeof relatedObjectCategoryMapping
                            ]}
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
