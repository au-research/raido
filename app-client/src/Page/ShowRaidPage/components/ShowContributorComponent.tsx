import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Checkbox,
  FormControlLabel,
  FormGroup,
  Grid,
  List,
  ListItem,
  ListItemText,
  Stack,
  Typography,
} from "@mui/material";
import { RaidDto } from "Generated/Raidv2";
import { extractKeyFromIdUri, extractLastUrlSegment } from "utils";

export default function ShowContributorComponent({
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
              Contributors
            </Typography>
          }
        />

        <CardContent>
          <Stack gap={3}>
            <Box>
              {raid?.contributor?.length === 0 && (
                <Typography
                  variant="body2"
                  color={"text.secondary"}
                  textAlign={"center"}
                >
                  No contributors defined
                </Typography>
              )}
            </Box>
            {raid?.contributor?.map((contributor, index) => {
              return (
                <Stack sx={{ paddingLeft: 2 }} spacing={2} key={index}>
                  <Box
                    sx={{
                      bgcolor: "rgba(0, 0, 0, 0.02)",
                      p: 2,
                      borderRadius: 2,
                    }}
                    className="animated-tile animated-tile-reverse"
                  >
                    <Grid container spacing={2}>
                      <Grid item xs={12} sm={12} md={4}>
                        <Box>
                          <Typography variant="body2">ID</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {contributor.id}
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={12} md={4}>
                        <Box>
                          <FormGroup>
                            <FormControlLabel
                              control={
                                <Checkbox
                                  disabled
                                  checked={contributor.leader}
                                />
                              }
                              label="Leader"
                            />
                            <FormControlLabel
                              control={
                                <Checkbox
                                  disabled
                                  checked={contributor.contact}
                                />
                              }
                              label="Contact"
                            />
                          </FormGroup>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={12} md={12}>
                        <Box>
                          <Typography variant="body2">Positions</Typography>
                          <List dense disablePadding>
                            {contributor.position?.map((position, index) => (
                              <ListItem key={index}>
                                <ListItemText
                                  primary={extractKeyFromIdUri(position.id)}
                                  secondary={`${position.startDate || ""} ➡️ ${
                                    position.endDate || "No end date"
                                  }`}
                                />
                              </ListItem>
                            ))}
                          </List>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={12} md={12}>
                        <Box>
                          <Typography variant="body2">Roles</Typography>
                          <List dense disablePadding>
                            {contributor.role?.map((role, index) => (
                              <ListItem key={index}>
                                <ListItemText
                                  primary={extractLastUrlSegment(role.id)}
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
