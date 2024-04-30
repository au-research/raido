import { contributorMapping } from "@/entities/contributor/contributor-mapping";
import type {
  Contributor,
  ContributorPosition,
  ContributorRole,
} from "@/generated/raid";
import { dateDisplayFormatter } from "@/Util/DateUtil";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Checkbox,
  Chip,
  FormControlLabel,
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

function ContributorPositionsComponent({
  contributorPositions,
}: {
  contributorPositions: ContributorPosition[];
}) {
  return (
    <>
      {contributorPositions.length === 0 && (
        <Typography
          variant="body2"
          color={"text.secondary"}
          textAlign={"center"}
        >
          No positions defined
        </Typography>
      )}

      {contributorPositions.length > 0 && (
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
                <TableCell sx={{ width: "50%" }}>Position</TableCell>
                <TableCell>Start Date</TableCell>
                <TableCell>End Date</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {contributorPositions.map((row) => {
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
                        label={
                          contributorMapping.contributorPosition[
                            row.id as keyof typeof contributorMapping.contributorPosition
                          ]
                        }
                        size="small"
                        color="primary"
                      />
                    </TableCell>
                    <TableCell>{dateDisplayFormatter(row.startDate)}</TableCell>
                    <TableCell>{dateDisplayFormatter(row.endDate)}</TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </>
  );
}

function ContributorRolesComponent({
  contributorRoles,
}: {
  contributorRoles: ContributorRole[];
}) {
  return (
    <>
      {contributorRoles.length === 0 && (
        <Typography
          variant="body2"
          color={"text.secondary"}
          textAlign={"center"}
        >
          No roles defined
        </Typography>
      )}
      <Grid container spacing={1}>
        {contributorRoles.map((row, index) => (
          <Grid item key={index}>
            <Chip label={row.id} size="small" color="primary" key={index} />
          </Grid>
        ))}
      </Grid>
    </>
  );
}

export default function ShowContributorComponent({
  contributor,
}: {
  contributor: Contributor[] | undefined;
}) {
  return (
    <Card sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}>
      <CardHeader title="Contributors" />
      <CardContent>
        <Stack gap={3}>
          <Box>
            {contributor?.length === 0 && (
              <Typography
                variant="body2"
                color={"text.secondary"}
                textAlign={"center"}
              >
                No contributors defined
              </Typography>
            )}
          </Box>
          {contributor?.map((contributor, index) => {
            return (
              <Card variant="outlined" key={index}>
                <CardContent>
                  <Stack spacing={2} key={index}>
                    <Box className="raid-card-well">
                      <Grid container spacing={2}>
                        <Grid item xs={12} sm={12} md={6}>
                          <Box>
                            <Typography variant="body2">ID</Typography>
                            <Typography color="text.secondary" variant="body1">
                              {contributor.id}
                            </Typography>
                          </Box>
                        </Grid>
                        <Grid item xs={12} sm={12} md={3}>
                          <Box>
                            <FormControlLabel
                              control={
                                <Checkbox
                                  disabled
                                  checked={contributor.leader}
                                />
                              }
                              label="Leader"
                            />
                          </Box>
                        </Grid>
                        <Grid item xs={12} sm={12} md={3}>
                          <Box>
                            <FormControlLabel
                              control={
                                <Checkbox
                                  disabled
                                  checked={contributor.contact}
                                />
                              }
                              label="Contact"
                            />
                          </Box>
                        </Grid>
                      </Grid>
                    </Box>
                    <Box className="raid-card-well">
                      <Grid item xs={12} sm={12} md={12}>
                        <Typography variant="body2" gutterBottom>
                          Positions
                        </Typography>

                        <ContributorPositionsComponent
                          contributorPositions={contributor.position}
                        />
                      </Grid>
                    </Box>
                    <Box className="raid-card-well">
                      <Grid item xs={12} sm={12} md={12}>
                        <Typography variant="body2" gutterBottom>
                          Roles
                        </Typography>
                        <ContributorRolesComponent
                          contributorRoles={contributor.role}
                        />
                      </Grid>
                    </Box>
                  </Stack>
                </CardContent>
              </Card>
            );
          })}
        </Stack>
      </CardContent>
    </Card>
  );
}
