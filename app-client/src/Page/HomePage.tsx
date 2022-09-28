import {
  isPagePath,
  NavPathResult,
  NavTransition
} from "Design/NavigationProvider";
import React from "react";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import { SmallContentMain } from "Design/LayoutMain";
import { raidoTitle } from "Component/Util";

const log = console;

const pageUrl = "/home";

export function getHomePageLink(): string{
  return pageUrl;
}

export function isHomePagePath(pathname: string): NavPathResult{
  const pathResult = isPagePath(pathname, pageUrl);
  if( pathResult.isPath ){
    return pathResult;
  }

  // use this page as the "default" or "home" page for the app  
  if( pathname === "" || pathname === "/" ){
    return {isPath: true, pathSuffix: ""};
  }

  return { isPath: false }
}

export function HomePage(){
  return <NavTransition isPagePath={isHomePagePath} 
    title={raidoTitle("Home")}>
    <Content/>
  </NavTransition>
}


function Content(){
  return <SmallContentMain>
    <ContainerCard title={"Home"}>
      <TextSpan>This will be the home page of the App.</TextSpan>
    </ContainerCard>
  </SmallContentMain>
}

