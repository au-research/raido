import SingletonServicePointApi from "@/SingletonServicePointApi";
import type { CreateServicePointRequest } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Button,
  FormControlLabel,
  FormGroup,
  Grid,
  Snackbar,
  Switch,
  TextField,
} from "@mui/material";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import React from "react";
import { Controller, FormProvider, useForm } from "react-hook-form";
import { z } from "zod";

export default function ServicePointCreateForm() {
  const queryClient = useQueryClient();
  const api = SingletonServicePointApi.getInstance();
  const { keycloak } = useCustomKeycloak();

  const initalServicePointValues: CreateServicePointRequest = {
    servicePointCreateRequest: {
      name: "",
      identifierOwner: "",
      adminEmail: "",
      techEmail: "",
      enabled: false,
      password: "",
      prefix: "",
      repositoryId: "",
      appWritesEnabled: false,
      groupId: "",
    },
  };

  const createServicePointRequestValidationSchema = z.object({
    servicePointCreateRequest: z.object({
      name: z.string().min(3),
      identifierOwner: z.string(),
      adminEmail: z.string(),
      techEmail: z.string(),
      enabled: z.boolean(),
      password: z.string().min(8),
      prefix: z.string(),
      repositoryId: z.string(),
      appWritesEnabled: z.boolean(),
    }),
  });

  const form = useForm<CreateServicePointRequest>({
    resolver: zodResolver(createServicePointRequestValidationSchema),
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: { ...initalServicePointValues },
  });

  const handleCreateSuccess = () => {
    queryClient.invalidateQueries({ queryKey: ["servicePoints"] });
    console.log("✅ Item created");
    form.reset();
    setSnackbarOpen(true);
  };

  const handleCreateError = (error: Error) => {
    console.log("error", error);
  };

  const createServicePoint = async (
    servicePoint: CreateServicePointRequest
  ) => {
    return await api.createServicePoint(servicePoint, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${keycloak.token}`,
      },
    });
  };

  const createServicePointMutation = useMutation({
    mutationFn: createServicePoint,
    onError: handleCreateError,
    onSuccess: handleCreateSuccess,
  });

  const onSubmit = (item: CreateServicePointRequest) => {
    createServicePointMutation.mutate(item);
  };

  const [snackbarOpen, setSnackbarOpen] = React.useState<boolean>(false);

  const handleSnackbarClose = (
    event: React.SyntheticEvent | Event,
    reason?: string
  ) => {
    if (reason === "clickaway") {
      return;
    }

    setSnackbarOpen(false);
  };

  return (
    <>
      <FormProvider {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} autoComplete="off">
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6} md={4}>
              <Controller
                name="servicePointCreateRequest.name"
                control={form.control}
                render={({ field }) => (
                  <TextField
                    error={
                      !!form.formState.errors?.servicePointCreateRequest?.name
                    }
                    helperText={
                      form.formState.errors?.servicePointCreateRequest?.name
                        ?.message
                    }
                    label="Service point name"
                    variant="outlined"
                    size="small"
                    fullWidth
                    {...field}
                    value={field.value}
                  />
                )}
              />
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
              <Controller
                name="servicePointCreateRequest.repositoryId"
                control={form.control}
                render={({ field }) => (
                  <TextField
                    label="Repository ID"
                    variant="outlined"
                    size="small"
                    fullWidth
                    {...field}
                    value={field.value}
                    error={
                      !!form.formState.errors?.servicePointCreateRequest
                        ?.repositoryId
                    }
                  />
                )}
              />
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
              <Controller
                name="servicePointCreateRequest.prefix"
                control={form.control}
                render={({ field }) => (
                  <TextField
                    label="Prefix"
                    variant="outlined"
                    size="small"
                    fullWidth
                    {...field}
                    value={field.value}
                    error={
                      !!form.formState.errors?.servicePointCreateRequest?.prefix
                    }
                  />
                )}
              />
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
              <Controller
                name="servicePointCreateRequest.groupId"
                control={form.control}
                render={({ field }) => (
                  <TextField
                    label="Group ID"
                    variant="outlined"
                    size="small"
                    fullWidth
                    {...field}
                    value={field.value}
                    error={
                      !!form.formState.errors?.servicePointCreateRequest
                        ?.groupId
                    }
                  />
                )}
              />
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
              <Controller
                name="servicePointCreateRequest.identifierOwner"
                control={form.control}
                render={({ field }) => (
                  <TextField
                    label="Identifier Owner"
                    variant="outlined"
                    helperText="ROR Identifier. e.g. https://ror.org/038sjwq14"
                    size="small"
                    fullWidth
                    {...field}
                    value={field.value}
                    error={
                      !!form.formState.errors?.servicePointCreateRequest
                        ?.identifierOwner
                    }
                  />
                )}
              />
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
              <Controller
                name="servicePointCreateRequest.adminEmail"
                control={form.control}
                render={({ field }) => (
                  <TextField
                    label="Admin email"
                    variant="outlined"
                    size="small"
                    fullWidth
                    {...field}
                    value={field.value}
                    error={
                      !!form.formState.errors?.servicePointCreateRequest
                        ?.adminEmail
                    }
                  />
                )}
              />
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
              <Controller
                name="servicePointCreateRequest.techEmail"
                control={form.control}
                render={({ field }) => (
                  <TextField
                    label="Tech email"
                    variant="outlined"
                    size="small"
                    fullWidth
                    {...field}
                    value={field.value}
                    error={
                      !!form.formState.errors?.servicePointCreateRequest
                        ?.techEmail
                    }
                  />
                )}
              />
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
              <Controller
                name="servicePointCreateRequest.password"
                control={form.control}
                render={({ field }) => (
                  <TextField
                    label="Password"
                    type="password"
                    variant="outlined"
                    size="small"
                    fullWidth
                    {...field}
                    value={field.value}
                    error={
                      !!form.formState.errors?.servicePointCreateRequest
                        ?.password
                    }
                    helperText={
                      form.formState.errors?.servicePointCreateRequest?.password
                        ?.message
                    }
                  />
                )}
              />
            </Grid>
            <Grid item xs={12} sm={12} md={12}>
              <Controller
                name="servicePointCreateRequest.enabled"
                control={form.control}
                render={({ field }) => (
                  <FormGroup>
                    <FormControlLabel
                      control={
                        <Switch {...field} defaultChecked={!!field.value} />
                      }
                      label="Service point enabled?"
                    />
                  </FormGroup>
                )}
              />
            </Grid>
            <Grid item xs={12} sm={12} md={12}>
              <Controller
                name="servicePointCreateRequest.appWritesEnabled"
                control={form.control}
                render={({ field }) => (
                  <FormGroup>
                    <FormControlLabel
                      control={
                        <Switch {...field} defaultChecked={!!field.value} />
                      }
                      label="Enable editing in app?"
                    />
                  </FormGroup>
                )}
              />
            </Grid>
          </Grid>
          <Button
            variant="outlined"
            type="submit"
            sx={{ mt: 3 }}
            disabled={Object.keys(form.formState.errors).length > 0}
          >
            Create service point
          </Button>
        </form>
      </FormProvider>
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={3000}
        onClose={handleSnackbarClose}
        message="✅ Service point created successfully"
      />
    </>
  );
}
