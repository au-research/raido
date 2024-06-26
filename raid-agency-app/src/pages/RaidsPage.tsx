import {
  HistoryEdu as HistoryEduIcon,
  Home as HomeIcon,
} from "@mui/icons-material";

import BreadcrumbsBar from "@/components/BreadcrumbsBar";
import type { Breadcrumb } from "@/types";
import { Container, Stack } from "@mui/material";
import RaidTablePage from "./RaidTablePage";

export default function RaidsPage() {
  const breadcrumbs: Breadcrumb[] = [
    {
      label: "Home",
      to: "/",
      icon: <HomeIcon />,
    },
    {
      label: "RAiDs",
      to: "/raids",
      icon: <HistoryEduIcon />,
    },
  ];

  return (
    <Container maxWidth="lg">
      <Stack gap={2}>
        <BreadcrumbsBar breadcrumbs={breadcrumbs} />
        <RaidTablePage />
      </Stack>
    </Container>
  );
}
