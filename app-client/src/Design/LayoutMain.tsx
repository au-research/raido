import * as React from "react";
import { CSSProperties } from "react";
import { Paper, useMediaQuery } from "@mui/material";
import { DialogTitleProps } from "@mui/material/DialogTitle";
import { styled } from '@mui/system';


export const largeContainerWidth = 1024;
export const smallContainerWidth = 600;

/** Standard main section expected to contain normal "singular" content.
 * i.e. a single form, single table, etc. 
 * The main mechanism of it is to use breakpoints to limit the max width of 
 * the container on large screens because "normal" content tends to be look 
 * awkward if you stretch it out really far across massive ultra-wide screens.
 * */
export function LargeContentMain({children, style}: {
  children: React.ReactNode,
  style?: CSSProperties,
} ){
  return <LargeScreenStyledMain style={style}>
    {children}
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
  return <SmallScreenMain>
    <SmallScreenPaper style={props.center ? {textAlign: "center"} : {}}>
      {props.children}
    </SmallScreenPaper>
  </SmallScreenMain>
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

const LargeScreenStyledMain = styled('main')(({theme}) => ({
  width: 'auto',
  marginTop: ".5em",
  /* Styles for really large screens.
  Don't want the content to just keep spreading out horizontally, it looks
  really ugly and is difficult to use on wide/ultra-wide screens when the 
  window is maximized.*/
  [theme.breakpoints.up(largeContainerWidth)]: {
    width: largeContainerWidth,
    marginLeft: 'auto',
    marginRight: 'auto',
    marginTop: theme.spacing(1),
  }
}));

/* Default settins are "compact", not wasting space on margins - 
 designed for mobile.
 BUT; before you decide to fiddle with the margins etc. - consider if your page
 should be using a "LargeContentMain" instead of a "SmallContentMain".
 If you're here because your screen looks bad on mobile or has lots of wasted 
 spaced because of all the margin - then the screen you're working on is 
 probably no longer "small content" and should be promoted to "large content",
 possibly with a screen-specific styling for maxWidth or something. */
export const SmallScreenMain = styled('main')(({theme}) => ({
  // default for mobile
  width: 'auto',
  marginTop: theme.spacing(1),
  marginLeft: theme.spacing(2),
  marginRight: theme.spacing(2),
  
  /*The defaults are designed for mobile, these are the styles that will be applied 
  larger "desktop"/"table" screens. */
  [theme.breakpoints.up(smallContainerWidth)]: {
    width: smallContainerWidth,
    marginLeft: 'auto',
    marginRight: 'auto',
    marginTop: theme.spacing(1),
  }
}));

export const SmallScreenPaper = styled(Paper)(({theme}) => ({
  // default for mobile
  marginTop: theme.spacing(3),
  marginBottom: theme.spacing(3),
  padding: theme.spacing(2),
  
  // styles for small content on deskop/table/huge screns
  [theme.breakpoints.up(smallContainerWidth)]: {
    marginTop: theme.spacing(6),
    marginBottom: theme.spacing(6),
    padding: theme.spacing(3),
  }
  
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

