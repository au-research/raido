import { ProtectedRoute } from "@/components/protected-route";
import { MintRaid } from "@/pages/mint-raid";
import { RaidEdit } from "@/pages/raid-edit";
import { RaidHistory } from "@/pages/raid-history";
import { Raids } from "@/pages/raids";
import { RaidDisplay } from "@/pages/raid-display/RaidDisplay";
import { RouteObject } from "react-router-dom";
import { RaidInvite } from "@/pages/raid-invite";

export const raidPageRoutes: RouteObject[] = [
  {
    path: "/raids",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <Raids />,
      },
    ],
  },
  {
    path: "/raids/new",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <MintRaid />,
      },
    ],
  },
  {
    path: "/raids/:prefix/:suffix",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <RaidDisplay />,
      },
    ],
  },
  {
    path: "/raids/:prefix/:suffix/edit",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <RaidEdit />,
      },
    ],
  },
  {
    path: "/raids/:prefix/:suffix/history",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <RaidHistory />,
      },
    ],
  },
  {
    path: "/raids/:prefix/:suffix/invite",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <RaidInvite />,
      },
    ],
  },
];
