import { faker } from "@faker-js/faker";
import { AlternateIdentifier } from "Generated/Raidv2";

export const alternateIdentifierGenerator = (): AlternateIdentifier => {
  return {
    id: faker.lorem.words(3),
    type: faker.lorem.words(3),
  };
};
