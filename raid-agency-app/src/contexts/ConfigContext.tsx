import React, { PropsWithChildren, useContext, useState } from "react";

interface IConfig {
  theme: "dark" | "light";
  greeting: string;
}

type ConfigContextType = {
  config: IConfig;
  saveConfig: (config: IConfig) => void;
};

const ConfigContext = React.createContext<ConfigContextType | null>(null);

export const ConfigProvider: React.FC<PropsWithChildren<unknown>> = ({
  children,
}) => {
  const [config, setConfig] = useState<IConfig>({
    theme: "light",
    greeting: "Hello",
  });

  const saveConfig = (newConfig: IConfig) => {
    setConfig(newConfig);
  };

  return (
    <ConfigContext.Provider value={{ config, saveConfig }}>
      {children}
    </ConfigContext.Provider>
  );
};

export const useConfig = () => {
  const context = useContext(ConfigContext);
  if (!context) {
    throw new Error("useConfig must be used within a ConfigProvider");
  }
  return context;
};
