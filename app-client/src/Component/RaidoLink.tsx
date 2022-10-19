import React, {ReactNode} from "react";
import { useLocationPathname } from "Util/Hook/LocationPathname";
import { Link } from "@mui/material";
import { useNavigation } from "Design/NavigationProvider";

export function RaidoLink({href, children}: {
  href: string,
  children?: ReactNode
}){
  //const loc = useLocationPathname();
  const nav = useNavigation();
  return <Link href={href}
    onClick={event=>{
      //event.preventDefault();
      //loc.pushState(href);
      nav.navigateTo(href, event);
    }}
  >{children}</Link>
}