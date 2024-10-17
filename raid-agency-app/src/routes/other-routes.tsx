import ProtectedRoute from "@/components/ProtectedRoute";
import AppNavBarUnauthenticated from "@/design/AppNavBarUnauthenticated";
import ApiKeyPage from "@/pages/ApiKeyPage";
import HomePage from "@/pages/Homepage";
import LoginPage from "@/pages/LoginPage";
import { AboutRaidPage } from "@/pages/Unauth/AboutRaidPage";
import { PrivacyPage } from "@/pages/Unauth/PrivacyPage";
import { UsageTermsPage } from "@/pages/Unauth/UsageTermsPage";
import UserProfilePage from "@/pages/UserProfilePage";
import { Box } from "@mui/material";
import { RouteObject } from "react-router-dom";

export const otherRoutes: RouteObject[] = [
  {
    path: "/",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <HomePage />,
      },
    ],
  },
  {
    path: "/login",
    element: (
      <>
        <AppNavBarUnauthenticated />
        <Box sx={{ height: "3rem" }} />
        <LoginPage />
      </>
    ),
  },
  {
    path: "/privacy",
    element: (
      <>
        <AppNavBarUnauthenticated />
        <PrivacyPage />
      </>
    ),
  },
  {
    path: "/terms",
    element: (
      <>
        <AppNavBarUnauthenticated />
        <UsageTermsPage />
      </>
    ),
  },
  {
    path: "/about-raid",
    element: (
      <>
        <AppNavBarUnauthenticated />
        <AboutRaidPage />
      </>
    ),
  },
  {
    path: "api-key",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <ApiKeyPage />,
      },
    ],
  },
  {
    path: "profile",
    element: <ProtectedRoute />,
    children: [
      {
        path: "",
        element: <UserProfilePage />,
      },
    ],
  },
  // https://orcid.test.raid.org.au/invite?userId=466a8eab-6325-49aa-ab34-ff7b62213c53
];
