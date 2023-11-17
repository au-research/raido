export const getPrefixFromUrl = (inputUrl: string) => {
  return new URL(inputUrl).pathname.split("/")[1];
};

export const getSuffixFromUrl = (inputUrl: string) => {
  return new URL(inputUrl).pathname.split("/")[2];
};

export const getTypeFromUrl = (url: string): string => {
  const match = /\/([\w-]+)\.json$/i.exec(url);
  if (match && match[1]) {
    return match[1].charAt(0).toUpperCase() + match[1].slice(1).toLowerCase();
  }
  return "Unknown";
};

export const getLastElementFromUrl = (url: string): string => {
  // remove trailing slash
  if (url.endsWith("/")) {
    url = url.slice(0, -1);
  }
  const match = new URL(url).pathname.split("/").pop();
  if (match) {
    return match;
  }

  return "Unknown";
};

export const getHumanReadableKey = (input: string) => {
  return input
    .split(/(?=[A-Z])/)
    .join(" ")
    .toLowerCase()
    .replace(/_/g, " ")
    .replace(/-/g, " ")
    .replace(/^\w/, (c) => c.toUpperCase());
};
