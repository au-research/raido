import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  IconButton,
  MenuItem,
  Stack,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import { RaidDto } from "Generated/Raidv2";
import {
  Control,
  Controller,
  FieldErrors,
  useFieldArray,
} from "react-hook-form";

export const identifierTypes = [
  "https://localcontexts.org/labels/traditional-knowledge-labels/",
  "https://localcontexts.org/labels/biocultural-labels/",
];

export default function FormTraditionalKnowledgeIdentifiersComponent({
  control,
  errors,
  color,
}: {
  control: Control<RaidDto, any>;
  errors: FieldErrors<RaidDto>;
  color: string;
}) {
  const tradionalKnowledgeIdentifierArray = useFieldArray({
    control,
    name: "traditionalKnowledgeLabels",
  });

  const handleAddTradionalKnowledgeIdentifier = () => {
    tradionalKnowledgeIdentifierArray.append({
      schemeUri: "aaa",
    });
  };

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: color,
        borderLeftWidth: 3,
      }}
    >
      <CardHeader
        title={
          <Typography variant="h6" component="div">
            Traditional Knowledge Identifiers
          </Typography>
        }
        action={
          <Tooltip
            title="Add Traditional Knowledge Identifier"
            placement="right"
          >
            <IconButton
              aria-label="Add Traditional Knowledge Identifier"
              onClick={handleAddTradionalKnowledgeIdentifier}
            >
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        }
      />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {tradionalKnowledgeIdentifierArray.fields.length === 0 && (
            <Typography
              variant="body2"
              color={"text.secondary"}
              textAlign={"center"}
            >
              No traditional knowledge identifier defined
            </Typography>
          )}
          {tradionalKnowledgeIdentifierArray.fields.map((field, index) => {
            return (
              <Box
                sx={{
                  bgcolor: "rgba(0, 0, 0, 0.02)",
                  p: 2,
                  borderRadius: 2,
                }}
                key={field.id}
              >
                <Controller
                  control={control}
                  name={`titles.${index}`}
                  render={({ field: { onChange, ...controllerField } }) => {
                    return (
                      <>
                        <Stack direction="row" alignItems="flex-start" gap={1}>
                          <Grid container spacing={2}>
                            <Grid item xs={12} sm={6} md={6}>
                              <TextField
                                select
                                {...controllerField}
                                value={controllerField?.value?.type.id}
                                size="small"
                                fullWidth
                                label="Traditional Knowledge Label Schema URI"
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    type: {
                                      ...controllerField?.value?.type,
                                      id: event.target.value,
                                    },
                                  });
                                }}
                              >
                                {identifierTypes.map((titleType) => (
                                  <MenuItem key={titleType} value={titleType}>
                                    {titleType}
                                  </MenuItem>
                                ))}
                              </TextField>
                            </Grid>
                          </Grid>
                          <Tooltip
                            title="Remove Traditional Knowledge Identifier"
                            placement="right"
                          >
                            <IconButton
                              aria-label="Remove Traditional Knowledge Identifier"
                              onClick={() =>
                                tradionalKnowledgeIdentifierArray.remove(index)
                              }
                            >
                              <RemoveCircleOutlineIcon />
                            </IconButton>
                          </Tooltip>
                        </Stack>
                      </>
                    );
                  }}
                />
              </Box>
            );
          })}
        </Stack>
      </CardContent>
    </Card>
  );
}
