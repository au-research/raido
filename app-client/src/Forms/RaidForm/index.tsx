import {zodResolver} from "@hookform/resolvers/zod";
import {ValidationFormSchema} from "validation-schemata";
import AnchorButtons from "Component/AnchorButtons";

import {Close as CloseIcon, Save as SaveIcon} from "@mui/icons-material";
import {Box, Card, CardHeader, Fab, Stack, Tooltip} from "@mui/material";
import {RaidCreateRequest, RaidDto} from "Generated/Raidv2";
import {FormProvider, useForm} from "react-hook-form";

import FormAlternateIdentifiersComponent from "./components/FormAlternateIdentifiersComponent";
import FormAlternateUrlsComponent from "./components/FormAlternateUrlsComponent";

import FormDatesComponent from "./components/FormDatesComponent";

import FormRelatedRaidsComponent from "./components/FormRelatedRaidsComponent";
import FormSpatialCoveragesComponent from "./components/FormSpatialCoveragesComponent";

// import FormTraditionalKnowledgeIdentifiersComponent from "./components/FormTraditionalKnowledgeIdentifiersComponent";
// make sure this is the last import
import FormSubjectsComponent from "./components/FormSubjectsComponent";
import FormTitlesComponent from "./components/FormTitlesComponent";
import FormAccessComponent from "./components/FormAccessComponent";
import FormContributorsComponent from "./components/FormContributorsComponent";
import FormDescriptionsComponent from "./components/FormDescriptionsComponent";
import FormOrganisationsComponent from "./components/FormOrganisationsComponent";
import FormRelatedObjectsComponent from "./components/FormRelatedObjectsComponent";
import {useEffect, useState} from "react";

type FormProps = {
    raidData: RaidCreateRequest;
    onSubmit(data: RaidDto): void;
    isSubmitting: boolean;
    onDirty: (isDirty: boolean) => void;
    prefix?: string;
    suffix?: string;
};

export default function RaidForm({raidData, onSubmit, onDirty, isSubmitting, prefix, suffix}: FormProps) {
    const [isInitialLoad, setIsInitialLoad] = useState(true);


    const formMethods = useForm<RaidDto>({
        defaultValues: raidData,
        resolver: zodResolver(ValidationFormSchema),
        mode: "onChange",
        reValidateMode: "onChange",
    });

    useEffect(() => {
        // Skip the first render (initial load)
        if (isInitialLoad) {
            setIsInitialLoad(false);
            return;
        }

        if(formMethods.formState.isDirty) {
            console.log("formMethods.formState.dirtyFields", JSON.stringify(formMethods.formState.dirtyFields))
        }


        // Call onDirty function when isDirty changes after the initial load
        onDirty(formMethods.formState.isDirty);
    }, [formMethods.formState.isDirty,formMethods.formState.dirtyFields , onDirty, isInitialLoad]);


    const {control, trigger} = formMethods
    const {errors} = formMethods.formState
    return (
        <FormProvider {...formMethods}>
            <form onSubmit={formMethods.handleSubmit(onSubmit)} autoComplete="off" noValidate>
                <Tooltip title="Cancel" placement="left">
                    <Fab
                        color="primary"
                        size="small"
                        sx={{position: "fixed", bottom: "72px", right: "16px"}}
                        component="button"
                        href={
                            raidData?.identifier?.id
                                ? `/show-raid/${prefix}/${suffix}`
                                : "/"
                        }
                    >
                        <CloseIcon/>
                    </Fab>
                </Tooltip>
                <Tooltip title="Save changes" placement="left">
                    <Fab
                        variant="extended"
                        color="primary"
                        sx={{position: "fixed", bottom: "16px", right: "16px"}}
                        component="button"
                        type="submit"
                        disabled={isSubmitting || Object.keys(formMethods.formState.errors).length > 0}
                    >
                        <SaveIcon sx={{mr: 1}}/>
                        {isSubmitting ? "Saving..." : "Save"}
                    </Fab>
                </Tooltip>

                <Stack spacing={2}>
                    <Card className="raid-card">
                        <CardHeader title={`${
                            raidData?.identifier?.id
                                ? `Edit RAiD ${prefix}/${suffix}`
                                : "Mint new RAiD"
                        }`}/>
                    </Card>
                    <AnchorButtons errors={formMethods.formState.errors}/>
                    <Stack sx={{paddingLeft: 2}} spacing={2}>
                        <Box id="dates" className="scroll">
                            <FormDatesComponent
                                control={control}
                                errors={errors}
                            />
                        </Box>

                        <Box id="titles" className="scroll">
                            <FormTitlesComponent
                                control={control}
                                errors={errors}
                                trigger={trigger}
                            />
                        </Box>

                        <Box id="descriptions" className="scroll">
                            <FormDescriptionsComponent
                                control={control}
                                errors={errors}
                                trigger={trigger}
                            />
                        </Box>

                        <Box id="contributors" className="scroll">
                            <FormContributorsComponent
                                control={control}
                                errors={errors}
                                trigger={trigger}
                            />
                        </Box>

                        <Box id="organisations" className="scroll">
                            <FormOrganisationsComponent
                                control={control}
                                errors={errors}
                            />
                        </Box>

                        <Box id="related-objects" className="scroll">
                            <FormRelatedObjectsComponent
                                control={control}
                                errors={errors}
                            />
                        </Box>

                        <Box id="alternate-identifiers" className="scroll">
                            <FormAlternateIdentifiersComponent
                                control={control}
                                errors={errors}
                            />
                        </Box>

                        <Box id="alternate-urls" className="scroll">
                            <FormAlternateUrlsComponent
                                control={control}
                                errors={errors}
                            />
                        </Box>

                        <Box id="related-raids" className="scroll">
                            <FormRelatedRaidsComponent
                                control={control}
                                errors={errors}
                            />
                        </Box>

                        <Box id="access" className="scroll">
                            <FormAccessComponent
                                control={control}
                                errors={errors}
                            />
                        </Box>

                        <Box id="subjects" className="scroll">
                            <FormSubjectsComponent
                                control={control}
                                errors={errors}
                            />
                        </Box>

                        <Box id="spatial-coverage" className="scroll">
                            <FormSpatialCoveragesComponent
                                control={control}
                            />
                        </Box>

                        <Box sx={{}}></Box>
                    </Stack>
                </Stack>
            </form>
        </FormProvider>
    );
}
