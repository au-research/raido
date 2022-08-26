import React, {ReactNode} from "react";
import { useLocationPathname } from "Util/Hook/LocationPathname";

export function RaidoLink({href, children}: {
  href: string,
  children?: ReactNode
}){
  const loc = useLocationPathname();
  return <a href={href}
    onClick={event=>{
      event.preventDefault();
      loc.pushState(href);
    }}
  >{children}</a>
}