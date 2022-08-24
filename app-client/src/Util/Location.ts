
export function recordToUrlSearchParams(
  search: Record<string, string|undefined>
): URLSearchParams{
  const params = new URLSearchParams();
  for( let iKey in search){
    params.append(iKey, search[iKey] || "");
  }
  return params;
}

/**
 * @param search with or without `?` prefix
 */
export function searchStringToRecord(search: string): 
Record<string, string | undefined>{
  return Object.fromEntries(new URLSearchParams(search));
}

/** @return with `?` prefix */
export function recordToSearchString(
  record: Record<string, string|undefined>
): string{
  return "?" + recordToUrlSearchParams(record).toString();
}

export function formatPath(
  pathname: string,
  search: Record<string, string|undefined>
): string{
  return pathname + recordToSearchString(search);
}

// note: "/first" will match "https://localhost/first/second"
export function isPathname(pathname: string, location: string): boolean{
  return location === pathname || location.startsWith(pathname+"/");
}

// IMPROVE: not very robust, is there a "proper" way?
export function parsePathname(value: string){
  value = value.trim();
  if( value.startsWith("http:") || value.startsWith("https:") ){
    return new URL(value).pathname;
  }

  if( value.indexOf("?") !== -1 ){
    return value.slice(0, value.indexOf("?"));
  }
  else if( value.indexOf("#") !== -1 ){
    return value.slice(0, value.indexOf("#"));
  }

  return value;
}

export function parseBool(val: string|null, defaultValue=false):boolean {
  try {
    return !!JSON.parse(String(val).toLowerCase());
  }
  catch( e ){
    return defaultValue;
  }
}

export function parseBooleanDefault<T extends Record<string, string>>(
  record: T,
  key: keyof T,
  defaultValue = false,
): boolean {
  return parseBool(record[key] || String(defaultValue), defaultValue);
}

export function parseBoolean<T extends Record<string, string>>(
  record: T,
  key: keyof T,
): boolean|undefined {
  if( !record[key] ){
    return undefined;
  }
  return parseBool(record[key]);
}

export function normalisePath(path: string): string{
  if( !path ){
    return "";
  }
  const normalizedPath = path.toLowerCase().trim();
  if( normalizedPath.charAt(normalizedPath.length-1) === "/" ){
    return normalizedPath.slice(0, -1);
  }
  else {
    return normalizedPath;
  }
}

