import {SvgIcon, SvgIconProps} from "@mui/material";
import React, { SVGProps } from "react";

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
