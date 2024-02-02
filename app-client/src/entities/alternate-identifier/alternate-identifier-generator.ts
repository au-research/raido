import { faker } from "@faker-js/faker";

export const alternateIdentifierGenerator = () => {
  return {
    id: faker.lorem.words(3),
    type: faker.lorem.words(3),
  };
};
