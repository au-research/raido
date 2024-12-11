export function removeNumberInBrackets(str: string) {
  return str.replace(/\[\d+\](?![^[]*\])/, "");
}

export function isValidNumber(value: unknown) {
  return typeof value === "number" && value !== null && !isNaN(value);
}
