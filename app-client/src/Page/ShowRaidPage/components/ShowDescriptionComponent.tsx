import {Box, Card, CardContent, CardHeader, Grid, Stack, Typography,} from "@mui/material";
import {Description} from "Generated/Raidv2";
import {extractKeyFromIdUri} from "utils";
import language from "../../../References/language.json";

export default function ShowDescriptionComponent({description}: { description: Description[] | undefined }) {
    return (
        <Card className="raid-card">
            <CardHeader title="Descriptions"/>

            <CardContent>
                <Stack gap={3}>
                    <Box>
                        {description?.length === 0 && (
                            <Typography
                                variant="body2"
                                color={"text.secondary"}
                                textAlign={"center"}
                            >
                                No descriptions defined
                            </Typography>
                        )}
                    </Box>
                    {description?.map((description, index) => {
                        const lang = language.find(
                            (language) => language.id === description?.language?.id
                        );
                        return (
                            <Stack spacing={2} key={index}>
                                <Box className="raid-card-well">
                                    <Grid container spacing={2}>
                                        <Grid item xs={12} sm={12} md={8}>
                                            <Box>
                                                <Typography variant="body2">
                                                    Description Text
                                                </Typography>
                                                <Typography color="text.secondary" variant="body1">
                                                    {description.text}
                                                </Typography>
                                            </Box>
                                        </Grid>
                                        <Grid item xs={12} sm={12} md={2}>
                                            <Box>
                                                <Typography variant="body2">
                                                    Description Type
                                                </Typography>
                                                <Typography color="text.secondary" variant="body1">
                                                    {extractKeyFromIdUri(description.type.id)}
                                                </Typography>
                                            </Box>
                                        </Grid>
                                        <Grid item xs={12} sm={12} md={2}>
                                            <Box>
                                                <Typography variant="body2">Language</Typography>
                                                <Typography color="text.secondary" variant="body1">
                                                    {lang?.name}
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
