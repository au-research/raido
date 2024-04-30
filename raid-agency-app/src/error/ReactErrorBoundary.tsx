import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import { Container, Divider, Typography } from "@mui/material";
import * as React from "react";

/** This component deals with unexpected errors (usually programming errors)
 * during component rendering.
 * See https://reactjs.org/docs/error-boundaries.html
 * Has to be a class because React error boundaries only work with class
 * components AFAIK.
 * Needs to be separate from ErrorDialog because that's designed to show
 * errors while still rendering the normal component hierarchy - we can't
 * render the component hierarchy if it's causing errors.
 */
export class ReactErrorBoundary extends React.Component<{
  children: React.ReactNode;
}> {
  state = {} as { hasError: undefined | Error };

  /** The "uncaught exception" logic of sentry does NOT fire when the app is
   * built in production mode.  Which makes sense - it *is* handled, right
   * here.
   * I assume the dev build behaviour is something to do with React development
   * mode showing stack traces or something.
   * So render errors may be logged twice by a development build.
   */
  componentDidCatch(error: Error, info: React.ErrorInfo) {
    console.log("unhandled react render error", error, info);
  }

  render() {
    if (!this.state.hasError) {
      return this.props.children;
    }

    return (
      <Container>
        <ErrorAlertComponent error={"Unhandle rendering error."} />;
        <br />
        <Divider />
        <br />
        <Typography>
          Things to try:
          <ul>
            <li>Click the refresh button in your browser</li>
            <li>
              Edit the URL location to remove any parameters (everything from
              the '#' character to the end)
            </li>
            <li>
              Do a "hard refresh" of your browser (shift-click, ctrl+F5 etc. -
              see links below)
            </li>
            <li>Log out of the app (including SSO logout).</li>
            <li>
              Clear local state like cookies / local storage for the current
              site
            </li>
            <li>
              Clear local state like cookies / local storage for all sites
            </li>
            <li>Update your browser version</li>
            <li>Contact support</li>
          </ul>
          More information:
          <ul>
            <li>
              <a
                target="_blank"
                rel="noopener noreferrer"
                href="https://en.wikipedia.org/wiki/Wikipedia:Bypass_your_cache"
              >
                About doing a "hard refresh"
              </a>
            </li>
            <li>
              <a
                target="_blank"
                rel="noopener noreferrer"
                href="https://refreshyourcache.com/en/safari-mobile/"
              >
                About clearing your cache
              </a>
            </li>
            <li>
              <a
                target="_blank"
                rel="noopener noreferrer"
                href="https://refreshyourcache.com/en/safari-mobile/"
              >
                About updating your browser
              </a>
            </li>
          </ul>
        </Typography>
      </Container>
    );
  }
}
