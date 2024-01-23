import {TextSpan} from "Component/TextSpan";
import * as React from "react";
import {ErrorInfo,} from "Error/ErrorUtil";
import {SecondaryButton} from "Component/AppButton";

const log = console;

export function ErrorInfoComponent(props: {
  error: ErrorInfo;
}){
  // explicitly calling out that it's "any" to remind that you can't just
  // dump the object into the HTML, need to at least stringify.
  let problem: any = props.error.problem;

  let detailsErrorContent;
  if( problem instanceof Error ){
    log.debug("problem type: Error");
    detailsErrorContent = <span>
      <TextSpan>{problem.name} - {problem.message}</TextSpan>
      <br/>
      <ErrorMoreDetailsExpando error={problem}/>
    </span>
  } else {
    detailsErrorContent = <TextSpan>
      {JSON.stringify(problem)}
    </TextSpan>
  }

  return <TextSpan>
    <TextSpan>{props.error.message}</TextSpan>
    <br/>
    {detailsErrorContent}
  </TextSpan>;
}

function ErrorMoreDetailsExpando(props:{error: Error}){
  const [showMore, setShowMore] = React.useState(false);

  if( !props.error ){
    return <div><pre>empty error</pre></div>
  }

  return <div>
    <SecondaryButton size="small" onClick={()=> setShowMore(!showMore)}>
      More
    </SecondaryButton>
    { showMore &&
    <div>
      <ErrorMessage error={props.error}/>
      <ErrorStack error={props.error}/>
    </div>
    }
  </div>
}

function ErrorMessage(props:{error: Error}){
  if( !props.error ){
    return null;
  }

  // if( props.error instanceof InvalidJsonError ){
  //   return <TextSpan>
  //     <pre>{props.error.validationError.message}</pre>
  //     <pre>{props.error.validationError.code}</pre>
  //     <pre>{props.error.validationError.dataPath}</pre>
  //     <pre>{props.error.validationError.schemaPath}</pre>
  //   </TextSpan>;
  // }

  return <TextSpan>
    <pre>{props.error.message}</pre>
  </TextSpan>;
}

function ErrorStack(props: {error: Error}){
  if( !props.error ){
    return null;
  }

  const stack = getErrorStack(props.error);

  if( !stack ){
    return <pre>empty error stack</pre>;
  }

  const stackString = JSON.stringify(stack);
  if( !stackString ){
    return  <pre>couldn't stringify error</pre>;
  }

  return (
    <pre>{
      stackString.replace(new RegExp(/\\n/, 'g'), "\n").
        // weird format from TV4
        replace(new RegExp(/@https/, 'g'), "\n@https")
    }</pre>
  );
}

function getErrorStack(error: Error): string | undefined {
  return error.stack;
}

// function invalidJsonErrorStack(error: InvalidJsonError | Error):
// string | undefined {
//   if( error instanceof InvalidJsonError ){
//     let stack = (error.validationError as any).stack;
//     if( typeof stack === "string" ){
//       return stack;
//     }
//   }
//
//   return error.stack;
// }