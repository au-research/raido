import {
  Description as DescriptionIcon,
  SentimentDissatisfied as SentimentDissatisfiedIcon,
} from "@mui/icons-material";

import {
  Box,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  ListSubheader,
  Typography,
} from "@mui/material";
import { ReadData } from "types";

export default function Descriptions({ data }: { data: ReadData | undefined }) {
  return (
    <>
      <Typography
        variant="body2"
        sx={{ fontWeight: 500, ml: 2 }}
        color="text.secondary"
      >
        Description
      </Typography>

      {(data?.metadata?.descriptions?.length &&
        data?.metadata?.descriptions?.length > 0 && (
          <List dense={true}>
            {data?.metadata?.descriptions?.map((description, index) => (
              <ListItem key={index}>
                <ListItemIcon>
                  <DescriptionIcon />
                </ListItemIcon>
                <ListItemText
                  primary={description.description}
                  secondary={
                    <Box sx={{ display: "flex" }}>
                      <span>{description.type}</span>
                    </Box>
                  }
                />
              </ListItem>
            ))}
          </List>
        )) || (
        <Box
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
        >
          <SentimentDissatisfiedIcon
            sx={{
              color: "rgba(0, 0, 0, 0.6)",
            }}
          />
          <Typography variant="body2" color="text.secondary">
            No content available.
          </Typography>
        </Box>
      )}
    </>
  );
}
