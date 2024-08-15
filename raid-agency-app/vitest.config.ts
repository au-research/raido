import { configDefaults, defineConfig } from "vitest/config";
import path from "path";

import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  base: "/.",
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  test: {
    globals: true,
    environment: "jsdom",
    exclude: [...configDefaults.exclude, "**/e2e/**/*.[jt]s?(x)"],
    setupFiles: "./vitest.setup.ts",
  },
});
