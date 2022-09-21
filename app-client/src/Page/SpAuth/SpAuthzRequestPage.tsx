import { NavTransition } from "Design/NavigationProvider";
import { raidoTitle } from "Component/Util";
import { LargeContentMain, SmallContentMain } from "Design/LayoutMain";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import React from "react";
import { normalisePath } from "Util/Location";

const log = console;

const pageUrl = "/authz-request";

export function getSpAuthzRequestPageLink(): string{
  return pageUrl;
}

export function isSpAuthzRequestPagePath(path: string): boolean{
  return normalisePath(path).startsWith(pageUrl);
}

export function SpAuthzRequestPage(){
  return <NavTransition isPath={isSpAuthzRequestPagePath} 
    title={raidoTitle("Authorisation requests")}
  >
    <Content/>
  </NavTransition>
}


function Content(){
  return <LargeContentMain>
    <ContainerCard title={"Authorisation requests"}>
      <TextSpan>List of authorisation requests</TextSpan>
    </ContainerCard>
  </LargeContentMain>
}

