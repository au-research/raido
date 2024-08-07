import type {
  Contributor,
  ContributorPosition,
  ContributorRole,
} from "@/generated/raid";
import mapping from "@/mapping.json";
import { MappingElement } from "@/types";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
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

import { ExpandMore as ExpandMoreIcon } from "@mui/icons-material";

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
                          mapping.find(
                            (el: MappingElement) =>
                              el.id === (row.id as unknown as string)
                          )?.value
                        }
                        size="small"
                        color="primary"
                      />

                      {}
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
    <Card>
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
              <div key={index}>
                <Accordion variant="outlined">
                  <AccordionSummary
                    expandIcon={<ExpandMoreIcon />}
                    aria-controls={`panel${index}-content`}
                    id={`panel${index}-header`}
                  >
                    <Stack
                      direction="row"
                      justifyContent="space-between"
                      alignItems="center"
                      spacing={2}
                      sx={{ width: "100%" }}
                    >
                      <Stack direction="row" alignItems="center" gap={2}>
                        <Typography>{contributor.id}</Typography>
                        <Chip
                          size="small"
                          color="primary"
                          label={
                            mapping.find(
                              (el: MappingElement) =>
                                el.id ===
                                (contributor.position[0]
                                  .id as unknown as string)
                            )?.value
                          }
                        />
                      </Stack>
                      <Box>
                        <FormControlLabel
                          control={
                            <Checkbox disabled checked={contributor.leader} />
                          }
                          label="Leader"
                        />
                        <FormControlLabel
                          control={
                            <Checkbox disabled checked={contributor.contact} />
                          }
                          label="Contact"
                        />
                      </Box>
                    </Stack>
                  </AccordionSummary>
                  <AccordionDetails>
                    <Stack gap={2}>
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
                  </AccordionDetails>
                </Accordion>
              </div>
            );
          })}
        </Stack>
      </CardContent>
    </Card>
  );
}
