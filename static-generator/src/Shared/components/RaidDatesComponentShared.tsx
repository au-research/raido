import {ModelDate} from "../../../../app-client/src/Generated/Raidv2";

export default function RaidDatesComponentShared({
  dates,
}: {
  dates: ModelDate;
}) {
  return (
    <div className="rounded border bg-card text-card-foreground shadow-sm bg-white border-l-sky-600 border-l-4">
      <div className="flex flex-col space-y-1.5 p-6">
        <h3 className="text-2xl font-semibold leading-none tracking-tight">
          Dates
        </h3>
        <p className="text-sm text-muted-foreground">RAiD Dates</p>
      </div>
      <div className="p-6 pt-0">
        <div className="grid grid-cols-4">
          <div className="space-y-1 text-sm">
            <h3 className="font-medium leading-none">{dates?.startDate}</h3>
            <p className="text-xs text-muted-foreground">Start Date</p>
          </div>
          <div className="space-y-1 text-sm">
            <h3 className="font-medium leading-none">
              {dates?.endDate || "-"}
            </h3>
            <p className="text-xs text-muted-foreground">End Date</p>
          </div>
        </div>
      </div>
    </div>
  );
}
