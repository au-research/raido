import { Failure } from "@/types";

const RaidFormErrorMessage = (
  error: Error,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  openErrorDialog: any
): void => {
  const message: string[] = [];
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  let messageParsed: any;

  try {
    messageParsed = JSON.parse(error.message);
  } catch (parseError) {
    // If parsing fails, handle the error message as a plain string
    message.push(error.message);
    console.log("error.message", JSON.stringify(error.message));
    messageParsed = null;
  }

  if (messageParsed) {
    if (messageParsed.failures) {
      messageParsed.failures.forEach((failure: Failure) => {
        message.push(`${failure.fieldId}: ${failure.message}`);
      });
    } else if (messageParsed.title) {
      message.push(messageParsed.title);
    } else {
      message.push("An error occurred.");
    }
  }
  openErrorDialog(message);
  throw new Error(message.join(", "));
};

RaidFormErrorMessage.displayName = "RaidFormErrorMessage";
export default RaidFormErrorMessage;
