import { ChangeOp, getOpIcon, getPrimaryText } from "../utils";
import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ListItem from "@mui/material/ListItem";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import AccordionDetails from "@mui/material/AccordionDetails";
import Divider from "@mui/material/Divider";
import List from "@mui/material/List";
import * as React from "react";

export default function ChangeList(parsedData: any) {
  return (
    <>
      {parsedData?.parsedData &&
        parsedData.parsedData
          .filter((el: ChangeOp) => el.path !== "/identifier/version")
          .map((el: ChangeOp, i: number) => (
            <div key={i}>
              <Accordion TransitionProps={{ unmountOnExit: true }}>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  <ListItem>
                    <ListItemIcon>{getOpIcon(el.op)}</ListItemIcon>
                    <ListItemText
                      primary={getPrimaryText(el)}
                      secondary={el.path}
                    />
                  </ListItem>
                </AccordionSummary>
                <AccordionDetails>
                  <pre>{getPrimaryText(el)}</pre>
                </AccordionDetails>
              </Accordion>
              {i === parsedData.parsedData.length - 2 ? "" : <Divider />}
            </div>
          ))}

      <pre style={{ marginBottom: "10px" }}>
        <List sx={{ mb: 3 }}>
          {parsedData?.parsedData &&
            parsedData.parsedData
              .filter((el: ChangeOp) => el.path !== "/identifier/version")
              .map((el: ChangeOp, i: number) => (
                <div key={i}>
                  <ListItem>
                    <ListItemIcon>{getOpIcon(el.op)}</ListItemIcon>
                    <ListItemText
                      primary={getPrimaryText(el)}
                      secondary={`${el.path} - ${i} - ${
                        parsedData.parsedData.length - 2
                      }`}
                    />
                  </ListItem>
                  {i === parsedData.parsedData.length - 2 ? "" : <Divider />}
                </div>
              ))}
        </List>
      </pre>
    </>
  );
}
