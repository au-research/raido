import { Typography } from "@mui/material";
import { SecondaryButton } from "Component/AppButton";
import { ErrorInfo } from "Error/ErrorUtil";
import * as React from "react";

const log = console;

export function ErrorInfoComponent(props: { error: ErrorInfo }) {
  // explicitly calling out that it's "any" to remind that you can't just
  // dump the object into the HTML, need to at least stringify.
  let problem: any = props.error.problem;

  let detailsErrorContent;
  if (problem instanceof Error) {
    log.debug("problem type: Error");
    detailsErrorContent = (
      <span>
        <Typography>
          {problem.name} - {problem.message}
        </Typography>
        <br />
        <ErrorMoreDetailsExpando error={problem} />
      </span>
    );
  } else {
    detailsErrorContent = <pre>{JSON.stringify(problem)}</pre>;
  }

  return (
    <Typography>
      <span>{props.error.message}</span>
      <br />
      {detailsErrorContent}
    </Typography>
  );
}

function ErrorMoreDetailsExpando(props: { error: Error }) {
  const [showMore, setShowMore] = React.useState(false);

  if (!props.error) {
    return (
      <div>
        <pre>empty error</pre>
      </div>
    );
  }

  return (
    <div>
      <SecondaryButton size="small" onClick={() => setShowMore(!showMore)}>
        More
      </SecondaryButton>
      {showMore && (
        <div>
          <ErrorMessage error={props.error} />
          <ErrorStack error={props.error} />
        </div>
      )}
    </div>
  );
}

function ErrorMessage(props: { error: Error }) {
  if (!props.error) {
    return null;
  }

  return (
    <Typography>
      <pre>{props.error.message}</pre>
    </Typography>
  );
}

function ErrorStack(props: { error: Error }) {
  if (!props.error) {
    return null;
  }

  const stack = getErrorStack(props.error);

  if (!stack) {
    return <pre>empty error stack</pre>;
  }

  const stackString = JSON.stringify(stack);
  if (!stackString) {
    return <pre>couldn't stringify error</pre>;
  }

  return (
    <pre>
      {stackString
        .replace(new RegExp(/\\n/, "g"), "\n")
        // weird format from TV4
        .replace(new RegExp(/@https/, "g"), "\n@https")}
    </pre>
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
