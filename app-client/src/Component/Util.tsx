export function getRoleForKey(role:string) {
    const roleMap = {
        SP_ADMIN: "Administrator",
        SP_USER: "User",
        OPERATOR: "Operator",
    };
    const displayRole = roleMap[role as keyof typeof roleMap];
    return displayRole || "Unknown";
}
