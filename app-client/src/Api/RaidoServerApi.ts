import { fetchGet, fetchPost } from "Util/Http";

// defined by the cloudfront definition in CloudfrontStack
export const apiPrefix = "api-prd-v2";

export function apiMapPost<TReqest, TResult>(
  name: string,
  req: TReqest,
  accessToken: string
): Promise<TResult>{
  // no checking, we just assume it's the shape we want 
  return fetchPost(
    `/${apiPrefix}/${name}`,
    req,
    accessToken
  ) as Promise<TResult>;
}

export function apiMapGet<TResult>(
  name: string,
  accessToken?: string
): Promise<TResult>{
  // no checking, we just assume it's the shape we want 
  return fetchGet(
    `/${apiPrefix}/${name}`,
    accessToken
  ) as Promise<TResult>;
}
