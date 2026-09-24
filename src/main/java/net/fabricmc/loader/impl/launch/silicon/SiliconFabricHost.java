package net.fabricmc.loader.impl.launch.silicon;

import net.fabricmc.loader.impl.launch.knot.Knot;
import net.fabricmc.loader.api.SiliconLoader;

/**
 * Standalone Java host for the Silicon/Fabric bridge.
 *
 * <p>This process owns the real Fabric Loader. Silicon connects to its local
 * HTTP bridge; it never attempts to execute Java in the browser.</p>
 */
public final class SiliconFabricHost {
	private SiliconFabricHost() { }

	public static void main(String[] args) {
		System.setProperty("fabric.silicon", "true");
		System.setProperty("fabric.silicon.bridge", "true");

		if (System.getProperty("fabric.silicon.port") == null) {
			System.setProperty("fabric.silicon.port", "8765");
		}

		int port = Integer.getInteger("fabric.silicon.port", 8765);

		try {
			SiliconHostBridge.start(port);
			System.out.println("Silicon Fabric Host");
			System.out.println("Fabric Loader: " + SiliconLoader.getLoaderVersion());
			System.out.println("Bridge: http://127.0.0.1:" + port);
			System.out.println("State: " + SiliconLoader.getState());
			System.out.println();

			// Knot performs the real Fabric discovery, transformation and
			// initialization. We do not report success independently of it.
			new Knot(null).init(args);

			System.out.println("Fabric Loader state: " + SiliconLoader.getState());
			System.out.println("Silicon status: " + SiliconLoader.getStatus());
		} catch (Throwable error) {
			SiliconCompatibility.failed(error);
			System.err.println("Silicon Fabric Host failed: " + error);
			error.printStackTrace();
			System.exit(1);
		}
	}
}
