import {
  QueryObserverLoadingErrorResult,
  QueryObserverLoadingResult,
  QueryObserverRefetchErrorResult,
  QueryObserverSuccessResult
} from "@tanstack/react-query";

export type RqQuery<TData> =
  QueryObserverRefetchErrorResult<TData> | 
  QueryObserverSuccessResult<TData> | 
  QueryObserverLoadingErrorResult<TData> | 
  QueryObserverLoadingResult<TData>
;
