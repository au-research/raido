import {Container} from "@mui/material";
import {useMutation, useQuery} from "@tanstack/react-query";
import {useAuthApi} from "Api/AuthApi";
import RaidForm from "Forms/RaidForm";
import {raidRequest} from "../../utils";
import {useParams} from "react-router-dom";
import React, {useCallback, useMemo} from "react";
import {useNavigate} from "react-router";

export default function EditRaidPage() {
    const navigate = useNavigate()
    // const [isDirty, setIsDirty] = React.useState(false);
    //
    // const handleDirty = (isDirty: boolean) => {
    //     setIsDirty(isDirty);
    // };

    const api = useAuthApi();
    const {prefix, suffix} = useParams();
    if (!prefix || !suffix) {
        throw new Error("prefix and suffix are required");
    }
    const raidQueryKey = useMemo(() => ["raids", prefix, suffix], [prefix, suffix]);

    const getRaid = useCallback(async () => {
        return await api.raid.findRaidByName({prefix, suffix});
    }, [api, prefix, suffix]);

    const {data, isLoading, isError} = useQuery(raidQueryKey, getRaid);

    /* The following enables blocking */

    // const shouldBlock = React.useCallback<BlockerFunction>(
    //     ({currentLocation, nextLocation}) => {
    //         return isDirty && (currentLocation.pathname !== nextLocation.pathname)
    //     },
    //     [data, isDirty]
    // );
    //
    // const blocker = useBlocker(shouldBlock);
    //
    // React.useEffect(() => {
    //     if (isDirty) {
    //             console.info("nav change and dirty form: auto save should happen now...")
    //             if (blocker.state === "blocked") {
    //                 blocker.reset();
    //             }
    //     }
    //
    // }, [blocker, data]);

    const handleRaidUpdate = useCallback(async (data: any) => {
        return await api.raid.updateRaid({
            prefix,
            suffix,
            raidUpdateRequest: raidRequest(data),
        });
    }, [api, prefix, suffix]);

    const updateRequest = useMutation(handleRaidUpdate, {
        onSuccess: () => {
            navigate(`/show-raid/${prefix}/${suffix}`)
        },
        onError: (error) => {
            console.log("error", error);
        },
    });

    if (isLoading) return <div>Loading...</div>;
    if (isError) return <div>Error...</div>;

    return (
        <Container>
            <RaidForm
                prefix={prefix}
                suffix={suffix}
                raidData={data}
                onDirty={handleDirty}
                onSubmit={updateRequest.mutate}
                isSubmitting={updateRequest.isLoading}
            />
        </Container>
    );
}

