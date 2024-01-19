import {Container} from "@mui/material";
import {useMutation, useQuery} from "@tanstack/react-query";
import {useAuthApi} from "Api/AuthApi";
import {raidoTitle} from "Component/Util";
import {
    isPagePath,
    NavigationState,
    NavPathResult,
    NavTransition,
    parsePageSuffixParams,
    useNavigation,
} from "Design/NavigationProvider";
import RaidForm from "Forms/RaidForm";
import {RaidDto,} from "Generated/Raidv2";

import {useState} from "react";
import {raidRequest} from "../../utils";
import {useParams} from "react-router-dom";

const pageUrl = "/edit-raid";

export function isEditRaidPagePath(pathname: string): NavPathResult {
    return isPagePath(pathname, pageUrl);
}

function getRaidHandleFromPathname(nav: NavigationState): string {
    return parsePageSuffixParams<string>(nav, isEditRaidPagePath, String);
}

function Content() {
    const api = useAuthApi();

    const {prefix, suffix} = useParams() as { prefix: string, suffix: string };

    const getRaid = async (): Promise<RaidDto> => {
        return await api.raid.findRaidByName({prefix, suffix});
    };

    const useGetRaid = () => {
        return useQuery<RaidDto>(["raids"], getRaid);
    };

    const readQuery = useGetRaid();

    const handleRaidUpdate = async (data: RaidDto): Promise<RaidDto> => {
        return await api.raid.updateRaid({
            prefix,
            suffix,
            raidUpdateRequest: raidRequest(data),
        });
    };

    const updateRequest = useMutation(handleRaidUpdate, {
        onSuccess: (updateResult) => {
            console.log("updateResult", updateResult);
            window.location.href = `/show-raid/${prefix}/${suffix}`;
        },
        onError: (error) => {
            console.log("error", error);
        },
    });

    if (readQuery.isLoading) {
        return <div>Loading...</div>;
    }

    if (readQuery.isError) {
        return <div>Error...</div>;
    }

    return (
        <Container maxWidth="lg" sx={{py: 2}}>
            <RaidForm
                defaultValues={readQuery.data}
                onSubmit={async (data) => {
                    console.log(JSON.stringify(data, null, 2));
                    updateRequest.mutate(data);
                }}
                isSubmitting={updateRequest.isLoading}
            />
        </Container>
    );
}

export default function EditRaidPage() {
    return (
        <Content/>
    );
}
