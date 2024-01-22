import * as React from "react";
import {Card, CardContent, CardHeader, Container, Dialog} from "@mui/material";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import {ErrorInfo} from "Error/ErrorUtil";
import Divider from "@mui/material/Divider";
import {ErrorInfoComponent} from "Error/ErrorInforComponent";
import {PrimaryButton} from "Component/AppButton";
import {TextSpan} from "Component/TextSpan";
import {InfoField, InfoFieldList} from "Component/InfoField";
import {getIdProvider} from "Component/GetIdProvider";
import {getRoleForKey} from "Component/Util";
import {formatLocalDateAsIsoShortDateTime} from "Util/DateUtil";
import {useAuthInAnyContext} from "Auth/AuthProvider";

const log = console;

type State = {
    dialogOpen: boolean,
    errors: ErrorInfo[],
}

type Action =
    { type: 'handleError', error: ErrorInfo } |
    { type: 'closeDialog' };

function handleError(state: State, action: { error: ErrorInfo }): State {
    if (state.dialogOpen) {
        return {
            ...state,
            // add new error *in addition* to what is already being displayed,
            // because the user might still be inspecting the old problem when a
            // new error occurs
            errors: state.errors.concat(action.error)
        };
    } else {
        return {
            ...state, dialogOpen: true,
            // we clear any previous errors when opening the dialog, instead of
            // when closing so that the dialog doesn't "flicker" as it's
            // transitioning away
            errors: [action.error]
        };
    }
}

function reducer(state: State, action: Action): State {
    switch (action.type) {
        case 'handleError':
            return handleError(state, action);
        case "closeDialog":
            return {...state, dialogOpen: false}
    }
}

export type ErrorDispatch = React.Dispatch<React.ReducerAction<typeof reducer>>;

const errorDialogContext = React.createContext<ErrorDispatch>(() =>
    log.error("ErrorDialogContext consumed outside scope of provider"));

/* Implemented with a reducer because if simple `useState()` hooks
were used, then every time some leaf component  handed off an error, every
component that used this context would be re-rendered.
This would be especially bad because things like `AuthenticatonProvider` use
this error handler, and when they re-render they lose their state (and
so would have to re-authenticate, etc.)
The syntax to call `handleError()` as a dispatch is a little funky, maybe
this function could return a convenience function that calls dispatch?
Is that allowed with hooks?
*/
export function useOpenErrorDialog(): (value: Action) => void {
    return React.useContext(errorDialogContext);
}


/** This component is intended to be used (via context) from everywhere in
 * the app to show details of "handled" errors: i.e. server errors,
 * unexpected / bad data etc.
 * Generally speaking, UI feedback that a "handled" error occurred should
 * be shown "inline" in the page, via something like the CompactErrorPanel.
 * If the user indicates they want more details, we then show the error as a
 * dialog "on top" of the screen so that the rest of the app component hierarchy
 * doesn't need to worry about being unmounted to show an error screen -
 * React just keeps the component alive underneath the dialog.
 * Additionally, rendering the dialog globally allows the fade-out transition
 * to work properly when the dialog is dismissed and it's nice that we don't
 * have tons of Dialog components sprinkled all throughout the app hierarchy
 * - errors are pretty common things, at least in my apps :P
 * The dialog models errors as a list, because you may have multiple components
 * doing things asynchronously that may run into problems.
 * The main place where you'd be calling this component from (rather than just
 * using a CompactErrorPanel) is when unexpected
 * things happen while doing something in the background - i.e. there
 * is no UI element to associate with the error.
 */
export function ErrorDialogProvider(props: { children: React.ReactNode }) {
    const [errorState, dispatch] = React.useReducer(
        reducer, {dialogOpen: false, errors: []});

    return <errorDialogContext.Provider value={dispatch}>
        {props.children}
        <ErrorDialog dialogOpen={errorState.dialogOpen} errors={errorState.errors}
                     onCloseClicked={() => dispatch({type: "closeDialog"})}/>

    </errorDialogContext.Provider>
}

/* This needs to catch browser navigation (esp. "back") and we should dismiss
 the modal - I hate that the back button doesn't currently work with this. */
function ErrorDialog(props: {
    dialogOpen: boolean,
    errors: ErrorInfo[],
    onCloseClicked: () => void,
}) {

    const filteredErrors: ErrorInfo[] = [];
    props.errors.forEach(i => {
        if (filteredErrors.some(j => i.message === j.message)) {
            // IMPROVE: this filters errors with same message but different problem
            console.info("ignored duplicate error message", i);
        } else {
            filteredErrors.push(i);
        }
    });

    return <Dialog fullScreen={true} open={props.dialogOpen}
                   onClose={props.onCloseClicked}
        /* on ios (iphone 8+), the error message wasn't visible
        Was able to reproduce in local dev by setting profile to iphone 4. */
                   scroll={"body"}
    >
        <DialogTitle id="error-dialog-title">
            Errors
        </DialogTitle>
        <DialogContent>
            <div>
                <ul>{filteredErrors.reverse().map((i, index) => {
                    return <li key={index}>
                        <ErrorInfoComponent error={i}/>
                    </li>
                })}</ul>
            </div>
        </DialogContent>

        <DialogActions sx={{
            display: "flex",
            gap: "1em",
            justifyContent: "center",
            width: "100%",
        }}>
            <PrimaryButton color="primary"
                           onClick={() => window.location.reload()}
                           disabled={false}
            >
                Refresh browser
            </PrimaryButton>
            <PrimaryButton color="primary"
                           onClick={props.onCloseClicked}
                           disabled={false}
            >
                Dismiss errors
            </PrimaryButton>
        </DialogActions>

        <br/><Divider/><br/>
        <Container maxWidth="sm">
            <SignInDetails/>
            <br/>
        </Container>

    </Dialog>
}

/* There are two ErrorDialog providers defined in App.tsx, we might be rendering
in the context of an authorized or unauthorized user. */
function SignInDetails() {
    const auth = useAuthInAnyContext();

    let details = <TextSpan>
        You have not signed in tho the app.
    </TextSpan>;
    if (auth) {
        const {session} = auth;
        const {payload: user} = session;
        const expiry = session.accessTokenExpiry;
        const formattedExpiry = formatLocalDateAsIsoShortDateTime(expiry);
        const isSessionExpired = expiry.getTime() <= new Date().getTime();
        const expiryFieldColor = isSessionExpired ? "red" : "inherit";

        details = <InfoFieldList>
            <InfoField id={"email"} label={"Identity"} value={user.email}/>
            <InfoField id={"idProvider"} label={"ID provider"}
                       value={getIdProvider(user.clientId)}/>

            <InfoField id={"sessionSignIn"} label={"Signed in at"}
                       value={formatLocalDateAsIsoShortDateTime(session.accessTokenIssuedAt)}/>

            <InfoField id={"sessionExpiry"} label="Sign-in expiry"
                       value={
                           <TextSpan color={expiryFieldColor}>{formattedExpiry}</TextSpan>
                       }
            />

            <InfoField id={"role"} label={"Role"} value={getRoleForKey(user.role)}/>
        </InfoFieldList>
    }
    return (
        <Card>
            <CardHeader title="Signed-in user"/>
            <CardContent>
                {details}
            </CardContent>
        </Card>
    );
}