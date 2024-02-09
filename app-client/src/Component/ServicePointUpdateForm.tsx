import { zodResolver } from "@hookform/resolvers/zod";
import {
  Button,
  FormControlLabel,
  FormGroup,
  Grid,
  Switch,
  TextField,
} from "@mui/material";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import type { ServicePoint, UpdateServicePointRequest } from "Generated/Raidv2";
import { Controller, FormProvider, useForm } from "react-hook-form";
import { z } from "zod";

export default function ServicePointUpdateForm({
  servicePoint,
}: {
  servicePoint: ServicePoint;
}) {
  const api = useAuthApi();

  const queryClient = useQueryClient();

  const initalServicePointValues: UpdateServicePointRequest = {
    id: servicePoint.id,
    servicePointUpdateRequest: { ...servicePoint },
  };

  const updateServicePointRequestValidationSchema = z.object({
    id: z.number(),
    servicePointUpdateRequest: z.object({
      id: z.number(),
      name: z.string().min(3),
      identifierOwner: z.string(),
      adminEmail: z.string(),
      techEmail: z.string(),
      enabled: z.boolean(),
      prefix: z.string(),
      repositoryId: z.string(),
      appWritesEnabled: z.boolean(),
    }),
  });

  const form = useForm<UpdateServicePointRequest>({
    resolver: zodResolver(updateServicePointRequestValidationSchema),
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: { ...initalServicePointValues },
  });

  const handleUpdateSuccess = () => {
    queryClient.invalidateQueries({
      queryKey: ["servicePoints", servicePoint.id.toString()],
    });
    console.log("✅ Item updated");
    // form.reset();
    // navigate(`/items/${item.id}`);
  };

  const handleCreateError = (error: Error) => {
    console.log("error", error);
  };

  const updateServicePoint = async (
    servicePoint: UpdateServicePointRequest
  ) => {
    return await api.servicePoint.updateServicePoint(servicePoint);
  };

  const updateServicePointMutation = useMutation({
    mutationFn: updateServicePoint,
    onError: handleCreateError,
    onSuccess: handleUpdateSuccess,
  });

  const onSubmit = (item: UpdateServicePointRequest) => {
    updateServicePointMutation.mutate(item);
  };

  return (
    <FormProvider {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} autoComplete="off">
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6} md={4}>
            <Controller
              name="servicePointUpdateRequest.name"
              control={form.control}
              render={({ field }) => (
                <TextField
                  error={!!form.formState.errors?.servicePointUpdateRequest?.name}
                  helperText={
                    form.formState.errors?.servicePointUpdateRequest?.name?.message
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
              name="servicePointUpdateRequest.repositoryId"
              control={form.control}
              render={({ field }) => (
                <TextField
                  label="Repository ID"
                  variant="outlined"
                  size="small"
                  fullWidth
                  {...field}
                  value={field.value}
                  error={!!form.formState.errors?.servicePointUpdateRequest?.repositoryId}
                />
              )}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={4}>
            <Controller
              name="servicePointUpdateRequest.prefix"
              control={form.control}
              render={({ field }) => (
                <TextField
                  label="Prefix"
                  variant="outlined"
                  size="small"
                  fullWidth
                  {...field}
                  value={field.value}
                  error={!!form.formState.errors?.servicePointUpdateRequest?.prefix}
                />
              )}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={4}>
            <Controller
              name="servicePointUpdateRequest.identifierOwner"
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
                  error={!!form.formState.errors?.servicePointUpdateRequest?.identifierOwner}
                />
              )}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={4}>
            <Controller
              name="servicePointUpdateRequest.adminEmail"
              control={form.control}
              render={({ field }) => (
                <TextField
                  label="Admin email"
                  variant="outlined"
                  size="small"
                  fullWidth
                  {...field}
                  value={field.value}
                  error={!!form.formState.errors?.servicePointUpdateRequest?.adminEmail}
                />
              )}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={4}>
            <Controller
              name="servicePointUpdateRequest.techEmail"
              control={form.control}
              render={({ field }) => (
                <TextField
                  label="Tech email"
                  variant="outlined"
                  size="small"
                  fullWidth
                  {...field}
                  value={field.value}
                  error={!!form.formState.errors?.servicePointUpdateRequest?.techEmail}
                />
              )}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={4}>
            <Controller
              name="servicePointUpdateRequest.password"
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
                  error={!!form.formState.errors?.servicePointUpdateRequest?.password}
                />
              )}
            />
          </Grid>
          <Grid item xs={12} sm={12} md={12}>
            <Controller
              name="servicePointUpdateRequest.enabled"
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
              name="servicePointUpdateRequest.appWritesEnabled"
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
        <Button variant="outlined" type="submit" sx={{ mt: 3 }}>
          Update service point
        </Button>
      </form>
    </FormProvider>
  );
}