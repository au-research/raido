import ProtectedRoute from "@/components/ProtectedRoute";
import EditRaidPage from "@/pages/EditRaidPage/EditRaidPage";
import MintRaidPage from "@/pages/MintRaidPage/MintRaidPage";
import RaidHistoryPage from "@/pages/RaidHistoryPage";
import RaidInvitePage from "@/pages/RaidInvitePage";
import RaidsPage from "@/pages/RaidsPage";
import ShowRaidPage from "@/pages/ShowRaidPage";
import { RouteObject } from "react-router-dom";

export const raidPageRoutes: RouteObject[] = [
  {
    path: "/raids",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <RaidsPage />,
      },
    ],
  },
  {
    path: "/raids/new",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <MintRaidPage />,
      },
    ],
  },
  {
    path: "/raids/:prefix/:suffix",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <ShowRaidPage />,
      },
    ],
  },
  {
    path: "/raids/:prefix/:suffix/edit",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <EditRaidPage />,
      },
    ],
  },
  {
    path: "/raids/:prefix/:suffix/history",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <RaidHistoryPage />,
      },
    ],
  },
  {
    path: "/raids/:prefix/:suffix/invite",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <RaidInvitePage />,
      },
    ],
  },
];
