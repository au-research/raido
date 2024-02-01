import {
  Card,
  CardContent,
  CardHeader,
  Checkbox,
  Container,
  FormControl,
  FormControlLabel,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import { PrimaryActionButton, SecondaryButton } from "Component/AppButton";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import {
  CreateServicePointRequest,
  ServicePoint,
  UpdateServicePointRequest,
  type ServicePointCreateRequest,
} from "Generated/Raidv2";
import { RqQuery } from "Util/ReactQueryUtil";
import { useState } from "react";
import { useNavigate } from "react-router";
import { useParams } from "react-router-dom";

export function ServicePointPage() {
  const navigate = useNavigate();
  const { servicePointId: servicePointIdParam } = useParams() as {
    servicePointId: string;
  };

  const [servicePointId, setServicePointId] = useState(servicePointIdParam);
  return (
    <Container>
      <ServicePointContainer
        servicePointId={+servicePointId}
        onCreate={(createdId) => {
          navigate(`service-point/${createdId}`, {
            replace: true,
          });
          setServicePointId(createdId.toString());
        }}
      />
    </Container>
  );
}

function ServicePointContainer({
  servicePointId,
  onCreate,
}: {
  servicePointId: number | undefined;
  onCreate: (servicePointId: number) => void;
}) {
  const api = useAuthApi();
  const queryClient = useQueryClient();
  const queryName = "readServicePoint";
  const [formData, setFormData] = useState({
    techEmail: "",
    adminEmail: "",
    enabled: true,
    appWritesEnabled: true,
  } as ServicePoint);
  const query: RqQuery<ServicePoint> = useQuery(
    [queryName, servicePointId],
    async () => {
      if (servicePointId) {
        let servicePoint = await api.servicePoint.findServicePointById({
          id: servicePointId,
        });
        setFormData(servicePoint);
        return servicePoint;
      } else {
        return formData;
      }
    }
  );
  const updateRequest = useMutation(
    async (data: UpdateServicePointRequest) => {
      const result = await api.servicePoint.updateServicePoint(data);
      if (!servicePointId) {
        onCreate(result.id);
      }
    },
    {
      onSuccess: async () => {
        await queryClient.invalidateQueries([queryName]);
      },
    }
  );

  const createRequest = useMutation(
    async (data: CreateServicePointRequest) => {
      const result = await api.servicePoint.createServicePoint(data);
      if (!servicePointId) {
        onCreate(result.id);
      }
    },
    {
      onSuccess: async () => {
        await queryClient.invalidateQueries([queryName]);
      },
    }
  );

  const createNewServicePoint = (formData: ServicePoint) => {
    const servicePointCreateRequest: ServicePointCreateRequest = {
      ...formData,
    };

    const createServicePointRequest: CreateServicePointRequest = {
      servicePointCreateRequest,
    };

    createRequest.mutate(createServicePointRequest);
  };

  const updateExistingServicePoint = (formData: ServicePoint) => {
    const updateServicePointRequest: UpdateServicePointRequest = {
      id: formData.id,
      servicePoint: formData,
    };

    updateRequest.mutate(updateServicePointRequest);
  };

  const handleFormSubmit = (formData: ServicePoint) => {
    formData.id
      ? updateExistingServicePoint(formData)
      : createNewServicePoint(formData);
  };

  if (query.error) {
    return <CompactErrorPanel error={query.error} />;
  }

  if (!query.data) {
    if (query.isLoading) {
      return <Typography>loading...</Typography>;
    } else {
      console.log("unexpected state", query);
      return <Typography>unexpected state</Typography>;
    }
  }

  const isWorking = query.isLoading || updateRequest.isLoading;

  return (
    <Card>
      <CardHeader title="Service point" />
      <CardContent>
        <form
          autoComplete="off"
          onSubmit={(e) => {
            e.preventDefault();
            handleFormSubmit(formData);
          }}
        >
          <Stack spacing={2}>
            <FormControl focused autoCorrect="off" autoCapitalize="on">
              <TextField
                id="name"
                label="Name"
                variant="outlined"
                disabled={isWorking}
                value={formData.name || ""}
                onChange={(e) => {
                  setFormData({ ...formData, name: e.target.value });
                }}
              />
            </FormControl>

            <FormControl focused autoCorrect="off" autoCapitalize="on">
              <TextField
                id="identifierOwner"
                label="Identifier Owner"
                variant="outlined"
                disabled={isWorking}
                value={formData.identifierOwner || ""}
                onChange={(e) => {
                  setFormData({ ...formData, identifierOwner: e.target.value });
                }}
              />
            </FormControl>

            <FormControl>
              <TextField
                id="adminEmail"
                label="Admin email"
                variant="outlined"
                disabled={isWorking}
                value={formData.adminEmail || ""}
                onChange={(e) => {
                  setFormData({ ...formData, adminEmail: e.target.value });
                }}
              />
            </FormControl>
            <TextField
              id="techEmail"
              label="Tech email"
              variant="outlined"
              disabled={isWorking}
              value={formData.techEmail || ""}
              onChange={(e) => {
                setFormData({ ...formData, techEmail: e.target.value });
              }}
            />
            <FormControl>
              <FormControlLabel
                disabled={isWorking}
                label="Enabled"
                labelPlacement="start"
                style={{
                  /* by default, MUI lays this out as <checkbox><label>.
                                     Doing `labelPlacement=start`, flips that around, but ends up
                                     right-justifying the content, so `marginRight=auto` pushes it back
                                     across to the left and `marginLeft=0` aligns nicely. */
                  marginLeft: 0,
                  marginRight: "auto",
                }}
                control={
                  <Checkbox
                    checked={formData.enabled ?? true}
                    onChange={() => {
                      setFormData({ ...formData, enabled: !formData.enabled });
                    }}
                  />
                }
              />
            </FormControl>

            <FormControl>
              <FormControlLabel
                disabled={isWorking}
                label="Enable editing in app"
                labelPlacement="start"
                style={{
                  /* by default, MUI lays this out as <checkbox><label>.
                                     Doing `labelPlacement=start`, flips that around, but ends up
                                     right-justifying the content, so `marginRight=auto` pushes it back
                                     across to the left and `marginLeft=0` aligns nicely. */
                  marginLeft: 0,
                  marginRight: "auto",
                }}
                control={
                  <Checkbox
                    checked={formData.appWritesEnabled ?? true}
                    onChange={() => {
                      setFormData({
                        ...formData,
                        appWritesEnabled: !formData.appWritesEnabled,
                      });
                    }}
                  />
                }
              />
            </FormControl>

            <Stack direction={"row"} spacing={2}>
              <SecondaryButton
                onClick={() => window.history.back()}
                disabled={updateRequest.isLoading}
              >
                Back
              </SecondaryButton>
              <PrimaryActionButton
                type="submit"
                context={"update service point"}
                disabled={
                  isWorking || !formData.name || !formData.identifierOwner
                }
                isLoading={updateRequest.isLoading}
                error={updateRequest.error}
              >
                {servicePointId ? "Update" : "Create"}
              </PrimaryActionButton>
            </Stack>
          </Stack>
        </form>
      </CardContent>
    </Card>
  );
}
