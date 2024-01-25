import {Container} from "@mui/material";
import {useMutation, useQuery} from "@tanstack/react-query";
import {useAuthApi} from "Api/AuthApi";
import RaidForm from "Forms/RaidForm";
import {raidRequest} from "../../utils";
import {useParams} from "react-router-dom";
import React, {useCallback, useMemo} from "react";

export default function EditRaidPage() {
    const api = useAuthApi();
    const { prefix, suffix } = useParams();
    if(!prefix || !suffix) {
        throw new Error("prefix and suffix are required");
    }
    const raidQueryKey = useMemo(() => ["raids", prefix, suffix], [prefix, suffix]);

    const getRaid = useCallback(async () => {
        return await api.raid.findRaidByName({ prefix, suffix });
    }, [api, prefix, suffix]);

    const { data, isLoading, isError } = useQuery(raidQueryKey, getRaid);

    const handleRaidUpdate = useCallback(async (data:any) => {
        return await api.raid.updateRaid({
            prefix,
            suffix,
            raidUpdateRequest: raidRequest(data),
        });
    }, [api, prefix, suffix]);

    const updateRequest = useMutation(handleRaidUpdate, {
        onSuccess: (updateResult) => {
            console.log("updateResult", updateResult);
            // use navigate from react-router-dom here
        },
        onError: (error) => {
            console.log("error", error);
            // possibly more detailed error handling
        },
    });

    if (isLoading) return <div>Loading...</div>;
    if (isError) return <div>Error...</div>;

    return (
        <Container maxWidth="lg" sx={{ py: 2 }}>
            <RaidForm
                prefix={prefix}
                suffix={suffix}
                raidData={data}
                onSubmit={updateRequest.mutate}
                isSubmitting={updateRequest.isLoading}
            />
        </Container>
    );
}
