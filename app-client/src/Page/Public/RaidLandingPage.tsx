import React from "react";
import { isPagePath } from "Design/NavigationProvider";
import { TextSpan } from "Component/TextSpan";
import { normalisePath } from "Util/Location";

const pageUrl = "/raid";

/*
not used by anything in the app, the "public landing page" link from the raid 
edit page links via the https://hdl.handle.net domain, which redirects to this
url, but that's encoding the raid url (content path) on the server. 
*/
export function getRaidLandingPagePath(handle: string): string{
  return `${pageUrl}/${handle}`;
}

export function isRaidLandingPagePath(pathname: string): boolean{
  return normalisePath(pathname).startsWith(pageUrl);
}

export function RaidLandingPage(){
  const isPath = isPagePath(window.location.pathname, pageUrl);  

  if( !isPath.isPath ){
    return null;
  }

  if( !isPath.pathSuffix ){
    throw new Error("could not parse handle from path");
  }
  
  return <>
    <Content handle={isPath.pathSuffix}/>
  </>;
}

function Content( {handle}:{handle:string}){
  return <TextSpan>
    RAiD - {handle}
  </TextSpan>
}