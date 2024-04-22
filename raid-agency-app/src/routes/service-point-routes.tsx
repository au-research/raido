import ProtectedRoute from "@/components/ProtectedRoute";
import ServicePoint from "@/pages/ServicePoint";
import ServicePoints from "@/pages/ServicePoints";
import { RouteObject } from "react-router-dom";

export const servicePointRoutes: RouteObject[] = [
  {
    path: "/service-points",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <ServicePoints />,
      },
    ],
  },
  {
    path: "/service-points/:servicePointId",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <ServicePoint />,
      },
    ],
  },
];
