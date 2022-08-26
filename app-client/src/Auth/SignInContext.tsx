import React, { useContext } from "react";

export type SignInState = {
  action: string | undefined,
  setAction: (action: string | undefined) => void,
};

export const SignInContext = React.createContext(
  undefined as unknown as SignInState);

/** If `use()` is called when not underneath the context provider,
 * they will get an error. */
export const useSignInContext = () => {
  let ctx = useContext(SignInContext);
  if( !ctx ){
    throw new Error("No SignInContextProvider present in component hierarchy");
  }
  return ctx;
};
