import { SVGProps } from "react";

export function RaidoLogoSvg(props: SVGProps<SVGSVGElement>) {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width={24} height={60} {...props}>
      <path
        d="M2 60V0"
        style={{
          fill: "none",
          fillOpacity: 0.75,
          fillRule: "evenodd",
          stroke: "#000",
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
          stroke: "#000",
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
