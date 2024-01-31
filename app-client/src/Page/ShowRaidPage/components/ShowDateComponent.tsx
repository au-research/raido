import {ModelDate} from "Generated/Raidv2";
import {Box, Card, CardContent, CardHeader, Grid, Stack, Typography} from "@mui/material";
import {dateDisplayFormatter} from "../../../Util/DateUtil";

export default function ShowDateComponent({date}: { date: ModelDate | undefined }) {
    return (
        <Card className="raid-card">
            <CardHeader title="Dates"/>
            <CardContent>
                <Stack gap={3}>
                    <Box className="raid-card-well">
                        <Grid container spacing={2}>
                            <Grid item xs={12} sm={12} md={4}>
                                <Box>
                                    <Typography variant="body2">Start Date</Typography>
                                    <Typography color="text.secondary" variant="body1">
                                        {dateDisplayFormatter(date?.startDate)}
                                    </Typography>
                                </Box>
                            </Grid>
                            <Grid item xs={12} sm={12} md={4}>
                                <Box>
                                    <Typography variant="body2">End Date</Typography>
                                    <Typography color="text.secondary" variant="body1">
                                        {dateDisplayFormatter(date?.endDate)}
                                    </Typography>
                                </Box>
                            </Grid>
                        </Grid>
                    </Box>
                </Stack>
            </CardContent>
        </Card>
    );
}
