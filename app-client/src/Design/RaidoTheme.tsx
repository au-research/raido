import {createTheme, Theme, ThemeProvider} from "@mui/material";
import React from "react";
import {ReactNode} from "react";

// https://www.color-name.com/
// ordered here by color value
export const Color = {
  // black shades
  doNotUseBlack: "#000000",
  darkCharcoal: "#333333",
  jet: "#343434",
  onyx: "#343A40",

  // white shades
  lotion: "#FAFAFA",
  doNotUseWhite: "#FFFFFF"
}

/** use lightColor if light mode, otherwise use darkColor */
function lightDarkColor(
  theme: Theme,
  lightColor: string | undefined = undefined,
  darkColor: string | undefined = undefined
): string | undefined{
  return theme.palette.mode === 'light' ? lightColor : darkColor
}

/* Not sure about the design of this theme stuff yet; I'm thinking maybe it 
would be better to run two separate lightMode/darkMode theme objects?
Then just switch between them.
Also, maybe want to think about moving the "width" constants from Screen.tsx
to the DefaultTheme (declared above) - i.e. "custom theme variables"?
*/
const mode = createTheme({
  palette: {
    mode: 'light',
    background: {
      default: Color.lotion
    },
  },
});

const theme = createTheme(mode, {
  components: {
    MuiAppBar: {
      styleOverrides: {
        colorPrimary: {
          backgroundColor: lightDarkColor(mode, Color.onyx),
        }
      }
    }
  }
});

export function RaidoTheme({children}: { children: ReactNode }){
  return <ThemeProvider theme={theme}>
    {children}
  </ThemeProvider>
}