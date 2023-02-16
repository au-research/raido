import { SvgIcon, SvgIconProps } from "@mui/material";
import React, { SVGProps } from "react";
import { Color } from "Design/RaidoTheme";

// https://uxwing.com/google-round-icon/
export function Google(props: SvgIconProps){
  return <SvgIcon width="64" height="64" viewBox="0 0 640 640"
    shapeRendering="geometricPrecision" textRendering="geometricPrecision"
    imageRendering="optimizeQuality" fillRule="evenodd" clipRule="evenodd"
    {...props}
  >
    <path d="M320 0C143.234 0 0 143.234 0 320s143.234 320 320 320 320-143.234
      320-320S496.766 0 320 0zm4.76 560.003C192.12 560.003 84.757 452.651 84.757
      320c0-132.651 107.364-240.003 240.003-240.003 64.772 0 118.998 23.646
      160.774 62.753l-65.115 62.764c-17.894-17.114-49.005-36.992-95.647-36.992C242.78
      168.522 176.01 236.4 176.01 320c0 83.6 66.887 151.478 148.762 151.478
      95.01 0 130.643-68.233 136.124-103.513l-136.136-.012v-82.241l226.633.012c1.996
      12.012 3.768 24.012 3.768 39.768.118 137.116-91.761 234.523-230.353
      234.523l-.047-.012z"/>
  </SvgIcon>
}

export function RaidoLogoSvg(props: SVGProps<SVGSVGElement>) {
  return (
    // width={24} height={60}
    <svg xmlns="http://www.w3.org/2000/svg" width={24} height={60} {...props}>
      <path
        d="M2 60V0"
        style={{
          fill: "none",
          fillOpacity: 0.75,
          fillRule: "evenodd",
          //stroke: "#000",
          stroke: props.color,
          strokeWidth: 4,
          strokeLinecap: "butt",
          strokeLinejoin: "miter",
          strokeMiterlimit: 4,
          strokeDasharray: "none",
          strokeOpacity: 1,
        }}
      />
      <path
        d="M0 2h4l16 14-7.383 17.274L22 59"
        style={{
          fill: "none",
          fillOpacity: 0.75,
          fillRule: "evenodd",
          stroke: props.color,
          strokeWidth: 4,
          strokeLinecap: "butt",
          strokeLinejoin: "bevel",
          strokeMiterlimit: 4,
          strokeDasharray: "none",
          strokeOpacity: 1,
        }}
      />
    </svg>
  );
}

/*
https://orcid.figshare.com/articles/figure/ORCID_iD_icon_graphics/5008697
https://orcid.figshare.com/articles/figure/ORCID_iD_icon_graphics/5008697?file=8439059
ORCIDiD_iconBwVector.svg
 */
export function OrcidSvgIcon({circleColor, letterColor}: {
  circleColor?: string, letterColor?: string
}){
  // currentColor is some kind of SVG keyword for "color"
  circleColor = circleColor ?? "currentColor";
  // can't figure out what "background" equivalent of currentColor is
  letterColor = letterColor ?? Color.lotion;

  /* Pretty sure we want to get rid of the text/css style and do normal fill,
  but trying to keep SVG content as close as possible to the source for now. */
  
  return <SvgIcon viewBox={"0 0 256 256"} x={0} y={0}
    xmlSpace={"preserve"}
  >
    <style type="text/css">{`
      .st0{fill:${circleColor};}
      .st1{fill:${letterColor};}
    `}</style>
    <path className="st0"
      d="M256,128c0,70.7-57.3,128-128,128C57.3,256,0,198.7,0,128C0,57.3,57.3,0,128,0C198.7,0,256,57.3,256,128z"/>
    <g>
      <path className="st1" d="M86.3,186.2H70.9V79.1h15.4v48.4V186.2z"/>
      <path className="st1" d="M108.9,79.1h41.6c39.6,0,57,28.3,57,53.6c0,27.5-21.5,53.6-56.8,53.6h-41.8V79.1z M124.3,172.4h24.5
      c34.9,0,42.9-26.5,42.9-39.7c0-21.5-13.7-39.7-43.7-39.7h-23.7V172.4z"/>
      <path className="st1" d="M88.7,56.8c0,5.5-4.5,10.1-10.1,10.1c-5.6,0-10.1-4.6-10.1-10.1c0-5.6,4.5-10.1,10.1-10.1
      C84.2,46.7,88.7,51.3,88.7,56.8z"/>
    </g>
  </SvgIcon>
}

/* https://www.svgrepo.com/svg/321772/australia */
export function AustraliaSvgIcon(){
  return <SvgIcon viewBox="0 0 512 512">
    <path fill="currentColor" d="M380.37 28.839l-27.24 100.215-64-48 17.405-34.46-83.863 8.079-13.541 42.38-35.512-25.482-67.16 85.62-83.008 48.593 34.81 156.752 38.87 6.518 112-64 74.38 52.082 21.62-28.094 32 72.012L424 415.452l64.549-126.398-6.014-64.703-65.404-79.297-36.762-116.215zm-14.75 411.238l15.099 43.084 20.412-2.107 11.435-35.864-46.947-5.113z"/>    
  </SvgIcon>  
}