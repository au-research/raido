import { NavTransition } from "Design/NavigationProvider";
import React from "react";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import { SmallContentMain } from "Design/LayoutMain";

const log = console;

const pageUrl = "/home";

export function getHomePageLink(): string{
  return pageUrl;
}

export function isHomePagePath(path: String): boolean{
  const normalizedPath = path.toLowerCase();
  return normalizedPath.startsWith(pageUrl) ||
    // use this page as the "default" or "home" page for the app  
    path === "/";
}

export function HomePage(){
  return <NavTransition isPath={isHomePagePath} title={"Raido - home page"}>
    <Content/>
  </NavTransition>
}


function Content(){
  return <SmallContentMain>
    <ContainerCard title={"Home"}>
      <TextSpan>This will be the home page of Raido.</TextSpan>
    </ContainerCard>
  </SmallContentMain>
}

