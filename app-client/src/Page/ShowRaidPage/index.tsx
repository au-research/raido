import {useQuery} from "@tanstack/react-query";
import {useAuthApi} from "Api/AuthApi";
import {FindRaidByNameRequest, RaidDto} from "Generated/Raidv2";
import "react18-json-view/src/style.css";
import ShowRaidPageContent from "./pages/ShowRaidPageContent";
import {useParams} from "react-router-dom";


export default function ShowRaidPage({ version }: { version?: number }) {
  const api = useAuthApi();

    const {prefix, suffix} = useParams() as { prefix: string, suffix: string };
    const handle = `${prefix}/${suffix}`

  const requestParameters: FindRaidByNameRequest = version
    ? {
        prefix: prefix,
        suffix: suffix,
        version: version,
      }
    : {
        prefix: prefix,
        suffix: suffix,
      };

  const getRaid = async (): Promise<RaidDto> => {
    return await api.raid.findRaidByName(requestParameters);
  };

  const readQuery = useQuery<RaidDto>(["raids"], getRaid);

  if (readQuery.isLoading) {
    return <div>Loading...</div>;
  }

  if (readQuery.isError) {
    return <div>Error...</div>;
  }

  const raidData = readQuery.data;

  return (
      <ShowRaidPageContent defaultValues={raidData} handle={handle} />
  );
}