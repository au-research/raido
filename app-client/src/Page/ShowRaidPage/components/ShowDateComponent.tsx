import { RaidDto } from "Generated/Raidv2";
import RaidDatesComponentShared from "../../../Shared/components/RaidDatesComponentShared";

export default function ShowDateComponent({ raid }: { raid: RaidDto }) {
  return (
    <div style={{ paddingLeft: "1em" }}>
      <RaidDatesComponentShared dates={raid.date!} />
    </div>
  );
}
