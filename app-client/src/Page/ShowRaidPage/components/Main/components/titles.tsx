import {
  Title as TitleIcon,
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

export default function Titles({ data }: { data: ReadData | undefined }) {
  return (
    <>
      <Typography
        variant="body2"
        sx={{ fontWeight: 500, ml: 2 }}
        color="text.secondary"
      >
        Title
      </Typography>

      {(data?.metadata?.titles?.length &&
        data?.metadata?.titles?.length > 0 && (
          <List dense={true}>
            {data?.metadata?.titles?.map((title, index) => {
              const startDateText = `${title.startDate.toLocaleDateString()}`;
              const endDateText = `${
                title.endDate?.toLocaleDateString() || "open"
              }`;
              const dateRangeText = `${startDateText} - ${endDateText}`;
              return (
                <ListItem key={index}>
                  <ListItemIcon>
                    <TitleIcon />
                  </ListItemIcon>
                  <ListItemText
                    disableTypography
                    primary={title.title}
                    secondary={
                      <>
                        <Box
                          sx={{
                            display: "flex",
                            gap: 2,
                            mt: 2,
                          }}
                        >
                          <Box>
                            <Typography variant="body2" color="text.secondary">
                              Type
                            </Typography>
                            <Typography
                              color="text.secondary"
                              variant="caption"
                            >
                              {title.type}
                            </Typography>
                          </Box>
                          <Box>
                            <Typography variant="body2" color="text.secondary">
                              Date Range
                            </Typography>
                            <Typography
                              color="text.secondary"
                              variant="caption"
                            >
                              {dateRangeText}
                            </Typography>
                          </Box>
                        </Box>
                      </>
                    }
                  />
                </ListItem>
              );
            })}
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
