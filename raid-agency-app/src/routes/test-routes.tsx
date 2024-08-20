import ProtectedRoute from "@/components/ProtectedRoute";
import TitlesFormWrapperPage from "@/entities/title/form-components/TitlesFormWrapperPage";
import DescriptionsFormWrapperPage from "@/entities/description/form-components/DescriptionsFormWrapperPage";
import { RouteObject } from "react-router-dom";

export const testRoutes: RouteObject[] = [
  {
    path: "/form-tester/titles",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <TitlesFormWrapperPage />,
      },
    ],
  },
  {
    path: "/form-tester/descriptions",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <DescriptionsFormWrapperPage />,
      },
    ],
  },
];
