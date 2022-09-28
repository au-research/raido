import React, {ReactNode} from "react";
import { useLocationPathname } from "Util/Hook/LocationPathname";
import { Link } from "@mui/material";

export function RaidoLink({href, children}: {
  href: string,
  children?: ReactNode
}){
  const loc = useLocationPathname();
  return <Link href={href}
    onClick={event=>{
      event.preventDefault();
      loc.pushState(href);
    }}
  >{children}</Link>
}