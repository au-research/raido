import * as React from "react";
import {ErrorInfo} from "Error/ErrorUtil";
import {CompactErrorPanel} from "Error/CompactErrorPanel";

type SpanProps = React.DetailedHTMLProps<
  React.HTMLAttributes<HTMLSpanElement>,
  HTMLSpanElement >;

/** adds padding to avoid buttons touching (especially if wrapped on a
 very small device)
 IMPROVE: likely better to refactor to grid layout.
 */
export function  ButtonContainer({
  children,
  error,
  ...spanProps
}: {
  children: React.ReactNode,
  error?: ErrorInfo,
} & SpanProps ){
  const justifyContent = spanProps.style?.justifyContent ?? 'flex-start';
  const buttonPadding = {paddingRight: "1em", paddingBottom: ".35em"};
  let errorComponent = null;
  if( error ){
    errorComponent = <span style={{...buttonPadding, marginBottom: ".35em"}}>
      <CompactErrorPanel error={error}/>
    </span>;
  }

  return <>
    { justifyContent === 'flex-end' && errorComponent }
    <span {...spanProps} style={{
      display: "flex", flexWrap: "wrap",
      ...spanProps.style,
    }}>
      {React.Children.map(
        children, i =>{
          if( i === undefined || i === null ){
            return null;
          }
          return <span style={buttonPadding}>
            <>{i}</>
          </span>
        }
      )}
    </span>
    { (justifyContent !== 'flex-end') && errorComponent }
  </>
}
