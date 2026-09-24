package net.fabricmc.loader.impl.launch.silicon;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.SiliconCompatibility;
import net.fabricmc.loader.impl.game.GameProvider;

/**
 * Minimal local HTTP bridge for Silicon-compatible Fabric Loader hosts.
 *
 * <p>This bridge is opt-in. Start with:
 * {@code -Dfabric.silicon=true -Dfabric.silicon.bridge=true}
 */
public final class SiliconHostBridge {
	private static final Map<String, SiliconHostBridge> SERVERS = new ConcurrentHashMap<>();

	private final HttpServer server;

	private SiliconHostBridge(HttpServer server) {
		this.server = server;
	}

	public static SiliconHostBridge start(int port) throws IOException {
		String key = String.valueOf(port);
		if (SERVERS.containsKey(key)) return SERVERS.get(key);

		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
		SiliconHostBridge bridge = new SiliconHostBridge(server);

		server.createContext("/", SiliconHostBridge::root);
		server.createContext("/status", SiliconHostBridge::status);
		server.createContext("/state", SiliconHostBridge::state);
		server.createContext("/boot", SiliconHostBridge::boot);
		server.createContext("/unload", SiliconHostBridge::unload);
		server.setExecutor(null);
		server.start();

		SERVERS.put(key, bridge);
		return bridge;
	}

	public void stop() {
		server.stop(0);
	}

	private static void root(HttpExchange exchange) throws IOException {
		addCors(exchange);
		write(exchange, 200, "{\"service\":\"Silicon Fabric Host\",\"version\":\"0.19.5\"}");
	}

	private static void status(HttpExchange exchange) throws IOException {
		addCors(exchange);
		write(exchange, 200, SiliconCompatibility.getStatus());
	}

	private static void state(HttpExchange exchange) throws IOException {
		addCors(exchange);
		write(exchange, 200, "{\"state\":\"" + escape(SiliconCompatibility.getState()) + "\"}");
	}

	private static void boot(HttpExchange exchange) throws IOException {
		addCors(exchange);
		if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
			write(exchange, 405, "{\"error\":\"POST required\"}");
			return;
		}

		FabricLoaderImpl loader = FabricLoaderImpl.INSTANCE;

		if (!SiliconCompatibility.isEnabled()) {
			write(exchange, 409, "{\"error\":\"Silicon compatibility is disabled\"}");
			return;
		}

		if ("ready".equals(SiliconCompatibility.getState())) {
			write(exchange, 200, SiliconCompatibility.getStatus());
			return;
		}

		try {
			GameProvider provider = loader.tryGetGameProvider();

			if (provider == null) {
				write(exchange, 503, "{\"error\":\"Fabric game provider is not initialized\"}");
				return;
			}

			// The actual Fabric lifecycle is owned by Knot. The bridge only
			// exposes its state and never fakes readiness.
			if (!"initialized".equals(SiliconCompatibility.getState())
					&& !"starting".equals(SiliconCompatibility.getState())) {
				write(exchange, 409, "{\"error\":\"Fabric Loader is not in a bootable Silicon state\"}");
				return;
			}

			write(exchange, 200, SiliconCompatibility.getStatus());
		} catch (Throwable error) {
			SiliconCompatibility.failed(error);
			write(exchange, 500, "{\"error\":\"" + escape(error.getMessage()) + "\"}");
		}
	}

	private static void unload(HttpExchange exchange) throws IOException {
		addCors(exchange);
		if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
			write(exchange, 405, "{\"error\":\"POST required\"}");
			return;
		}

		write(exchange, 200, "{\"state\":\"unload-requested\"}");
	}

	private static void addCors(HttpExchange exchange) {
		exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
		exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
	}

	private static void write(HttpExchange exchange, int code, String body) throws IOException {
		byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
		exchange.sendResponseHeaders(code, bytes.length);
		try (OutputStream out = exchange.getResponseBody()) {
			out.write(bytes);
		}
	}

	private static String escape(String value) {
		if (value == null) return "";
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
