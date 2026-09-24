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

        boot(context) {
            const host = root.SiliconFabricHost;

            if (!host || typeof host.boot !== "function") {
                throw new Error(
                    "Fabric Loader 0.19.5 requires a Java host bridge. " +
                    "The GitHub Pages backend is loaded, but Gandi cannot execute the Java Fabric Loader."
                );
            }

            const result = host.boot({
                loader: "Fabric",
                version: VERSION,
                silicon: context && context.silicon ? context.silicon : null
            });

            if (result === false) {
                throw new Error("Fabric host bridge rejected startup.");
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
