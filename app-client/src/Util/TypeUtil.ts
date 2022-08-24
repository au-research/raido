
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

