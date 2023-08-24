import { zodResolver } from "@hookform/resolvers/zod";
import { Cancel as CancelIcon } from "@mui/icons-material";
import {
  Alert,
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Container,
  Grid,
  IconButton,
  MenuItem,
  Skeleton,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { Controller, useFieldArray, useForm } from "react-hook-form";
import { z } from "zod";

import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import dayjs from "dayjs";
import {
  Access,
  Contributor,
  CreateRaidV1Request,
  Dates,
  RaidDto,
  Title,
} from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { useMutation } from "@tanstack/react-query";

const tagTypes = ["primary", "secondary", "success", "error", "warning"];

const ItemWithTagsSchema = z.object({
  itemId: z.string().uuid("Not a valid UUID"),
  itemName: z.string().min(10, "Min 10").max(30, "Max 30"),
  itemTags: z.array(
    z.object({ itemTagValue: z.string(), itemTagType: z.string() })
  ),
  dateCreated: z
    .string()
    .regex(/^\d{4}-\d{2}-\d{2}$/, "Not a valid date format"),
});

export type ItemWithTags = z.infer<typeof ItemWithTagsSchema>;

type FormProps = {
  defaultValues: RaidDto;
  onSubmit(data: RaidDto): void;
  isSubmitting: boolean;
};

function FormComponent({ onSubmit, defaultValues, isSubmitting }: FormProps) {
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<RaidDto>({
    defaultValues,
    mode: "onChange",
    reValidateMode: "onChange",
    // resolver: zodResolver(ItemWithTagsSchema),
  });

  const handleFormReset = () => {
    reset(defaultValues);
  };

  const { fields, append, remove } = useFieldArray({
    control,
    name: "titles",
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} autoComplete="off" noValidate>
      <Card>
        <CardHeader
          title="Create new item with tags"
          subheader="Fill out the following form"
        />
        <CardContent>
          <Grid container spacing={2}>
            {/* <Grid item xs={12} sm={12} md={12}>
              <Typography variant="body2" sx={{ mb: 2 }}>
                Titles
              </Typography>

              <Stack gap={1}>
                {fields.map((field, index) => {
                  return (
                    <div key={field.id}>
                      <Controller
                        control={control}
                        name={`itemTags.${index}`}
                        render={({
                          field: { onChange, ...controllerField },
                        }) => {
                          return (
                            <>
                              <Box
                                sx={{
                                  display: "flex",
                                  alignItems: "center",
                                  gap: 1,
                                }}
                              >
                                <TextField
                                  {...controllerField}
                                  value={controllerField.value.itemTagValue}
                                  size="small"
                                  fullWidth
                                  label="Tag Value"
                                  onChange={(event) => {
                                    onChange({
                                      ...controllerField.value,
                                      itemTagValue: event.target.value,
                                    });
                                  }}
                                />
                                <TextField
                                  select
                                  {...controllerField}
                                  value={controllerField.value.itemTagType}
                                  size="small"
                                  fullWidth
                                  label="Tag Type"
                                  onChange={(event) => {
                                    onChange({
                                      ...controllerField.value,
                                      itemTagType: event.target.value,
                                    });
                                  }}
                                >
                                  {tagTypes.map((option) => (
                                    <MenuItem key={option} value={option}>
                                      {option}
                                    </MenuItem>
                                  ))}
                                </TextField>
                                <IconButton
                                  type="button"
                                  sx={{ p: "10px" }}
                                  aria-label="search"
                                  onClick={() => remove(index)}
                                >
                                  <CancelIcon />
                                </IconButton>
                              </Box>
                            </>
                          );
                        }}
                      />
                    </div>
                  );
                })}
                <Box
                  sx={{
                    display: "flex",
                    alignItems: "center",
                  }}
                >
                  <Button
                    fullWidth
                    variant="outlined"
                    onClick={() =>
                      append({
                        itemTagValue: `${+new Date()}`,
                        itemTagType: "primary",
                      })
                    }
                  >
                    Add tag
                  </Button>
                  <IconButton
                    type="button"
                    sx={{ p: "21px" }}
                    aria-label="search"
                  ></IconButton>
                </Box>
              </Stack>
            </Grid> */}

            {/* <Grid item xs={12} sm={12} md={12}>
              <Controller
                name="dateCreated"
                control={control}
                render={({ field: { onChange, ...restField } }) => {
                  return (
                    <Grid item xs={12} sm={12}>
                      <DatePicker
                        label="Date created"
                        defaultValue={dayjs(restField.value)}
                        format="DD-MMM-YYYY"
                        onChange={(event) => {
                          onChange(event?.format("YYYY-MM-DD"));
                        }}
                        slotProps={{
                          textField: {
                            size: "small",
                            fullWidth: true,
                          },
                          actionBar: {
                            actions: ["today"],
                          },
                        }}
                        slots={<TextField />}
                      />
                    </Grid>
                  );
                }}
              />
            </Grid> */}

            {/* <Grid item xs={12} sm={12} md={12}>
              <Typography variant="body2" sx={{ mb: 2 }}>
                Tags
              </Typography>

              <Stack gap={1}>
                {fields.map((field, index) => {
                  return (
                    <div key={field.id}>
                      <Controller
                        control={control}
                        name={`itemTags.${index}`}
                        render={({
                          field: { onChange, ...controllerField },
                        }) => {
                          return (
                            <>
                              <Box
                                sx={{
                                  display: "flex",
                                  alignItems: "center",
                                  gap: 1,
                                }}
                              >
                                <TextField
                                  {...controllerField}
                                  value={controllerField.value.itemTagValue}
                                  size="small"
                                  fullWidth
                                  label="Tag Value"
                                  onChange={(event) => {
                                    onChange({
                                      ...controllerField.value,
                                      itemTagValue: event.target.value,
                                    });
                                  }}
                                />
                                <TextField
                                  select
                                  {...controllerField}
                                  value={controllerField.value.itemTagType}
                                  size="small"
                                  fullWidth
                                  label="Tag Type"
                                  onChange={(event) => {
                                    onChange({
                                      ...controllerField.value,
                                      itemTagType: event.target.value,
                                    });
                                  }}
                                >
                                  {tagTypes.map((option) => (
                                    <MenuItem key={option} value={option}>
                                      {option}
                                    </MenuItem>
                                  ))}
                                </TextField>
                                <IconButton
                                  type="button"
                                  sx={{ p: "10px" }}
                                  aria-label="search"
                                  onClick={() => remove(index)}
                                >
                                  <CancelIcon />
                                </IconButton>
                              </Box>
                            </>
                          );
                        }}
                      />
                    </div>
                  );
                })}
                <Box
                  sx={{
                    display: "flex",
                    alignItems: "center",
                  }}
                >
                  <Button
                    fullWidth
                    variant="outlined"
                    onClick={() =>
                      append({
                        itemTagValue: `${+new Date()}`,
                        itemTagType: "primary",
                      })
                    }
                  >
                    Add tag
                  </Button>
                  <IconButton
                    type="button"
                    sx={{ p: "21px" }}
                    aria-label="search"
                  ></IconButton>
                </Box>
              </Stack>
            </Grid> */}
          </Grid>
          <pre>{JSON.stringify(errors, null, 2)}</pre>
        </CardContent>
        <CardActions>
          <Button
            type="submit"
            variant="contained"
            size="small"
            disabled={isSubmitting || Object.keys(errors).length > 0}
          >
            Submit
          </Button>
          <Button variant="contained" size="small" onClick={handleFormReset}>
            Reset
          </Button>
        </CardActions>
      </Card>
    </form>
  );
}

function LoadingCard() {
  return (
    <Container maxWidth="sm">
      <Card>
        <CardHeader title="Loading..." subheader="Please wait" />
        <CardContent>
          <Skeleton animation="wave" height={100} />
        </CardContent>
      </Card>
    </Container>
  );
}

function ErrorCard() {
  return (
    <Container maxWidth="sm">
      <Card>
        <CardHeader title="Error" subheader="An error occured" />
        <CardContent>
          <Alert severity="error">Please try again</Alert>
        </CardContent>
      </Card>
    </Container>
  );
}

export default function CreateItemFormWithTags() {
  const currentTimestamp = `${+new Date()}`;
  const newRaid: CreateRaidV1Request = {
    titles: [
      {
        title: `${currentTimestamp} - title`,
        type: {
          id: `https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json`,
          schemeUri: `https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1`,
        },
        startDate: new Date(),
      },
    ],
    access: {
      type: {
        id: "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json",
        schemeUri:
          "https://github.com/au-research/raid-metadata/tree/main/scheme/access/type/v1",
      },
      accessStatement: {
        statement: "This is a test statement",
        language: {
          id: "eng",
          schemeUri: "https://iso639-3.sil.org/",
        },
      },
    },
    dates: {
      startDate: new Date(),
    },
    contributors: [],
  };

  const defaultValues = newRaid;

  const handleRaidCreate = async (data: RaidDto): Promise<RaidDto> => {
    return await api.raid.createRaidV1({
      createRaidV1Request: {
        titles: data?.titles || ([] as Title[]),
        access: data?.access || ({} as Access),
        dates: data?.dates || ({} as Dates),
        contributors: data?.contributors || ([] as Contributor[]),
      },
    });
  };

  const api = useAuthApi();
  const mintRequest = useMutation(handleRaidCreate, {
    onSuccess: (mintResult) => {
      console.log("mintResult", mintResult);
    },
    onError: (error) => {
      console.log("error", error);
    },
  });

  return (
    <Container maxWidth="sm">
      <FormComponent
        defaultValues={defaultValues}
        onSubmit={async (data) => {
          mintRequest.mutate(data);
        }}
        // isSubmitting={itemWithTagsUpdateMutation.isLoading}
        isSubmitting={false}
      />
    </Container>
  );
}
