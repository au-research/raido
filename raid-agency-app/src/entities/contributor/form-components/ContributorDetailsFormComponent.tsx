import { contributorPositionGenerator } from "@/entities/contributor-position/data-components/contributor-position-generator";
import ContributorPositionDetailsFormComponent from "@/entities/contributor-position/form-components/ContributorPositionDetailsFormComponent";
import ContributorRoleDetailsFormComponent from "@/entities/contributor-role/form-components/ContributorRoleDetailsFormComponent";
import { CheckboxField } from "@/fields/CheckboxField";
import { TextInputField } from "@/fields/TextInputField";
import {
  RaidCreateRequest,
  RaidDto,
  RaidUpdateRequest,
} from "@/generated/raid";

import {
  Button,
  Grid,
  IconButton,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import { useCallback } from "react";
import {
  Control,
  FieldErrors,
  useFieldArray,
  UseFormTrigger,
} from "react-hook-form";
import { useMutation } from "@tanstack/react-query";
import { OrcidContributor } from "@/types";
import { useParams } from "react-router-dom";
import { DisplayItem } from "@/components/DisplayItem";

interface ContributorDetailsFormComponentProps {
  control: Control<RaidDto>;
  index: number;
  errors: FieldErrors<RaidDto>;
  handleRemoveContributor: (index: number) => void;
  trigger: UseFormTrigger<RaidDto>;
  orcidContributorData?: OrcidContributor;
  raidData: RaidCreateRequest | RaidUpdateRequest;
}

export default function ContributorDetailsFormComponent({
  control,
  index,
  errors,
  handleRemoveContributor,
  orcidContributorData,
  raidData,
}: ContributorDetailsFormComponentProps) {
  const { prefix, suffix } = useParams() as { prefix: string; suffix: string };
  const orcidPayload = {
    title: {
      title: {
        value: raidData.title?.map((title) => title.text).join(", "),
      },
    },
    "short-description": raidData.description
      ?.map((description) => description.text)
      .join(", "),
    type: "online-resource",
    "external-ids": {
      "external-id": [
        {
          "external-id-type": "raid",
          "external-id-value": `https://app.test.raid.org.au/raids/${prefix}/${suffix} ${Date.now().toString()}`,
          "external-id-url": {
            value: `${prefix}/${suffix}`,
          },
          "external-id-relationship": "self",
        },
      ],
    },
    url: {
      value: `https://app.test.raid.org.au/raids/${prefix}/${suffix}`,
    },
  };

  const writeToOrcidMutation = useMutation({
    mutationKey: ["write-to-orcid"],
    mutationFn: async ({
      accessToken,
      email,
    }: {
      accessToken: string;
      email: string;
    }) => {
      const response = await fetch(
        `https://orcid.test.raid.org.au/orcid-update`,
        {
          method: "POST",
          headers: {
            Accept: "application/json",
            "Content-Type": "application/json",
            Authorization: `Bearer ${accessToken}`,
          },
          body: JSON.stringify({
            email,
            data: orcidPayload,
          }),
        }
      );
      const data = await response.json();
      alert(JSON.stringify(data));
      return data;
    },
  });

  const positionFieldArray = useFieldArray({
    control,
    name: `contributor.${index}.position`,
  });

  const handleAddContributorPosition = useCallback(() => {
    positionFieldArray.append(contributorPositionGenerator());
  }, [positionFieldArray]);

  const handleRemoveContributorPosition = useCallback(
    (index: number) => {
      positionFieldArray.remove(index);
    },
    [positionFieldArray]
  );

  return (
    <Stack gap={2}>
      <Stack direction="row" alignItems="flex-start" gap={1}>
        <Grid container spacing={2}>
          <TextInputField
            control={control}
            errors={errors}
            width={8}
            formFieldProps={{
              name: `contributor.${index}.id`,
              type: "text",
              label: "ORCID ID",
              placeholder: "ORCID ID",
              helperText: "",
              errorText: "",
            }}
          />
          <CheckboxField
            control={control}
            errors={errors}
            width={2}
            formFieldProps={{
              name: `contributor.${index}.leader`,
              label: "Leader?",
            }}
          />
          <CheckboxField
            control={control}
            errors={errors}
            width={2}
            formFieldProps={{
              name: `contributor.${index}.contact`,
              label: "Contact?",
            }}
          />
          <DisplayItem
            label="Name (for reference)"
            value={orcidContributorData?.name}
            width={4}
            small={true}
          />
          <DisplayItem
            label="ORCID ID (for reference)"
            value={orcidContributorData?.orcid}
            width={4}
            small={true}
          />
        </Grid>

        <Tooltip title="Remove contributor" placement="right">
          <IconButton
            aria-label="Remove contributor"
            onClick={() => handleRemoveContributor(index)}
          >
            <RemoveCircleOutlineIcon />
          </IconButton>
        </Tooltip>
      </Stack>
      {orcidContributorData &&
        orcidContributorData.status === "invitation accepted" && (
          <Button
            variant="outlined"
            sx={{ width: "200px" }}
            onClick={() => {
              writeToOrcidMutation.mutate({
                accessToken: orcidContributorData.access_token,
                email: orcidContributorData.email,
              });
            }}
          >
            Write to ORCID
          </Button>
        )}

      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <Typography variant="body2">Position</Typography>
        <IconButton
          aria-label="Add contributor position"
          onClick={handleAddContributorPosition}
        >
          <AddCircleOutlineIcon />
        </IconButton>
      </Stack>

      {positionFieldArray.fields.map((field, i) => (
        <Stack direction="row" justifyContent="space-between" key={i}>
          <ContributorPositionDetailsFormComponent
            control={control}
            errors={errors}
            contributorIndex={index}
            positionIndex={i}
          />
          <Tooltip title="Remove contributor position">
            <IconButton
              aria-label="Add contributor position"
              onClick={() => {
                handleRemoveContributorPosition(i);
              }}
            >
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        </Stack>
      ))}

      <Typography variant="body2">Roles</Typography>

      <ContributorRoleDetailsFormComponent
        control={control}
        errors={errors}
        index={index}
      />
    </Stack>
  );
}
