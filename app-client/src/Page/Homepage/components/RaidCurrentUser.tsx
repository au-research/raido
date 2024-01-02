import {useAuthApi} from "../../../Api/AuthApi";
import {useAuth} from "../../../Auth/AuthProvider";
import {useQuery} from "@tanstack/react-query";
import {Box, Card, CardContent, CardHeader, Grid} from "@mui/material";
import Typography from "@mui/material/Typography";
import {IdProviderDisplay} from "../../../Component/IdProviderDisplay";
import {RoleDisplay} from "../../../Component/Util";
import React from "react";

export default function RaidCurrentUser() {
    const api = useAuthApi();
    const {
        session: { payload: user },
    } = useAuth();
    const spQuery = useQuery(
        ["readServicePoint", user.servicePointId],
        async () =>
            await api.admin.readServicePoint({
                servicePointId: user.servicePointId,
            }),
    );
    return (
        <Card
            sx={{
                mt: 3,
                borderLeft: "solid",
                borderLeftColor: "primary.main",
                borderLeftWidth: 3,
            }}
        >
            <CardHeader title="Signed-in user" />
            <CardContent>
                <Grid container>
                    <Grid item xs={12} sm={6} md={6}>
                        <Box>
                            <Typography variant="body2">Identity</Typography>
                            <Typography color="text.secondary" variant="body1">
                                {user.email}
                            </Typography>
                        </Box>
                    </Grid>
                    <Grid item xs={12} sm={2} md={2}>
                        <Box>
                            <Typography variant="body2">ID provider</Typography>
                            <Typography color="text.secondary" variant="body1">
                                <IdProviderDisplay payload={user} />
                            </Typography>
                        </Box>
                    </Grid>
                    <Grid item xs={12} sm={2} md={2}>
                        <Box>
                            <Typography variant="body2">Service point</Typography>
                            <Typography color="text.secondary" variant="body1">
                                {spQuery.data?.name || ""}
                            </Typography>
                        </Box>
                    </Grid>
                    <Grid item xs={12} sm={2} md={2}>
                        <Box>
                            <Typography variant="body2">Role</Typography>
                            <Typography color="text.secondary" variant="body1">
                                <RoleDisplay role={user.role} />
                            </Typography>
                        </Box>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
}