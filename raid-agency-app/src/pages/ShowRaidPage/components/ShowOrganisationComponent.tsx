import { organisationRoles } from "@/entities/organisation/organisation-mapping";
import { Organisation } from "@/generated/raid";
import { dateDisplayFormatter } from "@/Util/DateUtil";
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

export default function ShowOrganisationComponent({
  organisation,
}: {
  organisation: Organisation[] | undefined;
}) {
  return (
    <Card sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}>
      <CardHeader title="Organisations" />
      <CardContent>
        <Stack gap={3}>
          <Box>
            {(organisation?.length === 0 || organisation === undefined) && (
              <Typography
                variant="body2"
                color={"text.secondary"}
                textAlign={"center"}
              >
                No organisation defined
              </Typography>
            )}
          </Box>
          {organisation?.map((organisation, index) => {
            return (
              <Stack sx={{ paddingLeft: 2 }} spacing={2} key={index}>
                <Box className="raid-card-well">
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

                        {(organisation.role.length === 0 ||
                          organisation.role === undefined) && (
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
                              {organisation.role.map((row, index) => {
                                return (
                                  <TableRow
                                    key={index}
                                    sx={{
                                      "&:last-child td, &:last-child th": {
                                        border: 0,
                                      },
                                    }}
                                  >
                                    <TableCell component="th" scope="row">
                                      <Chip
                                        label={
                                          organisationRoles[
                                            row.id as keyof typeof organisationRoles
                                          ]
                                        }
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
  );
}
