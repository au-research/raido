import {TextSpan} from "Component/TextSpan";
import React, {ReactNode, useState} from "react";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {ApiKey, GenerateApiTokenRequest, UpdateApiKeyRequest} from "Generated/Raidv2";
import {useAuthApi} from "Api/AuthApi";
import {ContentCopy, West} from "@mui/icons-material"
import {CompactErrorPanel} from "Error/CompactErrorPanel";
import {
    Alert,
    Card,
    CardContent,
    CardHeader,
    Checkbox,
    Container,
    FormControl,
    FormControlLabel,
    IconButton,
    InputLabel,
    MenuItem,
    Select,
    SelectChangeEvent,
    Stack,
    TextField,
    Typography
} from "@mui/material";
import {PrimaryActionButton, SecondaryButton} from "Component/AppButton";
import {HelpChip, HelpPopover} from "Component/HelpPopover";
import {addDays} from "Util/DateUtil";
import {RqQuery} from "Util/ReactQueryUtil";
import {useParams, useSearchParams} from "react-router-dom";
import {useNavigate} from "react-router";

const viewPageUrl = "/api-key";
const createPageUrl = "/create-api-key";

export function getViewApiKeyPageLink(apiKeyId: number): string {
    return `${viewPageUrl}/${apiKeyId}`;
}

export function getCreateApiKeyPageLink(servicePointId: number): string {
    return `${createPageUrl}/${servicePointId}`;
}

export function ApiKeyPage() {
    return <Content/>
}

function Content() {
    const navigate = useNavigate()
    const [searchParams] = useSearchParams();
    const servicePointId = searchParams.get('servicePointId') || 0;
    const {apiKeyId: apiKeyIdParams} = useParams() as { apiKeyId: string };
    const [apiKeyId, setApiKeyId] = useState<number>();

    React.useEffect(() => {
        if (apiKeyIdParams) {
            setApiKeyId(+apiKeyIdParams);
        }
    }, [apiKeyIdParams])

    return (
        <Container>
            <ApiKeyContainer
                apiKeyId={apiKeyId}
                servicePointId={+servicePointId}
                onCreate={(createdId) => {
                    navigate(`${getViewApiKeyPageLink(createdId)}?servicePointId=${servicePointId}`, {replace: true});
                    setApiKeyId(createdId);
                }}
            />
        </Container>
    )
}

function isDifferent(formData: ApiKey, original: ApiKey) {
    return formData.subject !== original.subject ||
        formData.role !== original.role ||
        formData.enabled !== original.enabled ||
        formData.tokenCutoff?.getTime() !== original.tokenCutoff?.getTime();
}

function ApiKeyContainer({apiKeyId, servicePointId, onCreate}: {
    apiKeyId: number | undefined,
    servicePointId: number | undefined,
    onCreate: (aipKeyId: number) => void,
}) {
    const api = useAuthApi();
    const queryClient = useQueryClient();
    const queryName = 'readApiKey';
    const [formData, setFormData] = useState({
        // id set to null signals creation is being requested
        id: undefined as unknown as number,
        servicePointId: servicePointId,
        idProvider: "RAIDO_API",
        /* date will be ignored for create, but must be set to something or
         the openApi generated code will fail */
        dateCreated: new Date(),

        // these are the editable fields
        subject: "",
        role: "SP_USER",
        tokenCutoff: addDays(new Date(), 365),
        enabled: true
    } as ApiKey);
    const query: RqQuery<ApiKey> = useQuery(
        [queryName, apiKeyId],
        async () => {
            if (apiKeyId) {
                let apiKey = await api.admin.readApiKey({
                    apiKeyId: apiKeyId
                });
                setFormData({...apiKey});
                return apiKey;
            } else {
                return formData;
            }
        }
    );
    const updateRequest = useMutation(
        async (data: UpdateApiKeyRequest) => {
            const result = await api.admin.updateApiKey(data);
            if (!apiKeyId) {
                onCreate(result.id);
            }
            return result;
        },
        {
            onSuccess: async () => {
                await queryClient.invalidateQueries([queryName]);
            },
        }
    );
    const generateRequest = useMutation(
        async (data: GenerateApiTokenRequest) => {
            return await api.admin.generateApiToken({
                generateApiTokenRequest: data
            });
        }
    );

    if (query.error) {
        return <CompactErrorPanel error={query.error}/>
    }

    if (!query.data) {
        if (query.isLoading) {
            return <TextSpan>loading...</TextSpan>
        } else {
            console.log("unexpected state", query);
            return <TextSpan>unexpected state</TextSpan>
        }
    }

    const canEditSubject = !apiKeyId;
    const isWorking = query.isLoading || updateRequest.isLoading;
    const isValid = !!formData.subject;
    const hasChanged = isDifferent(formData, query.data);
    const canGenerateToken =
        !isWorking && !hasChanged && !updateRequest.isLoading && !!apiKeyId;
    const canSubmitUpdate =
        !isWorking && hasChanged && isValid && !updateRequest.isLoading;
    const servicePointIdFormatted = Intl.NumberFormat().format(+servicePointId!).replace(/,/g, ' ');
    return (

        <Card>
            <CardHeader action={
                <ApiKeyHelp/>
            }
                        title="API Key"
                        subheader={`Service point ${servicePointIdFormatted}`}/>


            <CardContent>
                <form autoComplete="off" onSubmit={(e) => {
                    e.preventDefault();
                    updateRequest.mutate({apiKey: {...formData}});
                }}>
                    <Stack spacing={2}>
                        <TextField id="subject" label="Subject" variant="outlined"
                                   autoFocus autoCorrect="off" autoCapitalize="on"
                                   disabled={isWorking || !canEditSubject}
                                   value={formData.subject || ''}
                                   onChange={(e) => {
                                       setFormData({...formData, subject: e.target.value});
                                   }}
                        />
                        <FormControl>
                            <InputLabel id="roleLabel">Role</InputLabel>
                            <Select labelId="roleLabel" id="roleSelect"
                                    value={formData.role ?? "SP_USER"}
                                    label="Role"
                                    onChange={(event: SelectChangeEvent) => {
                                        setFormData({...formData, role: event.target.value});
                                    }}
                            >
                                <MenuItem value={"SP_USER"}>Service Point User</MenuItem>
                                <MenuItem value={"SP_ADMIN"}>Service Point Admin</MenuItem>
                            </Select>
                        </FormControl>
                        <FormControl>
                            <Stack direction={"row"} spacing={2} alignItems={"center"}>
                                <TextField id="expires" label="Expire"
                                           variant="outlined" disabled
                                           value={Intl.DateTimeFormat("en-AU",{
                                                  dateStyle: "medium",
                                                  timeStyle: "short",
                                                    hour12: false,
                                           }).format(formData.tokenCutoff)}
                                />
                                <West/>
                                <SecondaryButton onClick={(e) => {
                                    e.preventDefault();
                                    setFormData({
                                        ...formData,
                                        tokenCutoff: addDays(formData.tokenCutoff, 365)
                                    });
                                }}>
                                    Extend expiry
                                </SecondaryButton>
                            </Stack>
                        </FormControl>
                        <FormControl>
                            <FormControlLabel
                                disabled={isWorking}
                                label="Enabled"
                                labelPlacement="start"
                                style={{
                                    /* by default, MUI lays this out as <checkbox><label>.
                                     Doing `labelPlacement=start`, flips that around, but ends up
                                     right-justigying the content, `marginRight=auto` pushes it back
                                     across to the left and `marginLeft=0` aligns nicely. */
                                    marginLeft: 0,
                                    marginRight: "auto",
                                }}
                                control={
                                    <Checkbox
                                        checked={formData.enabled ?? true}
                                        onChange={() => {
                                            setFormData({...formData, enabled: !formData.enabled})
                                        }}
                                    />
                                }
                            />
                        </FormControl>
                        <Stack direction={"row"} spacing={2}>
                            <SecondaryButton onClick={()=>window.history.back()}
                                             disabled={updateRequest.isLoading}>
                                Back
                            </SecondaryButton>
                            <PrimaryActionButton type="button" context={"generate token"}
                                                 error={undefined} variant={"outlined"}
                                                 disabled={!canGenerateToken}
                                                 onClick={e => {
                                                     e.preventDefault();
                                                     if (!apiKeyId) {
                                                         throw new Error("cannot call generate without an apiKeyId");
                                                     }
                                                     generateRequest.mutate({apiKeyId});
                                                 }}
                            >
                                Generate token
                            </PrimaryActionButton>
                            <PrimaryActionButton type="submit" context={"update api key"}
                                                 disabled={!canSubmitUpdate}
                                                 isLoading={updateRequest.isLoading}
                                                 error={updateRequest.error}
                            >
                                {apiKeyId ? "Update" : "Create"}
                            </PrimaryActionButton>
                        </Stack>
                        {generateRequest.data &&
                            <Alert severity="success">
                                <Typography>
                                    An API Token has been generated.
                                    <br/>
                                    Click to copy to clipboard <IconButton onClick={() =>
                                    navigator.clipboard.writeText(generateRequest.data?.apiToken!)
                                }><ContentCopy/></IconButton>
                                </Typography>
                            </Alert>
                        }
                        <CompactErrorPanel error={generateRequest.error}/>
                    </Stack>
                </form>
            </CardContent>
        </Card>

    )
}

function ApiKeyHelp() {
    return <HelpPopover content={
        <Stack spacing={1}>
            <HelpList>
                <HelpItem><HelpChip label={"Subject"}/>
                    Subject cannot be updated after the API key has been created.
                </HelpItem>
                <HelpItem><HelpChip label={"Role"}/>
                    Role can be changed, but only api-tokens after the change has been
                    saved will contain that role. Previously existing api-tokens will
                    continue to function with the original role.
                </HelpItem>
                <HelpItem><HelpChip label={"Expire"}/>
                    Note that if you use "Extend expiry" - you still need to generate a
                    new api-token if your old api-token is expired. Old, previously
                    generated api-tokens have their "expires at" set to the original
                    value as at the time they were generated (because they're only stored
                    by API clients, not the RAiD service; we couldn't update them if we
                    wanted to).
                    <br/>
                    The "extend expiry" button exists to allow you to extend your
                    api-key without having to create a whole new api-key with a new
                    subject - it won't let you define old, stale tokens that are past
                    their expiry date as "still valid".
                </HelpItem>
                <HelpItem><HelpChip label={"Enabled"}/>
                    Setting the api-key to "disabled" invalidates all issued api-tokens
                    for that api-key.
                    An API call authorized with an api-token associated with a disabled
                    api-key will be rejected, regardless of the expiry date in the token's
                    `exp` claim.
                </HelpItem>
                <HelpItem><HelpChip label={"Generate token"}/>
                    This generates a brand new api-token based on the current api-key
                    stored on the server (i.e. subject, expiry date, role, etc.)
                    The RAiD service does not store api-tokens on the server, every
                    time you click the "Generate token" button, the api-token generated
                    has an updated "issued at" claim, and the "expires at" claim is based
                    on the api-key "Expire" field.
                    If you change the "role" for your api-key, you must generate a new
                    api-token, previously generated api-tokens will still contain the
                    original "role" claim.
                </HelpItem>
            </HelpList>
        </Stack>
    }/>;
}

function HelpList({children}: { children: ReactNode }) {
    return <ul>
        {children}
    </ul>
}

function HelpItem({children}: { children: ReactNode }) {
    return <li style={{marginBottom: ".5em"}}>
        {children}
    </li>
}