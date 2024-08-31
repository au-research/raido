import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import { contributorGenerator } from "@/entities/contributor/data-components/contributor-generator";
import ContributorDetailsFormComponent from "@/entities/contributor/form-components/ContributorDetailsFormComponent";
import {
  RaidCreateRequest,
  RaidDto,
  RaidUpdateRequest,
} from "@/generated/raid";
import LoadingPage from "@/pages/LoadingPage";
import { fetchRaidOrcidContributors } from "@/services/contributors";
import { OrcidContributor } from "@/types";
import { AddCircleOutline as AddCircleOutlineIcon } from "@mui/icons-material";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Divider,
  IconButton,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useMemo } from "react";
import {
  Control,
  FieldErrors,
  UseFormTrigger,
  useFieldArray,
} from "react-hook-form";
import { useParams } from "react-router-dom";

interface ContributorsFormComponentProps {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
  raidData?: RaidCreateRequest | RaidUpdateRequest;
}

export default function ContributorsFormComponent({
  control,
  errors,
  trigger,
  raidData,
}: ContributorsFormComponentProps) {
  const { prefix, suffix } = useParams() as { prefix: string; suffix: string };
  const { fields, append, remove } = useFieldArray({
    keyName: "fieldArrayId",
    control,
    name: "contributor",
  });

  const handleAddContributor = useCallback(() => {
    append(contributorGenerator());
    trigger("contributor");
  }, [append, trigger]);

  const handleRemoveContributor = useCallback(
    (index: number) => {
      remove(index);
    },
    [remove]
  );

  const errorMessage = useMemo(() => errors.title?.message, [errors.title]);

  const raidOrcidContributorsQuery = useQuery<OrcidContributor[]>({
    queryKey: ["orcid-contributors", prefix, suffix],
    queryFn: () =>
      fetchRaidOrcidContributors({
        handle: `${prefix}/${suffix}`,
      }),
  });

  if (raidOrcidContributorsQuery.isPending) {
    return <LoadingPage />;
  }

  if (raidOrcidContributorsQuery.isError) {
    return <ErrorAlertComponent error={raidOrcidContributorsQuery.error} />;
  }

  return (
    <Card
      sx={{
        borderLeft: errors.contributor ? "3px solid" : "none",
        borderLeftColor: "error.main",
      }}
    >
      <CardHeader
        title="Contributors"
        action={
          <Tooltip title="Add Contributor" placement="right">
            <IconButton
              aria-label="Add Contributor"
              onClick={handleAddContributor}
            >
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        }
      />
      <CardContent>
        <Stack gap={2}>
          <Box>
            {errorMessage && (
              <Typography variant="body2" color="error" textAlign="center">
                {errorMessage}
              </Typography>
            )}
            {fields.length === 0 && (
              <Typography
                variant="body2"
                color="text.secondary"
                textAlign="center"
              >
                No contributors defined
              </Typography>
            )}
          </Box>
          <Stack divider={<Divider />} gap={5} data-testid="titles-form">
            {fields.map((field, index) => {
              const orcidContributorData =
                raidOrcidContributorsQuery.data?.find(
                  (contributor) => contributor.email === field.id
                );
              return (
                <ContributorDetailsFormComponent
                  key={field.fieldArrayId}
                  control={control}
                  errors={errors}
                  handleRemoveContributor={handleRemoveContributor}
                  index={index}
                  trigger={trigger}
                  orcidContributorData={orcidContributorData}
                  raidData={raidData!}
                />
              );
            })}
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
}
