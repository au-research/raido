import { defineConfig } from "astro/config";

import tailwind from "@astrojs/tailwind";

// https://astro.build/config
export default defineConfig({
  integrations: [tailwind()],
  routes: [
    {
      pattern: "/raids/:prefix/:suffix",
      component: "src/raids/[prefix]/[suffix].astro",
    },
  ],
});
