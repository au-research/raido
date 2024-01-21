import {TypographyProps} from "@mui/material/Typography";
import {TextSpan} from "Component/TextSpan";
import {formatLocalDateAsIsoShortDateTime} from "Util/DateUtil";
import React from "react";


export function raidoTitle(pageName: string) {
    return `RAiD - ${pageName}`;
}

export function getRoleForKey(role:string) {
    const roleMap = {
        SP_ADMIN: "Administrator",
        SP_USER: "User",
        OPERATOR: "Operator",
    };
    const displayRole = roleMap[role as keyof typeof roleMap];
    return displayRole || "Unknown";
}

export function DateTimeDisplay({date, ...props}: {
    date?: Date,
} & TypographyProps) {
    return <TextSpan {...props}>
        {formatLocalDateAsIsoShortDateTime(date)}
    </TextSpan>
}

