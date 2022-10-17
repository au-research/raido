# Opinions

> Doesn't really matter. This is one way to do it - whatever floats your boat.


## use function declarations instead of const

i.e `export function doSomething()` instead of 
`export const doSomething = () =>{}` 


### use inline property definitions for React components

```typescript
export function Thing({a}: {s: string}){
  console.log(a);
}
```

As opposed to a separate definition like:

```typescript

export interface ThingProps{
  a: string
}

export function Thing(props: ThingProps){
  const {a} = props;
  console.log(a);
}
```

But you can still use the explicitly named type if you want, you might need to
if you're trying to share types or do something else tricky.
