import ProtectedRoute from "@/components/ProtectedRoute";
import EditRaidPage from "@/pages/EditRaidPage";
import MintRaidPage from "@/pages/MintRaidPage";
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
];
