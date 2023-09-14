import { faker } from "@faker-js/faker";
import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
  Autocomplete,
  Box,
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  IconButton,
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

import { subjects } from "../../../references";

export default function FormSubjectsComponent({
  control,
  errors,
  color,
}: {
  control: Control<RaidDto, any>;
  errors: FieldErrors<RaidDto>;
  color: string;
}) {
  const subjectsFieldArray = useFieldArray({
    control,
    name: "subjects",
  });

  const handleAddSubject = () => {
    subjectsFieldArray.append({
      keyword: faker.lorem.word(),
      id: "Transgenesis",
      schemeUri: "https://linked.data.gov.au/def/anzsrc-for/2020/",
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
        action={
          <Tooltip title="Add Subject" placement="right">
            <IconButton aria-label="Add Subject" onClick={handleAddSubject}>
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        }
        title="Subjects"
        subheader="RAiD Subjects"
      />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {subjectsFieldArray.fields.length === 0 && (
            <Typography
              variant="body2"
              color={"text.secondary"}
              textAlign={"center"}
            >
              No subjects defined
            </Typography>
          )}
          {subjectsFieldArray.fields.map((field, index) => {
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
                  name={`subjects.${index}`}
                  render={({ field: { onChange, ...controllerField } }) => {
                    return (
                      <>
                        <Stack direction="row" alignItems="flex-start" gap={1}>
                          <Grid container spacing={2}>
                            <Grid item xs={12} sm={12} md={12}>
                              <Controller
                                name={`subjects.${index}.schemeUri`}
                                control={control}
                                defaultValue=""
                                rules={{ required: true }}
                                render={({
                                  field: { onChange, value },
                                  fieldState: { error },
                                }) => (
                                  <Autocomplete
                                    options={subjects}
                                    getOptionLabel={(option) =>
                                      `${option.id}: ${option.name}`
                                    }
                                    value={
                                      subjects.find(
                                        (lang) => lang.id === value
                                      ) || null
                                    }
                                    onChange={(_, newValue) => {
                                      onChange(newValue ? newValue.id : "");
                                    }}
                                    isOptionEqualToValue={(option, value) => {
                                      return option.id === value.id;
                                    }}
                                    renderInput={(params) => (
                                      <TextField
                                        {...params}
                                        size="small"
                                        label="Subject Keyword"
                                        error={!!error}
                                        helperText={
                                          error
                                            ? "This field is required"
                                            : null
                                        }
                                      />
                                    )}
                                  />
                                )}
                              />
                            </Grid>
                          </Grid>
                          <Tooltip title="Remove subject" placement="right">
                            <IconButton
                              aria-label="Remove subject"
                              onClick={() => subjectsFieldArray.remove(index)}
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
