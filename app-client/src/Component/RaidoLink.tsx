import React, { ReactNode } from "react";
import { Link } from "@mui/material";
import { useNavigation } from "Design/NavigationProvider";
import {useNavigate} from "react-router";

export function RaidoLink({href, children}: {
  href: string,
  children?: ReactNode
}){
  const navigate = useNavigate();
  return <Link href={href}
    onClick={event => navigate(href)}
  >{children}</Link>
}