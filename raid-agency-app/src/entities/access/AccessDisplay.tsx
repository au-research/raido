import DisplayCard from "@/components/DisplayCard";
import DisplayItem from "@/components/DisplayItem";
import { useMapping } from "@/contexts/mapping/useMapping";
import { Access } from "@/generated/raid";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import { Card, CardContent, CardHeader, Grid } from "@mui/material";
import dayjs from "dayjs";
import { memo, useMemo } from "react";

const AccessDisplay = memo(({ data }: { data: Access }) => {
  const { generalMap, languageMap } = useMapping();

  const hasEmbargoedAccess: boolean = data.type.id.includes("c_f1cf");

  const accessTypeMappedValue = useMemo(
    () => generalMap.get(String(data.type?.id)) ?? "",
    [data.type?.id]
  );

  const languageMappedValue = useMemo(
    () => languageMap.get(String(data?.statement?.language?.id)) ?? "",
    [data?.statement?.language?.id]
  );

  return (
    <DisplayCard
      data={data}
      labelPlural="Access"
      children={
        <Grid container spacing={2}>
          <DisplayItem
            label="Statement"
            value={data?.statement?.text}
            width={hasEmbargoedAccess ? 6 : 8}
          />
          <DisplayItem label="Language" value={languageMappedValue} width={2} />
          <DisplayItem
            label="Access Type"
            value={accessTypeMappedValue}
            width={2}
          />
          {hasEmbargoedAccess && (
            <DisplayItem
              label="Embargo Expiry"
              value={dateDisplayFormatter(
                dayjs(data?.embargoExpiry).format("YYYY-MM-DD")
              )}
              width={2}
            />
          )}
        </Grid>
      }
    />
  );
});

AccessDisplay.displayName = "AccessDisplay";
export default AccessDisplay;
