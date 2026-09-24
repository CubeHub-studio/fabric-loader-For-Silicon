fabric-loader-For-Silicon
=========================

This is a Silicon-compatible fork of Fabric Loader 0.19.5.

## Silicon compatibility

Silicon compatibility is implemented as an optional Java-side bridge. Normal
Fabric behavior is unchanged unless the property below is enabled:

```
-Dfabric.silicon=true
```

When enabled, the loader:

1. Starts the normal Fabric Loader lifecycle.
2. Resolves and freezes the real Fabric mod set.
3. Exposes the real loader state through `net.fabricmc.loader.api.SiliconLoader`.
4. Invokes the optional `silicon` mod entrypoint.
5. Reports `ready` only after normal Fabric pre-launch initialization succeeds.

### Silicon API

```java
SiliconLoader.isEnabled()
SiliconLoader.getState()
SiliconLoader.getLoaderVersion()
SiliconLoader.getEnvironment()
SiliconLoader.getModCount()
SiliconLoader.getStatus()
```

The status report is machine-readable JSON-like text, for example:

```json
{"enabled":true,"state":"ready","loader":"Fabric","loaderVersion":"0.19.5","environment":"client","mods":12}
```

### Silicon entrypoint

A Fabric mod may optionally declare a `silicon` entrypoint and implement
`net.fabricmc.loader.api.SiliconEntrypoint`:

```json
{
  "entrypoints": {
    "silicon": [
      "com.example.MySiliconEntrypoint"
    ]
  }
}
```

The entrypoint is called after Fabric has resolved and frozen the mod set and
before the normal `preLaunch` entrypoints.

## Important

This fork does **not** turn Fabric Loader into JavaScript and does not fake
Fabric startup. Fabric still performs its real Java class loading, mod
discovery, dependency resolution, transformation, and launch lifecycle.

Silicon is the integration layer around that real Fabric lifecycle.

## License

Licensed under the Apache License 2.0.
