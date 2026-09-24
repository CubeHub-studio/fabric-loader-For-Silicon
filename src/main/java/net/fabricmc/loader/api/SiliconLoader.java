/*
 * Copyright 2026 Cubes Studio
 *
 * Licensed under the Apache License, Version 2.0.
 */

package net.fabricmc.loader.api;

import net.fabricmc.loader.impl.SiliconCompatibility;

/**
 * Fabric Loader integration API for the Silicon runtime.
 *
 * <p>Silicon compatibility is optional and disabled by default. When enabled,
 * Fabric Loader exposes its real startup state through this API.</p>
 */
public final class SiliconLoader {
	private SiliconLoader() { }

	/**
	 * Returns whether Silicon compatibility was enabled for this loader process.
	 */
	public static boolean isEnabled() {
		return SiliconCompatibility.isEnabled();
	}

	/**
	 * Returns the current Silicon startup state.
	 */
	public static String getState() {
		return SiliconCompatibility.getState();
	}

	/**
	 * Returns the Fabric Loader version used by the Silicon-compatible loader.
	 */
	public static String getLoaderVersion() {
		return SiliconCompatibility.getLoaderVersion();
	}

	/**
	 * Returns the current Fabric environment, or {@code "unknown"} before initialization.
	 */
	public static String getEnvironment() {
		return SiliconCompatibility.getEnvironment();
	}

	/**
	 * Returns the number of mods resolved by Fabric Loader.
	 */
	public static int getModCount() {
		return SiliconCompatibility.getModCount();
	}

	/**
	 * Returns a compact machine-readable startup report.
	 */
	public static String getStatus() {
		return SiliconCompatibility.getStatus();
	}
}
