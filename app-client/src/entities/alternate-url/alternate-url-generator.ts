import { faker } from "@faker-js/faker";
import { AlternateUrl } from "Generated/Raidv2";

export const alternateUrlGenerator = (): AlternateUrl => {
  return {
    url: faker.internet.url(),
  };
};
