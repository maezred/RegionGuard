package com.moltendorf.bukkit.quickclaims;

import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import com.mewin.WGRegionEvents.events.RegionLeftEvent;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Listener register.
 *
 * @author moltendorf
 */
public class Listeners implements Listener {

	final protected Plugin plugin;

	final protected PlayerStore players;

	protected Listeners(final Plugin instance) {
		plugin = instance;

		players = new PlayerStore(plugin);

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			Location location = player.getLocation();
			World world = location.getWorld();
			RegionManager manager = WGBukkit.getRegionManager(world);

			for (ProtectedRegion region : manager.getApplicableRegions(location)) {
				if (isPrivateRegion(region) && isMemberOfRegion(player, region)) {
					players.put(world, region, player);

					memberEnteredRegion(player, region, world);
				} else {
					playerEnteredRegion(player, region, world);
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

	protected void playerEnteredRegion(Player player, ProtectedRegion region, World world) {
		System.out.println("Player " + player.getDisplayName() + " entered region " + region.getId() + " in world " + world.getName() + ".");
	}

	protected void memberEnteredRegion(Player player, ProtectedRegion region, World world) {
		System.out.println("Member " + player.getDisplayName() + " entered region " + region.getId() + " in world " + world.getName() + ".");
	}

	protected void playerLeftRegion(Player player, ProtectedRegion region, World world) {
		System.out.println("Player " + player.getDisplayName() + " left region " + region.getId() + " in world " + world.getName() + ".");
	}

	protected void memberLeftRegion(Player player, ProtectedRegion region, World world) {
		System.out.println("Member " + player.getDisplayName() + " left region " + region.getId() + " in world " + world.getName() + ".");
	}

	protected void setActiveFlagsOnRegion(ProtectedRegion region) {
		if (region.getId().equals("__global__")) {
			return;
		}

		final CommandSender sender = plugin.getServer().getConsoleSender();
		final WorldGuardPlugin wg = WGBukkit.getPlugin();

		try {
			final StateFlag.State flag = region.getFlag(DefaultFlag.CREEPER_EXPLOSION);

			if (flag == null || !flag.toString().equals("ALLOW")) {
				region.setFlag(DefaultFlag.CREEPER_EXPLOSION, DefaultFlag.CREEPER_EXPLOSION.parseInput(wg, sender, "ALLOW"));
			}
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			final StateFlag.State flag = region.getFlag(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE);

			if (flag == null || !flag.toString().equals("ALLOW")) {
				region.setFlag(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE, DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE.parseInput(wg, sender, "ALLOW"));
			}
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			final StateFlag.State flag = region.getFlag(DefaultFlag.ENDER_BUILD);

			if (flag == null || !flag.toString().equals("ALLOW")) {
				region.setFlag(DefaultFlag.ENDER_BUILD, DefaultFlag.ENDER_BUILD.parseInput(wg, sender, "ALLOW"));
			}
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			final StateFlag.State flag = region.getFlag(DefaultFlag.GHAST_FIREBALL);

			if (flag == null || !flag.toString().equals("ALLOW")) {
				region.setFlag(DefaultFlag.GHAST_FIREBALL, DefaultFlag.GHAST_FIREBALL.parseInput(wg, sender, "ALLOW"));
			}
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			final StateFlag.State flag = region.getFlag(DefaultFlag.OTHER_EXPLOSION);

			if (flag == null || !flag.toString().equals("ALLOW")) {
				region.setFlag(DefaultFlag.OTHER_EXPLOSION, DefaultFlag.OTHER_EXPLOSION.parseInput(wg, sender, "ALLOW"));
			}
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		System.out.println("Disabled protection on region " + region.getId() + ".");
	}

	protected void setInactiveFlagsOnRegion(ProtectedRegion region) {
		if (region.getId().equals("__global__")) {
			return;
		}

		final CommandSender sender = plugin.getServer().getConsoleSender();
		final WorldGuardPlugin wg = WGBukkit.getPlugin();

		try {
			final StateFlag.State flag = region.getFlag(DefaultFlag.CREEPER_EXPLOSION);

			if (flag == null || !flag.toString().equals("DENY")) {
				region.setFlag(DefaultFlag.CREEPER_EXPLOSION, DefaultFlag.CREEPER_EXPLOSION.parseInput(wg, sender, "DENY"));
			}
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			final StateFlag.State flag = region.getFlag(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE);

			if (flag == null || !flag.toString().equals("DENY")) {
				region.setFlag(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE, DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE.parseInput(wg, sender, "DENY"));
			}
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			final StateFlag.State flag = region.getFlag(DefaultFlag.ENDER_BUILD);

			if (flag == null || !flag.toString().equals("DENY")) {
				region.setFlag(DefaultFlag.ENDER_BUILD, DefaultFlag.ENDER_BUILD.parseInput(wg, sender, "DENY"));
			}
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			final StateFlag.State flag = region.getFlag(DefaultFlag.GHAST_FIREBALL);

			if (flag == null || !flag.toString().equals("DENY")) {
				region.setFlag(DefaultFlag.GHAST_FIREBALL, DefaultFlag.GHAST_FIREBALL.parseInput(wg, sender, "DENY"));
			}
		} catch (InvalidFlagFormat invalidFlagFormat) {
			invalidFlagFormat.printStackTrace();
		}

		try {
			final StateFlag.State flag = region.getFlag(DefaultFlag.OTHER_EXPLOSION);

			if (flag == null || !flag.toString().equals("DENY")) {
				region.setFlag(DefaultFlag.OTHER_EXPLOSION, DefaultFlag.OTHER_EXPLOSION.parseInput(wg, sender, "DENY"));
			}
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

	protected void flagPlayerForRegion(final Player player, final ProtectedRegion region) {
		flagPlayerForRegion(player, region, player.getWorld());
	}

	protected void flagPlayerForRegion(final Player player, final ProtectedRegion region, final World world) {
		if (isPrivateRegion(region)) {
			String message = region.getFlag(DefaultFlag.GREET_MESSAGE);

			if (message == null) {
				message = "&1[&C";

				int count = 0;

				for (String name : region.getOwners().getPlayers()) {
					OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(name);

					if (offlinePlayer.getFirstPlayed() != 0) {
						message += (count++ == 0 ? " " : " & ") + offlinePlayer.getName();
					}
				}

				message += "'s Home &1]";

				CommandSender sender = plugin.getServer().getConsoleSender();
				WorldGuardPlugin wg = WGBukkit.getPlugin();

				try {
					region.setFlag(DefaultFlag.GREET_MESSAGE, DefaultFlag.GREET_MESSAGE.parseInput(wg, sender, message));
				} catch (InvalidFlagFormat invalidFlagFormat) {
					invalidFlagFormat.printStackTrace();
				}
			}

			if (isMemberOfRegion(player, region)) {
				if (players.isEmpty(world, region)) {
					setActiveFlagsOnRegion(region);
				}

				players.put(region, player);

				memberEnteredRegion(player, region, world);
			} else {
				playerEnteredRegion(player, region, world);
			}
		} else {
			playerEnteredRegion(player, region, world);
		}
	}

	protected void unflagPlayerForRegion(final Player player, final ProtectedRegion region) {
		unflagPlayerForRegion(player, region, player.getWorld());
	}

	protected void unflagPlayerForRegion(final Player player, final ProtectedRegion region, final World world) {
		if (players.remove(region, player)) {
			memberLeftRegion(player, region, world);

			if (players.isEmpty(world, region)) {
				setInactiveFlagsOnRegion(region);
			}
		} else {
			playerLeftRegion(player, region, world);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void RegionEnteredEventMonitor(final RegionEnteredEvent event) {
		Player player = event.getPlayer();
		ProtectedRegion region = event.getRegion();

		flagPlayerForRegion(player, region);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void RegionLeaveEventMonitor(final RegionLeftEvent event) {
		Player player = event.getPlayer();
		ProtectedRegion region = event.getRegion();

		unflagPlayerForRegion(player, region);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void PlayerChangedWorldEventMonitor(final PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();

		final UUID playerID = player.getUniqueId();
		final UUID worldID = player.getWorld().getUID();

		final PlayerData playerData = players.playerRegions.get(playerID);

		if (playerData == null || playerData.currentWorldID == worldID) {
			return;
		}

		playerData.clear();
		playerData.currentWorldID = worldID;
	}
}
