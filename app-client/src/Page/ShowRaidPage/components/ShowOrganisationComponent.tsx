import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Chip,
  Grid,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import { RaidDto } from "Generated/Raidv2";
import { dateDisplayFormatter } from "date-utils";
import { extractKeyFromIdUri } from "utils";

export default function ShowOrganisationComponent({
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
              Organisations
            </Typography>
          }
        />

        <CardContent>
          <Stack gap={3}>
            <Box>
              {raid?.organisation?.length === 0 && (
                <Typography
                  variant="body2"
                  color={"text.secondary"}
                  textAlign={"center"}
                >
                  No organisation defined
                </Typography>
              )}
            </Box>
            {raid?.organisation?.map((organisation, index) => {
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
                      <Grid item xs={12} sm={12} md={12}>
                        <Box>
                          <Typography variant="body2">Name</Typography>
                          <Typography color="text.secondary" variant="body1">
                            {organisation.id}
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={12} md={12}>
                        <Box>
                          <Typography variant="body2">Roles</Typography>

                          {organisation.role.length === 0 && (
                            <Typography
                              variant="body2"
                              color={"text.secondary"}
                              textAlign={"center"}
                            >
                              No roles defined
                            </Typography>
                          )}

                          <TableContainer
                            component={Paper}
                            variant="outlined"
                            sx={{
                              background: "transparent",
                            }}
                          >
                            <Table size="small">
                              <TableHead>
                                <TableRow>
                                  <TableCell sx={{ width: "50%" }}>
                                    Position
                                  </TableCell>
                                  <TableCell>Start Date</TableCell>
                                  <TableCell>End Date</TableCell>
                                </TableRow>
                              </TableHead>
                              <TableBody>
                                {organisation.role.map((row) => {
                                  return (
                                    <TableRow
                                      key={row.id}
                                      sx={{
                                        "&:last-child td, &:last-child th": {
                                          border: 0,
                                        },
                                      }}
                                    >
                                      <TableCell component="th" scope="row">
                                        <Chip
                                          label={extractKeyFromIdUri(row.id)}
                                          size="small"
                                          color="primary"
                                        />
                                      </TableCell>
                                      <TableCell>
                                        {dateDisplayFormatter(row.startDate)}
                                      </TableCell>
                                      <TableCell>
                                        {dateDisplayFormatter(row.endDate)}
                                      </TableCell>
                                    </TableRow>
                                  );
                                })}
                              </TableBody>
                            </Table>
                          </TableContainer>
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
