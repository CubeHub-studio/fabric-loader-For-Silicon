// Fabric Loader for Silicon - Java host bridge descriptor
// This file is intentionally small: the real Fabric Loader remains Java.
// Silicon loads this endpoint as its Fabric backend descriptor.
//
// If a host application embeds the Java fork, it may provide
// globalThis.SiliconFabricHost.boot(context). The Gandi extension itself
// cannot execute Java, so without that host bridge this backend reports the
// limitation instead of pretending Fabric has started.

(function (root) {
    "use strict";

    const VERSION = "0.19.5";
    const URL = "https://cubehub-studio.github.io/fabric-loader-For-Silicon/";

    root.SiliconLoaders = root.SiliconLoaders || {};

    root.SiliconLoaders.fabric = {
        name: "Fabric",
        version: VERSION,
        url: URL,

        async boot(context) {
            const host = root.SiliconFabricHost;

            if (host && typeof host.boot === "function") {
                const result = await host.boot({
                    loader: "Fabric",
                    version: VERSION,
                    silicon: context && context.silicon ? context.silicon : null
                });

                if (result === false) {
                    throw new Error("Fabric host bridge rejected startup.");
                }

                return true;
            }

            // Browser/Gandi fallback: connect to the Java host bridge.
            const port = context && context.silicon && context.silicon.loaderConfig
                ? context.silicon.loaderConfig.port || 8765
                : 8765;
            const base = "http://127.0.0.1:" + Number(port);

            if (typeof fetch !== "function") {
                throw new Error("No browser fetch API is available for the Fabric host bridge.");
            }

            const response = await fetch(base + "/boot", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({loader: "Fabric", version: VERSION})
            });

            const text = await response.text();
            if (!response.ok) {
                let message = text;
                try {
                    message = JSON.parse(text).error || text;
                } catch (_) {}
                throw new Error(message || ("Fabric host returned HTTP " + response.status));
            }

            return true;
        },

        state() {
            const host = root.SiliconFabricHost;
            return host && typeof host.state === "function"
                ? String(host.state())
                : "host-required";
        }
    };
})(typeof globalThis !== "undefined" ? globalThis : this);
