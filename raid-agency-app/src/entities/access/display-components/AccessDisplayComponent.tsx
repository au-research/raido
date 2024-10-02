import { DisplayItem } from "@/components/DisplayItem";
import { Access } from "@/generated/raid";
import mapping from "@/mapping.json";
import language from "@/references/language.json";
import { MappingElement } from "@/types";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import { Card, CardContent, CardHeader, Grid } from "@mui/material";
import dayjs from "dayjs";

export default function AccessDisplayComponent({ access }: { access: Access }) {
  const hasEmbargoedAccess: boolean = access.type.id.includes("c_f1cf");
  const currentLanguage = language.find(
    (language) =>
      language.code.toString() === access?.statement?.language?.id?.toString()
  );
  return (
    <Card>
      <CardHeader title="Access" />
      <CardContent>
        <Grid container spacing={2}>
          <DisplayItem
            label="Access Statement"
            value={access?.statement?.text}
            width={4}
          />
          <DisplayItem
            label="Access Type"
            value={
              mapping.find((el: MappingElement) => el.id === access.type.id)
                ?.value
            }
            width={hasEmbargoedAccess ? 3 : 6}
          />
          {hasEmbargoedAccess && (
            <DisplayItem
              label="Embargo Expiry"
              value={dateDisplayFormatter(
                dayjs(access?.embargoExpiry).format("YYYY-MM-DD")
              )}
              width={3}
            />
          )}
          <DisplayItem
            label="Language"
            value={currentLanguage?.name}
            width={2}
          />
        </Grid>
      </CardContent>
    </Card>
  );
}
