import { Card, CardContent, CardHeader, CircularProgress } from "@mui/material";

export const Loading = ({
  cardTitle,
  cardSubheader,
}: {
  cardTitle?: string;
  cardSubheader?: string;
}) => {
  return (
    <Card>
      <CardHeader
        title={cardTitle || "Loading..."}
        subheader={cardSubheader || "Please wait..."}
      />
      <CardContent sx={{ display: "flex", justifyContent: "center" }}>
        <CircularProgress />
      </CardContent>
    </Card>
  );
};
