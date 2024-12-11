import accessGenerator from "@/entities/access/data-components/access-generator";
import AccessForm from "@/entities/access/AccessForm";

import alternateIdentifierGenerator from "@/entities/alternateIdentifier/data-components/alternate-identifier-generator";
import AlternateIdentifierForm from "@/entities/alternateIdentifier/AlternateIdentifierForm";

import alternateUrlGenerator from "@/entities/alternateUrl/data-components/alternate-url-generator";
import AlternateUrlForm from "@/entities/alternateUrl/AlternateUrlForm";

import contributorGenerator from "@/entities/contributor/data-components/contributor-generator";
import ContributorForm from "@/entities/contributor/ContributorForm";

import contributorPositionGenerator from "@/entities/contributor/position/data-components/contributor-position-generator";
import ContributorPositionForm from "@/entities/contributor/position/ContributorPositionForm";

import contributorRoleGenerator from "@/entities/contributor/role/data-components/contributor-role-generator";
import ContributorRoleForm from "@/entities/contributor/role/ContributorRoleForm";

import dateGenerator from "@/entities/date/data-components/date-generator";
import DateForm from "@/entities/date/DateForm";

import descriptionGenerator from "@/entities/description/data-components/description-generator";
import DescriptionForm from "@/entities/description/DescriptionForm";

import organisationGenerator from "@/entities/organisation/data-components/organisation-generator";
import OrganisationForm from "@/entities/organisation/OrganisationForm";

import organisationRoleGenerator from "@/entities/organisation/role/data-components/organisation-role-generator";
import OrganisationRoleForm from "@/entities/organisation/role/OrganisationRoleForm";

import relatedObjectGenerator from "@/entities/relatedObject/data-components/related-object-generator";
import RelatedObjectForm from "@/entities/relatedObject/RelatedObjectForm";

import relatedObjectCategoryGenerator from "@/entities/relatedObject/category/data-components/related-object-category-generator";
import RelatedObjectCategoryForm from "@/entities/relatedObject/category/RelatedObjectCategoryForm";

import relatedRaidGenerator from "@/entities/relatedRaid/data-components/related-raid-generator";
import RelatedRaidForm from "@/entities/relatedRaid/RelatedRaidForm";

import spatialCoverageGenerator from "@/entities/spatialCoverage/spatial-coverage-generator";
import SpatialCoverageForm from "@/entities/spatialCoverage/SpatialCoverageForm";

import spatialCoveragePlaceGenerator from "@/entities/spatialCoverage/place/spatial-coverage-place-generator";
import SpatialCoveragePlaceForm from "@/entities/spatialCoverage/place/SpatialCoveragePlaceForm";

import subjectGenerator from "@/entities/subject/data-components/subject-generator";
import SubjectForm from "@/entities/subject/SubjectForm";

import subjectKeywordGenerator from "@/entities/subject/keyword/data-components/subject-keyword-generator";
import SubjectKeywordForm from "@/entities/subject/keyword/SubjectKeywordForm";

import titleGenerator from "@/entities/title/data-components/title-generator";
import TitleForm from "@/entities/title/TitleForm";

import traditionalKnowledgeLabelGenerator from "@/entities/traditionalKnowledgeLabel/traditional-knowledge-label-generator";
import TraditionalKnowledgeLabelForm from "@/entities/traditionalKnowledgeLabel/TraditionalKnowledgeLabelForm";

import { ChildConfig } from "@/types";

import AnchorButtons from "@/components/anchor-buttons/AnchorButtons";
import { DynamicForm } from "@/components/dynamic-form";
import ValidationFormSchema from "@/entities/validation-schema";
import { RaidCreateRequest, RaidDto } from "@/generated/raid";
import { zodResolver } from "@hookform/resolvers/zod";
import { Close as CloseIcon, Save as SaveIcon } from "@mui/icons-material";
import {
  Card,
  CardContent,
  CardHeader,
  Fab,
  Stack,
  Tooltip,
} from "@mui/material";
import { memo, useCallback, useEffect, useMemo, useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { Link } from "react-router-dom";

type BaseConfiguration = {
  id: string;
  label: string;
  labelPlural: string;
  entityKey: keyof RaidDto;
  DetailsFormComponent: any;
  generator: () => any;
};

type FlatFormConfiguration = BaseConfiguration & {
  component: typeof FlatFormComponent;
  childConfigs?: ChildConfig[];
};

type MultiFormConfiguration = BaseConfiguration & {
  component: typeof DynamicForm;
  ChildFormComponent?: React.ComponentType<{ index: number }>;
  childConfigs?: ChildConfig[];
};

type FormConfiguration = FlatFormConfiguration | MultiFormConfiguration;

export const FlatFormComponent = memo(
  ({
    DetailsFormComponent,
    labelPlural,
  }: {
    DetailsFormComponent: React.ComponentType<{}>;
    labelPlural: string;
  }) => (
    <Card>
      <CardHeader title={labelPlural} />
      <CardContent>
        <DetailsFormComponent />
      </CardContent>
    </Card>
  )
);

export const RaidForm = memo(
  ({
    raidData,
    onSubmit,
    isSubmitting,
    prefix,
    suffix,
  }: {
    raidData: RaidCreateRequest | RaidDto;
    onSubmit: (data: RaidDto) => void;
    isSubmitting: boolean;
    prefix: string;
    suffix: string;
  }) => {
    const [isInitialLoad, setIsInitialLoad] = useState(true);

    const configurations: FormConfiguration[] = useMemo(
      () => [
        {
          id: "date",
          label: "Date",
          labelPlural: "Dates",
          entityKey: "date" as keyof RaidDto,
          component: FlatFormComponent,
          DetailsFormComponent: DateForm,
          generator: dateGenerator,
        },
        {
          id: "title",
          label: "Title",
          labelPlural: "Titles",
          entityKey: "title" as keyof RaidDto,
          component: DynamicForm,
          DetailsFormComponent: TitleForm,
          ChildFormComponent: undefined,
          generator: titleGenerator,
        },
        {
          id: "description",
          label: "Description",
          labelPlural: "Descriptions",
          entityKey: "description" as keyof RaidDto,
          component: DynamicForm,
          DetailsFormComponent: DescriptionForm,
          ChildFormComponent: TitleForm,
          generator: descriptionGenerator,
        },
        {
          id: "organisation",
          label: "Organisation",
          labelPlural: "Organisations",
          entityKey: "organisation" as keyof RaidDto,
          component: DynamicForm,
          DetailsFormComponent: OrganisationForm,
          generator: organisationGenerator,
          childConfigs: [
            {
              fieldKey: "role",
              label: "Role",
              labelPlural: "Roles",
              DetailsComponent: OrganisationRoleForm,
              generator: organisationRoleGenerator,
            },
          ],
        },
        {
          id: "contributor",
          label: "Contributor",
          labelPlural: "Contributors",
          entityKey: "contributor" as keyof RaidDto,
          component: DynamicForm,
          DetailsFormComponent: ContributorForm,
          generator: contributorGenerator,
          childConfigs: [
            {
              fieldKey: "role",
              label: "Role",
              labelPlural: "Roles",
              DetailsComponent: ContributorRoleForm,
              generator: contributorRoleGenerator,
            },
            {
              fieldKey: "position",
              label: "Position",
              labelPlural: "Positions",
              DetailsComponent: ContributorPositionForm,
              generator: contributorPositionGenerator,
            },
          ],
        },
        {
          id: "relatedObject",
          label: "Related Object",
          labelPlural: "Related Objects",
          entityKey: "relatedObject" as keyof RaidDto,
          component: DynamicForm,
          DetailsFormComponent: RelatedObjectForm,
          generator: relatedObjectGenerator,
          childConfigs: [
            {
              fieldKey: "category",
              label: "Category",
              labelPlural: "Categories",
              DetailsComponent: RelatedObjectCategoryForm,
              generator: relatedObjectCategoryGenerator,
            },
          ],
        },
        {
          id: "alternateIdentifier",
          label: "Alternate Identifier",
          labelPlural: "Alternate Identifiers",
          entityKey: "alternateIdentifier" as keyof RaidDto,
          component: DynamicForm,
          DetailsFormComponent: AlternateIdentifierForm,
          generator: alternateIdentifierGenerator,
        },
        {
          id: "alternateUrl",
          label: "Alternate URL",
          labelPlural: "Alternate URLs",
          entityKey: "alternateUrl" as keyof RaidDto,
          component: DynamicForm,
          DetailsFormComponent: AlternateUrlForm,
          generator: alternateUrlGenerator,
        },
        {
          id: "relatedRaid",
          label: "Related RAiD",
          labelPlural: "Related RAiDs",
          entityKey: "relatedRaid" as keyof RaidDto,
          component: DynamicForm,
          DetailsFormComponent: RelatedRaidForm,
          generator: relatedRaidGenerator,
        },
        {
          id: "access",
          label: "Access",
          labelPlural: "Access",
          entityKey: "access" as keyof RaidDto,
          component: FlatFormComponent,
          DetailsFormComponent: AccessForm,
          generator: accessGenerator,
        },
        {
          id: "subject",
          label: "Subject",
          labelPlural: "Subjects",
          entityKey: "subject" as keyof RaidDto,
          component: DynamicForm,
          DetailsFormComponent: SubjectForm,
          generator: subjectGenerator,
          childConfigs: [
            {
              fieldKey: "keyword",
              label: "Keyword",
              labelPlural: "Keywords",
              DetailsComponent: SubjectKeywordForm,
              generator: subjectKeywordGenerator,
            },
          ],
        },

        {
          id: "traditionalKnowledgeLabel",
          label: "Traditional Knowledge Label",
          labelPlural: "Traditional Knowledge Labels",
          entityKey: "traditionalKnowledgeLabel" as keyof RaidDto,
          component: DynamicForm,
          DetailsFormComponent: TraditionalKnowledgeLabelForm,
          generator: traditionalKnowledgeLabelGenerator,
        },
        {
          id: "spatialCoverage",
          label: "Spatial Coverage",
          labelPlural: "Spatial Coverages",
          entityKey: "spatialCoverage" as keyof RaidDto,
          component: DynamicForm,
          DetailsFormComponent: SpatialCoverageForm,
          generator: spatialCoverageGenerator,
          childConfigs: [
            {
              fieldKey: "place",
              label: "Place",
              labelPlural: "Places",
              DetailsComponent: SpatialCoveragePlaceForm,
              generator: spatialCoveragePlaceGenerator,
            },
          ],
        },
      ],
      []
    );

    const formMethods = useForm<RaidDto>({
      defaultValues: raidData,
      resolver: zodResolver(ValidationFormSchema),
      mode: "onChange",
      reValidateMode: "onChange",
    });

    const { control, trigger, formState } = formMethods;
    const isFormValid = Object.keys(formState.errors).length === 0;

    const handleSubmit = useCallback(
      (data: RaidDto) => {
        onSubmit(data);
      },
      [onSubmit]
    );

    useEffect(() => {
      if (isInitialLoad) {
        setIsInitialLoad(false);
      }
    }, [isInitialLoad]);

    return (
      <FormProvider {...formMethods}>
        <form
          onSubmit={formMethods.handleSubmit(handleSubmit)}
          autoComplete="off"
          noValidate
        >
          <Stack
            gap={2}
            sx={{
              position: "fixed",
              bottom: "16px",
              right: "16px",
              zIndex: 1000,
            }}
            alignItems="end"
          >
            <Tooltip title="Cancel" placement="left">
              <Fab
                component={Link}
                color="primary"
                size="small"
                to={
                  raidData?.identifier?.id ? `/raids/${prefix}/${suffix}` : "/"
                }
              >
                <CloseIcon />
              </Fab>
            </Tooltip>
            <Tooltip title="Save changes" placement="left">
              <Fab
                variant="extended"
                color="primary"
                component="button"
                type="submit"
                disabled={isSubmitting || !isFormValid}
                data-testid="save-raid-button"
              >
                <SaveIcon sx={{ mr: 1 }} />
                {isSubmitting ? "Saving..." : "Save"}
              </Fab>
            </Tooltip>
          </Stack>

          <Stack spacing={2} data-testid="raid-form">
            <AnchorButtons raidData={raidData} errors={formState.errors} />
            <Stack spacing={2}>
              {configurations.map((config) => {
                const commonProps = {
                  control,
                  errors: formState.errors,
                  trigger,
                  label: config.label,
                  labelPlural: config.labelPlural,
                  entityKey: config.entityKey,
                  DetailsFormComponent: config.DetailsFormComponent,
                  generator: config.generator,
                };

                if (config.component === FlatFormComponent) {
                  return (
                    <FlatFormComponent
                      key={config.id}
                      labelPlural={config.labelPlural}
                      DetailsFormComponent={config.DetailsFormComponent}
                    />
                  );
                }

                if (config.component === DynamicForm && config.childConfigs) {
                  return (
                    <DynamicForm
                      key={config.id}
                      {...commonProps}
                      childConfigs={config.childConfigs}
                    />
                  );
                }

                if (
                  config &&
                  config.component === DynamicForm &&
                  config.childConfigs
                ) {
                  return (
                    <DynamicForm
                      // ChildFormComponent={undefined}
                      key={config.id}
                      {...commonProps}
                      childConfigs={config.childConfigs}
                    />
                  );
                }

                return (
                  <DynamicForm
                    key={config.id}
                    {...commonProps}
                    // ChildFormComponent={config.ChildFormComponent}
                  />
                );
              })}
            </Stack>
          </Stack>
        </form>
      </FormProvider>
    );
  }
);
