import { dateTimeReviver } from "Util/DateUtil";

export async function fetchGet(
  path: string,
  authToken?: string
): Promise<any>{
  let response: Response = await fetch(
    path,
    {
      method: 'GET',
      headers: constructHeaders(authToken),
    }
  );

  return handleServerResponse(response);
}

// not sure about the Record type, it just got me going
export async function fetchPost(
  path: string,
  body:  Record<string, any>,
  authToken?: string,
): Promise<any>{
  let response: Response = await fetch(
    path,
    {
      method: 'POST',
      headers: constructHeaders(authToken),
      body: jsonFormat(body),
    }
  );

  return handleServerResponse(response);
}

export async function handleServerResponse(response: Response): Promise<object>{
  if( response.status !== 200 ){
    console.log("non-200!");
    const message = await response.text();
    throw new Error("server error: " + message);
  }

  return await jsonParse(response);
}

export function jsonFormat(body: undefined|object): string{
  if( !body ){
    return "{}";
  }

  /* IMPROVE: deal with dates, so that json parse/format are symmetrical
   though now that we're using Zod to define types, we can probably just
   use the built-in date handling */
  return JSON.stringify(body, null, 4);
}

export async function jsonParse(response: Response): Promise<object>{
  const text = await response.text();
  /* crazy dodgy dateTime shennanigans - this kind of silliness
   is why I prefer an IDL */
  return JSON.parse(text, dateTimeReviver);
}

function constructHeaders(authToken?: string): Headers{
  let headers = new Headers();
  headers.append("Accept", 'application/json');
  headers.append("Content-Type", 'application/json');
  headers.append("Accept-Encoding", 'gzip, deflate');
  if( authToken ){
    headers.append("Authorization", 'Bearer ' + authToken);
  }

  return headers
}

