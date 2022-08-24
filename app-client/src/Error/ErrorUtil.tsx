import * as React from "react";

export interface ErrorInfo {
  message: string;
  problem: any;
}

export function isError<T>(e: T | Error): e is Error{
  if( e === undefined ){
    return false;
  }

  const error = e as Error;
  if( !error.name){
    return false;
  }
  if( !error.message ){
    return false;
  }
  return true;
}

export function isErrorInfo<T>(e: T | ErrorInfo): e is ErrorInfo{
  if( e === undefined || e == null ){
    return false;
  }
  return (e as ErrorInfo).message !== undefined &&
    (e as ErrorInfo).problem !== undefined;
}

export function forceError(e: unknown): Error{
  if( !e ){
    return new Error("[null or undefined]");
  }
  if( e instanceof Error ){
    return e;
  }
  if( typeof e === 'string' ){
    return new Error(e);
  }
  if( typeof e === 'object' ){
    return new Error(e.toString());
  }
  return new Error("unknown error");
}
