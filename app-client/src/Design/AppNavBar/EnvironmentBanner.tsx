import React from "react";
import {Config} from "Config";

export default function EnvironmentBanner() {
    if (Config.isProd) {
        return null;
    }

    return (
        <aside className="raido-environment-banner">
            {Config.environmentName.toUpperCase()}
        </aside>
    );
}
