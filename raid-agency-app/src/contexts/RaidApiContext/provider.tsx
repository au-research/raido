import { RaidApi } from "@/generated/raid";
import { RaidApiContext } from "./context";

export const RaidApiProvider: React.FC<React.PropsWithChildren<unknown>> = ({
  children,
}) => {
  const raidApi = new RaidApi();

  return (
    <RaidApiContext.Provider value={{ api: raidApi }}>
      {children}
    </RaidApiContext.Provider>
  );
};
