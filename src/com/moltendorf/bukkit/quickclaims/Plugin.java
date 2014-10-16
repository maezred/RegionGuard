package com.moltendorf.bukkit.quickclaims;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author moltendorf
 */
public class Plugin extends JavaPlugin {

	// Variable data.
	protected Configuration configuration = null;
	protected Listeners listeners = null;

	@Override
	public synchronized void onDisable() {

		// Clear data.
		configuration = null;
	}

	@Override
	public synchronized void onEnable() {

		// Construct new configuration.
		configuration = new Configuration();

		// Are we enabled?
		if (!configuration.global.enabled) {
			return;
		}

		// Get server.
		final Server server = getServer();

		// Get plugin manager.
		final PluginManager manager = server.getPluginManager();

		listeners = new Listeners(this);

		// Register our event listeners.
		manager.registerEvents(listeners, this);
	}
}
