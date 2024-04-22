import { RaidApi } from "@/generated/raid";
import { ReactNode, createContext, useMemo } from "react";

const RaidApiContext = createContext<RaidApi | null>(null);

export function RaidApiProvider({ children }: { children: ReactNode }) {
  const raidApi = useMemo(() => new RaidApi(), []);

  return (
    <RaidApiContext.Provider value={raidApi}>
      {children}
    </RaidApiContext.Provider>
  );
}
