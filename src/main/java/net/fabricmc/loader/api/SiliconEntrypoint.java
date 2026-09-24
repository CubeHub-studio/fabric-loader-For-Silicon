/*
 * Copyright 2026 Cubes Studio
 *
 * Licensed under the Apache License, Version 2.0.
 */

package net.fabricmc.loader.api;

/**
 * Optional entrypoint for mods that want to integrate with Silicon.
 *
 * <p>Silicon entrypoints are invoked after Fabric Loader has resolved and
 * frozen the mod set, immediately before normal {@code preLaunch} entrypoints.</p>
 */
@FunctionalInterface
public interface SiliconEntrypoint {
	/**
	 * Called when Silicon-compatible Fabric startup reaches the integration phase.
	 */
	void onSiliconLoad();
}
