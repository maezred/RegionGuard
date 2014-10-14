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
				if (isPrivateRegion(region) && isMemberOfRegion(player, region)) {
					players.put(world, region, player);

					memberEnteredRegion(player, region);
				} else {
					playerEnteredRegion(player, region);
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

	protected void playerEnteredRegion(Player player, ProtectedRegion region) {
		System.out.println("Player " + player.getDisplayName() + " entered region " + region.getId() + " in world " + player.getWorld().getName() + ".");
	}

	protected void memberEnteredRegion(Player player, ProtectedRegion region) {
		System.out.println("Member " + player.getDisplayName() + " entered region " + region.getId() + " in world " + player.getWorld().getName() + ".");
	}

	protected void playerLeftRegion(Player player, ProtectedRegion region) {
		System.out.println("Player " + player.getDisplayName() + " left region " + region.getId() + " in world " + player.getWorld().getName() + ".");
	}

	protected void memberLeftRegion(Player player, ProtectedRegion region) {
		System.out.println("Member " + player.getDisplayName() + " left region " + region.getId() + " in world " + player.getWorld().getName() + ".");
	}

	protected void setActiveFlagsOnRegion(ProtectedRegion region) {
		if (region.getId().equals("__global__")) {
			return;
		}

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

		System.out.println("Disabled protection on region " + region.getId() + ".");
	}

	protected void setInactiveFlagsOnRegion(ProtectedRegion region) {
		if (region.getId().equals("__global__")) {
			return;
		}

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

		System.out.println("Enabled protection on region " + region.getId() + ".");
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

	protected boolean isMemberOfRegion(Player player, ProtectedRegion region) {
		String playerName = player.getName().toLowerCase();

		do {
			if (region.getMembers().getPlayers().contains(playerName)) {
				return true;
			}

			if (region.getOwners().getPlayers().contains(playerName)) {
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

		if (isPrivateRegion(region) && isMemberOfRegion(player, region)) {
			if (players.isEmpty(player.getWorld(), region)) {
				setActiveFlagsOnRegion(region);
			}

			players.put(region, player);

			memberEnteredRegion(player, region);
		} else {
			playerEnteredRegion(player, region);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void RegionLeaveEventMonitor(final RegionLeftEvent event) {
		ProtectedRegion region = event.getRegion();
		Player player = event.getPlayer();

		if (players.remove(region, player)) {
			memberLeftRegion(player, region);

			if (players.isEmpty(player.getWorld(), region)) {
				setInactiveFlagsOnRegion(region);
			}
		} else {
			playerLeftRegion(player, region);
		}
	}
}
