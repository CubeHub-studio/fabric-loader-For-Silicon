/*
 * Copyright 2026 Cubes Studio
 *
 * Licensed under the Apache License, Version 2.0.
 */

package net.fabricmc.loader.impl;

import net.fabricmc.loader.impl.util.SystemProperties;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;

/**
 * Internal Silicon integration for Fabric Loader.
 *
 * <p>This class does not replace Fabric's launcher. It exposes the real Fabric
 * initialization lifecycle to a Silicon-compatible host.</p>
 */
public final class SiliconCompatibility {
	private static volatile String state = "disabled";
	private static volatile String environment = "unknown";
	private static volatile int modCount;

	private SiliconCompatibility() { }

	public static boolean isEnabled() {
		return SystemProperties.isSet(SystemProperties.SILICON);
	}

	public static void begin(FabricLoaderImpl loader) {
		if (!isEnabled()) {
			state = "disabled";
			return;
		}

		state = "starting";
		environment = "unknown";
		modCount = 0;

		Log.info(LogCategory.GENERAL, "Silicon compatibility enabled.");
	}

	public static void initialized(FabricLoaderImpl loader) {
		if (!isEnabled()) return;

		environment = loader.getEnvironmentType().name().toLowerCase();
		modCount = loader.getAllMods().size();
		state = "initialized";

		Log.info(LogCategory.GENERAL,
				"Silicon compatibility initialized: environment=%s, mods=%d",
				environment, modCount);
	}

	public static void ready(FabricLoaderImpl loader) {
		if (!isEnabled()) return;

		environment = loader.getEnvironmentType().name().toLowerCase();
		modCount = loader.getAllMods().size();
		state = "ready";

		Log.info(LogCategory.GENERAL, "Silicon compatibility ready.");
	}

	public static void failed(Throwable error) {
		if (!isEnabled()) return;

		state = "error";
		Log.error(LogCategory.GENERAL, "Silicon compatibility failed.", error);
	}

	public static String getState() {
		return state;
	}

	public static String getLoaderVersion() {
		return FabricLoaderImpl.VERSION;
	}

	public static String getEnvironment() {
		return environment;
	}

	public static int getModCount() {
		return modCount;
	}

	public static String getStatus() {
		return "{"
				+ "\"enabled\":" + isEnabled()
				+ ",\"state\":\"" + state + "\""
				+ ",\"loader\":\"Fabric\""
				+ ",\"loaderVersion\":\"" + FabricLoaderImpl.VERSION + "\""
				+ ",\"environment\":\"" + environment + "\""
				+ ",\"mods\":" + modCount
				+ "}";
	}
}
