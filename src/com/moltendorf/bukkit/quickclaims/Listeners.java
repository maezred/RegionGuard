package com.moltendorf.bukkit.quickclaims;

import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import com.mewin.WGRegionEvents.events.RegionLeftEvent;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

/**
 * Listener register.
 *
 * @author moltendorf
 */
public class Listeners implements Listener {

	final protected Plugin plugin;

	final protected PlayerStore players = new PlayerStore();

	protected Listeners(final Plugin instance) {
		plugin = instance;

		final CommandSender sender = plugin.getServer().getConsoleSender();
		final WorldGuardPlugin wg = WGBukkit.getPlugin();

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			Location location = player.getLocation();
			World world = location.getWorld();
			RegionManager manager = WGBukkit.getRegionManager(world);

			for (ProtectedRegion region : manager.getApplicableRegions(location)) {
				if (isPrivateRegion(region) || region.isMember(player.getName())) {
					players.put(world, region, player);
				}
			}
		}

		for (World world : plugin.getServer().getWorlds()) {
			RegionManager manager = WGBukkit.getRegionManager(world);

			for (Map.Entry<String, ProtectedRegion> entry : manager.getRegions().entrySet()) {
				ProtectedRegion region = entry.getValue();

				if (players.isEmpty(world, region)) {
					setInactiveFlagsOnRegion(region);
				} else {
					setActiveFlagsOnRegion(region);
				}
			}
		}
	}

	protected void setActiveFlagsOnRegion(ProtectedRegion region) {
		CommandSender sender = plugin.getServer().getConsoleSender();
		WorldGuardPlugin wg = WGBukkit.getPlugin();

		try {
			region.setFlag(DefaultFlag.CREEPER_EXPLOSION, DefaultFlag.CREEPER_EXPLOSION.parseInput(wg, sender, "allow"));
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			region.setFlag(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE, DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE.parseInput(wg, sender, "allow"));
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			region.setFlag(DefaultFlag.ENDER_BUILD, DefaultFlag.ENDER_BUILD.parseInput(wg, sender, "allow"));
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			region.setFlag(DefaultFlag.GHAST_FIREBALL, DefaultFlag.GHAST_FIREBALL.parseInput(wg, sender, "allow"));
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			region.setFlag(DefaultFlag.OTHER_EXPLOSION, DefaultFlag.OTHER_EXPLOSION.parseInput(wg, sender, "allow"));
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}
	}

	protected void setInactiveFlagsOnRegion(ProtectedRegion region) {
		CommandSender sender = plugin.getServer().getConsoleSender();
		WorldGuardPlugin wg = WGBukkit.getPlugin();

		try {
			region.setFlag(DefaultFlag.CREEPER_EXPLOSION, DefaultFlag.CREEPER_EXPLOSION.parseInput(wg, sender, "deny"));
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			region.setFlag(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE, DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE.parseInput(wg, sender, "deny"));
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			region.setFlag(DefaultFlag.ENDER_BUILD, DefaultFlag.ENDER_BUILD.parseInput(wg, sender, "deny"));
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			region.setFlag(DefaultFlag.GHAST_FIREBALL, DefaultFlag.GHAST_FIREBALL.parseInput(wg, sender, "deny"));
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			region.setFlag(DefaultFlag.OTHER_EXPLOSION, DefaultFlag.OTHER_EXPLOSION.parseInput(wg, sender, "deny"));
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}
	}

	protected boolean isPrivateRegion(ProtectedRegion region) {
		do {
			if (region.getOwners().getPlayers().size() > 0) {
				return true;
			}

			region = region.getParent();
		} while (region != null);

		return false;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void RegionEnteredEventMonitor(final RegionEnteredEvent event) {
		ProtectedRegion region = event.getRegion();
		Player player = event.getPlayer();

		if (isPrivateRegion(region) || region.isMember(player.getName())) {
			if (players.isEmpty(player.getWorld(), region)) {
				setActiveFlagsOnRegion(region);

				System.out.println("Disabled protection on region " + event.getRegion().getId() + " in world " + event.getPlayer().getWorld().getName() + ".");
			}

			players.put(region, player);

			System.out.println("Member " + event.getPlayer().getDisplayName() + " entered region " + event.getRegion().getId() + " in world " + event.getPlayer().getWorld().getName() + ".");
		} else {
			System.out.println("Player " + event.getPlayer().getDisplayName() + " entered region " + event.getRegion().getId() + " in world " + event.getPlayer().getWorld().getName() + ".");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void RegionLeaveEventMonitor(final RegionLeftEvent event) {
		ProtectedRegion region = event.getRegion();
		Player player = event.getPlayer();

		if (players.remove(region, player)) {
			System.out.println("Member " + event.getPlayer().getDisplayName() + " left region " + event.getRegion().getId() + " in world " + event.getPlayer().getWorld().getName() + ".");

			if (players.isEmpty(player.getWorld(), region)) {
				setInactiveFlagsOnRegion(region);

				System.out.println("Enabled protection on region " + event.getRegion().getId() + " in world " + event.getPlayer().getWorld().getName() + ".");
			}
		} else {
			System.out.println("Player " + event.getPlayer().getDisplayName() + " left region " + event.getRegion().getId() + " in world " + event.getPlayer().getWorld().getName() + ".");
		}
	}
}
