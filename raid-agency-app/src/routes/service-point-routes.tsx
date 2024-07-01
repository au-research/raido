import ProtectedRoute from "@/components/ProtectedRoute";
import ServicePoint from "@/pages/ServicePoint/ServicePoint";
import ServicePoints from "@/pages/ServicePoints/ServicePoints";
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
