
export function isNonEmptyArrayOfString(value: any): value is string[] {
  return Array.isArray(value) &&
    !!value.length &&
    value.every(item => typeof item === "string");
}

export function isNonEmptyArrayOfNumber(value: any): value is number[] {
  return Array.isArray(value) &&
    !!value.length &&
    value.every(item => typeof item === "number");
}

export function assertIsNumberArray(input: number[] | any)
: asserts input is number[] {
  if( !isNonEmptyArrayOfNumber(input) ){
    throw new Error(`expected a number[], got a ${typeof input}: ${input}`);
  }
}

export function assert(input: unknown, message?: string)
: asserts input {
  if (!input) {
    throw new Error(message ?? 'value must be defined');
  }  
}

/*
 https://stackoverflow.com/a/69328045/924597
 Is there a builtin for this?
*/
export type WithRequired<T, K extends keyof T> = T & { 
  [P in K]-?: T[P] 
}

export type WithStringKey<T> = {
  [K in keyof T]: T[K] extends string ? K : never;
}[keyof T];

