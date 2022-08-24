import * as React from "react";
import {
  Paper,
  useMediaQuery
} from "@mui/material";
import {DialogTitleProps} from "@mui/material/DialogTitle";
import {styled, Theme} from '@mui/system';


export const largeContainerWidth = 1024;
export const smallContainerWidth = 600;

/** Standard main section expected to contain normal "singular" content.
 * i.e. a single form, single table, etc. 
 * The main mechanism of it is to use breakpoints to limit the max width of 
 * the container on large screens because "normal" content tends to be look 
 * awkward if you stretch it out really far across massive ultra-wide screens.
 * */
export function LargeContentMain(props: {
  children: React.ReactNode,
}){
  return <LargeScreenStyledMain>
    {props.children}
  </LargeScreenStyledMain>
}

/** Used on screens that have very little content so they look awkward when
 * hosted in the large content container.
 * Works the same as the large content container, just has a smaller max width.
 */
export function SmallContentMain(props: {
  children: React.ReactNode,
  center?: boolean,
}){
  return <SmallScreenStyledMain>
    <SmallScreenStyledPaper style={props.center ? {textAlign: "center"} : {}}>
      {props.children}
    </SmallScreenStyledPaper>
  </SmallScreenStyledMain>
}

/** Used when screen is split up into multiple, granular sections that can 
 * easily be laid out left to right, making use of a wide-screen format.
 * Could probably allow the flex container to larger if desired.
 * The main mechanism of it is a wrapping flex container.
 */
export function FlexContentMain(props: {
  children:React.ReactNode,
}) {
  return <main style={{
    display: 'flex',
    justifyContent: 'center',
    marginTop: "1em",
  }}>
    <div style={{
      display: 'flex',
      flexWrap: 'wrap',
      justifyContent: 'flex-start',
      maxWidth: largeContainerWidth,
    }}>
      {props.children}
    </div>
  </main>
}

function mainLayoutBreakpoints(theme: Theme, width: number){
  return {
    [theme.breakpoints.up(width)]: {
      width: width,
      marginLeft: 'auto',
      marginRight: 'auto',
    }
  }
}

const LargeScreenStyledMain = styled('main')(({theme}) => ({
  width: 'auto',
  marginTop: ".5em",
  ...mainLayoutBreakpoints(theme, largeContainerWidth)
}));

function paperBreakpoints(theme: Theme, width: number){
  return {
    [theme.breakpoints.up(width)]: {
      marginTop: theme.spacing(6),
      marginBottom: theme.spacing(6),
      padding: theme.spacing(3),
    }
  }
}

const SmallScreenStyledMain = styled('main')(({theme}) => ({
  width: 'auto',
  marginTop: theme.spacing(1),
  marginLeft: theme.spacing(2),
  marginRight: theme.spacing(2),
  ...mainLayoutBreakpoints(theme, smallContainerWidth),
}));

const SmallScreenStyledPaper = styled(Paper)(({theme}) => ({
  marginTop: theme.spacing(3),
  marginBottom: theme.spacing(3),
  padding: theme.spacing(2),
  ...paperBreakpoints(theme, smallContainerWidth),
}));

export function useFullScreenDialog(): boolean{
  // 960 is the "md" breakpoint, the old withMobileDialog defaulted to "sm",
  // which meant the dialog was fullscreen until view size hit the "next"
  // breakpoint after "sm" (i.e. "md")
  return useMediaQuery('(max-width:960px)', {noSsr: true});
}

/** Using these props allows you to supply the content of the dialog title
 * as a two elements, the flex spacing will put the first element (the title)
 * on the left, and the second element (close button) on the right.
 * Remember if you want to override any of these, the override must come *after*
 * the spread of these properties.
 */
export const dialogTitleWithCloseButtonProps = {
  disableTypography: true,
  style: {
    padding: ".5em",
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
} as DialogTitleProps;

