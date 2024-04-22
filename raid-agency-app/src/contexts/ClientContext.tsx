import React, { createContext } from "react";

export const ClientContext = createContext<{
  clientId: string;
}>({
  clientId: "",
});

export const useClient = () => React.useContext(ClientContext);
