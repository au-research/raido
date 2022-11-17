import React, { ReactNode } from "react";
import { Link } from "@mui/material";
import { useNavigation } from "Design/NavigationProvider";

export function RaidoLink({href, children}: {
  href: string,
  children?: ReactNode
}){
  const nav = useNavigation();
  return <Link href={href}
    onClick={event => {
      nav.navigateTo(href, event);
    }}
  >{children}</Link>
}