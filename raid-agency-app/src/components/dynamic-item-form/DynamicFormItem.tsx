import { IndeterminateCheckBox as IndeterminateCheckBoxIcon } from "@mui/icons-material";
import { Box, IconButton, Stack, Typography } from "@mui/material";
import { memo, useState } from "react";

export const DynamicFormItem = memo(
  ({
    index,
    onRemove,
    children,
    label,
  }: {
    index: number;
    onRemove: (index: number) => void;
    children: React.ReactNode;
    label: string;
  }) => {
    const [isRowHighlighted, setIsRowHighlighted] = useState(false);
    const handleRemoveClick = () => {
      if (
        window.confirm(
          `Are you sure you want to remove "${label} #${index + 1}"?`
        )
      ) {
        onRemove(index ? index : index);
      }
    };
    return (
      <Stack gap={2}>
        {Array.isArray(children) && (
          <Typography variant="body2">
            <span
              style={{
                textDecoration: isRowHighlighted ? "line-through" : "",
              }}
            >
              {label} #{index + 1}
            </span>{" "}
            {isRowHighlighted ? "(to be deleted)" : ""}
          </Typography>
        )}

        <Stack gap={2} width="100%" alignItems="end">
          <Stack gap={1} direction="row" alignItems="start" width="100%">
            <Box width="100%" className={isRowHighlighted ? "remove" : ""}>
              {children}
            </Box>
            <IconButton
              aria-label="delete"
              color="error"
              onMouseEnter={() => setIsRowHighlighted(true)}
              onMouseLeave={() => setIsRowHighlighted(false)}
              onClick={handleRemoveClick}
            >
              <IndeterminateCheckBoxIcon />
            </IconButton>
          </Stack>
        </Stack>
      </Stack>
    );
  }
);
