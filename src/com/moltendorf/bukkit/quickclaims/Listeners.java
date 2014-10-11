package com.moltendorf.bukkit.quickclaims;

import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import com.mewin.WGRegionEvents.events.RegionLeftEvent;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Listener register.
 *
 * @author moltendorf
 */
public class Listeners implements Listener {

	final protected Plugin plugin;

	protected Listeners(final Plugin instance) throws InvalidFlagFormat {
		plugin = instance;

		final CommandSender sender = plugin.getServer().getConsoleSender();
		final WorldGuardPlugin wg = WGBukkit.getPlugin();

		final Map<Flag<?>, Object> flags = new LinkedHashMap<Flag<?>, Object>() {{
			put(DefaultFlag.CREEPER_EXPLOSION, DefaultFlag.CREEPER_EXPLOSION.parseInput(wg, sender, "deny"));
			put(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE, DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE.parseInput(wg, sender, "deny"));
			put(DefaultFlag.ENDER_BUILD, DefaultFlag.ENDER_BUILD.parseInput(wg, sender, "deny"));
			put(DefaultFlag.GHAST_FIREBALL, DefaultFlag.GHAST_FIREBALL.parseInput(wg, sender, "deny"));
			put(DefaultFlag.OTHER_EXPLOSION, DefaultFlag.OTHER_EXPLOSION.parseInput(wg, sender, "deny"));
		}};

		for (World world : plugin.getServer().getWorlds()) {
			RegionManager manager = WGBukkit.getRegionManager(world);

			for (Map.Entry<String, ProtectedRegion> entry : manager.getRegions().entrySet()) {
				ProtectedRegion region = entry.getValue();

				region.setFlags(flags);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void RegionEnteredEventMonitor(final RegionEnteredEvent event) {
		final Player player = event.getPlayer();

		System.out.println("RegionEnteredEventMonitor: " + player.getDisplayName());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void RegionLeftEventMonitor(final RegionLeftEvent event) {
		final Player player = event.getPlayer();

		System.out.println("RegionLeftEventMonitor: " + player.getDisplayName());
	}
}
