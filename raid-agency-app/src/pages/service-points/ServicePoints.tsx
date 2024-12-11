import { BreadcrumbsBar } from "@/components/breadcrumbs-bar";
import { useAuthHelper } from "@/keycloak";
import { Breadcrumb } from "@/types";
import { Home as HomeIcon, Hub as HubIcon } from "@mui/icons-material";
import { Container, Stack } from "@mui/material";
import ServicePointsGroupAdminView from "./ServicePointsGroupAdminView";
import ServicePointsOperatorView from "./ServicePointsOperatorView";

export default function ServicePoints() {
  const { isOperator, isGroupAdmin } = useAuthHelper();

  const breadcrumbs: Breadcrumb[] = [
    {
      label: "Home",
      to: "/",
      icon: <HomeIcon />,
    },
    {
      label: "Service points",
      to: "/service-points",
      icon: <HubIcon />,
    },
  ];

  return (
    <Container>
      <Stack direction="column" gap={2}>
        <BreadcrumbsBar breadcrumbs={breadcrumbs} />
        {isOperator && <ServicePointsOperatorView />}
        {isGroupAdmin && !isOperator && <ServicePointsGroupAdminView />}
      </Stack>
    </Container>
  );
}
