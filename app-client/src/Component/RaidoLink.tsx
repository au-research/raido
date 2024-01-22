import React, {ReactNode} from "react";
import {Link} from "@mui/material";
import {useNavigate} from "react-router";

export function RaidoLink({href, children}: {
  href: string,
  children?: ReactNode
}){
  const navigate = useNavigate();
  return <Link href={href}
    onClick={()=> navigate(href)}
  >{children}</Link>
}