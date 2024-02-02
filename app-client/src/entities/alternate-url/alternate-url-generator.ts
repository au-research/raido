import { faker } from "@faker-js/faker";

export const alternateUrlGenerator = () => {
  return {
    url: faker.internet.url(),
  };
};
