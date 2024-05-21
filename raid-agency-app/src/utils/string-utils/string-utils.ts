export function removeNumberInBrackets(str: string) {
  return str.replace(/\[\d+\](?![^[]*\])/, "");
}
