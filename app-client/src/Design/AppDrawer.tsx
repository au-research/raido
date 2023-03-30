import * as React from "react";
import SwipeableDrawer from '@mui/material/SwipeableDrawer';
import List from '@mui/material/List';
import { ListItemButton, ListItemIcon, ListItemText } from "@mui/material";
import HomeIcon from "@mui/icons-material/Home";
import { useNavigation } from "Design/NavigationProvider";
import { useLocationPathname } from "Util/Hook/LocationPathname";
import { getHomePageLink, isHomePagePath } from "Page/HomePage";
import { getAboutAppPagePath, isAboutAppPagePath } from "Page/Public/AboutAppPage";
import { Info } from "@mui/icons-material";
import {
  getAdminAuthzRequestPageLink,
  isAdminAuthzRequestPagePath
} from "Page/Admin/AdminAuthzRequestPage";
import { useAuth } from "Auth/AuthProvider";
import {
  getListServicePointPageLink,
  isListServicePointPagePath
} from "Page/Admin/ListServicePointPage";
import {
  getListAppUserPageLink,
  isListAppUserPagePath
} from "Page/Admin/ListAppUserPage";
import { isOperator, isSpAdmin } from "Auth/Authz";
import {
  getListApiKeyPageLink,
  isListApiKeyPagePath
} from "Page/Admin/ListApiKeyPage";


export function AppDrawer(props: {
  anchor: 'left' |'right',
  open: boolean,
  toggleDrawer: (open:boolean)=>void,
}){
  const auth = useAuth();
  const isOp = isOperator(auth);
  const isAdmin = isSpAdmin(auth);
  const {pathname} = useLocationPathname();

  const sideList = (
    // hardcoded width reminds folks that mobile is a thing
    <div style={{width: 250}}>
      <List>
        <ListNavButton href={getHomePageLink()}
          isCurrent={isHomePagePath(pathname).isPath}
          description={"Home"}
          icon={<HomeIcon/>}
        />
        <ListNavButton href={getAboutAppPagePath()}
          isCurrent={isAboutAppPagePath(pathname)}
          description={"About"}
          icon={<Info/>}
        />
        { isOp && 
          <ListNavButton href={getAdminAuthzRequestPageLink()}
          isCurrent={isAdminAuthzRequestPagePath(pathname)}
          description={"Authorisation requests"}
          />
        }
        { isOp && 
          <ListNavButton href={getListServicePointPageLink()}
          isCurrent={isListServicePointPagePath(pathname)}
          description={"Service points"}
          />
        }
        { (isOp || isAdmin) &&
          <ListNavButton
            href={getListAppUserPageLink(auth.session.payload.servicePointId)}
            isCurrent={isListAppUserPagePath(pathname).isPath}
            description={"Users"}
          />
        }
        { (isOp || isAdmin) &&
          <ListNavButton
            href={getListApiKeyPageLink(auth.session.payload.servicePointId)}
            isCurrent={isListApiKeyPagePath(pathname).isPath}
            description={"API keys"}
          />
        }
      </List>
    </div>
  );

  // const iOS = /iPad|iPhone|iPod/.test(navigator.userAgent);

  const onClose = ()=> props.toggleDrawer(false);
  return <SwipeableDrawer
    // disableBackdropTransition={!iOS}
    // disableDiscovery={iOS}
    open={props.open}
    onClose={onClose}
    onOpen={()=> props.toggleDrawer(true)}
    anchor={props.anchor}
  >
    <div
      tabIndex={0}
      role="button"
      onClick={onClose}
      onKeyDown={onClose}
    >
      {sideList}
    </div>
  </SwipeableDrawer>;
}

function ListNavButton(props: {
  href: string,
  description: string,
  icon?: JSX.Element,
  adminOnly?: boolean,
  isCurrent: boolean,
}){
  // const authz = useAuthz();
  const nav = useNavigation();


  // if( props.adminOnly && !authz.isAdmin() ){
  //   // if needs admin but user isn't admin, show nothing
  //   return null;
  // }

  // const isCurrent = currentScreen === props.screen;
  const {isCurrent} = props;
  const description = <span style={{fontWeight: isCurrent ? "bold":"normal"}}>
    {props.description}
  </span>;

  return <ListItemButton href={props.href}
    onClick={event=>{
      nav.navigateTo(props.href, event)
    }}
  >
    { props.icon &&
      <ListItemIcon>
        {props.icon}
      </ListItemIcon>
    }
    <ListItemText primary={description} />
  </ListItemButton>;
}
