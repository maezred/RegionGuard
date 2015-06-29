package net.moltendorf.Bukkit.RegionGuard;

import com.mewin.WorldGuardRegionEvents.events.RegionEnteredEvent;
import com.mewin.WorldGuardRegionEvents.events.RegionLeftEvent;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Listener register.
 *
 * @author moltendorf
 */
public class Listeners implements Listener {

	final protected RegionGuard plugin;

	final protected ConsoleCommandSender console;

	final protected PlayerStore players;
	final protected Set<String> regions = new HashSet<>();
	private final   Pattern     pattern = Pattern.compile("__path-([nesw]{1,2})([0-9]+)([jp])");

	protected Listeners(final RegionGuard instance) {
		plugin = instance;

		console = plugin.getServer().getConsoleSender();

		players = new PlayerStore(plugin);

		// Protect all regions.
		for (World world : plugin.getServer().getWorlds()) {
			RegionManager manager = WGBukkit.getRegionManager(world);

			for (Map.Entry<String, ProtectedRegion> entry : manager.getRegions().entrySet()) {
				ProtectedRegion region = entry.getValue();

				setInactiveFlagsOnRegion(region, world);
			}
		}

		// Disable extensive protections on regions with members in them.
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			Location location = player.getLocation();
			World world = location.getWorld();
			RegionManager manager = WGBukkit.getRegionManager(world);

			for (ProtectedRegion region : manager.getApplicableRegions(location)) {
				flagPlayerForRegion(player, region, world);
			}
		}
	}

	protected String direction(final char cardinal) {
		switch (cardinal) {
			case 'n':
				return "North";

			case 'e':
				return "East";

			case 's':
				return "South";

			case 'w':
				return "West";
		}

		return null;
	}

	protected void playerEnteredRegion(Player player, ProtectedRegion region, World world) {
		console.sendMessage("§fPlayer " + player.getDisplayName() + " entered region " + world.getName() + ":" + region.getId() + ".");
	}

	protected void memberEnteredRegion(Player player, ProtectedRegion region, World world) {
		console.sendMessage("§bMember " + player.getDisplayName() + " entered region " + world.getName() + ":" + region.getId() + ".");
	}

	protected void playerLeftRegion(Player player, ProtectedRegion region, World world) {
		console.sendMessage("§7Player " + player.getDisplayName() + " left region " + world.getName() + ":" + region.getId() + ".");
	}

	protected void memberLeftRegion(Player player, ProtectedRegion region, World world) {
		console.sendMessage("§3Member " + player.getDisplayName() + " left region " + world.getName() + ":" + region.getId() + ".");
	}

	protected void setActiveFlagsOnRegion(ProtectedRegion region, World world) {
		if (region.getId().equals("__global__")) {
			return;
		}

		final CommandSender    sender = plugin.getServer().getConsoleSender();
		final WorldGuardPlugin wg     = WGBukkit.getPlugin();

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

		console.sendMessage("§4Disabled protection on region " + world.getName() + ":" + region.getId() + ".");
	}

	protected void setInactiveFlagsOnRegion(ProtectedRegion region, World world) {
		if (region.getId().equals("__global__")) {
			return;
		}

		final CommandSender    sender = plugin.getServer().getConsoleSender();
		final WorldGuardPlugin wg     = WGBukkit.getPlugin();

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

		console.sendMessage("§2Enabled protection on region " + world.getName() + ":" + region.getId() + ".");
	}

	protected boolean isPrivateRegion(ProtectedRegion region) {
		do {
			if (region.getOwners().size() > 0) {
				return true;
			}

			region = region.getParent();
		} while (region != null);

		return false;
	}

	protected boolean isMemberOfRegion(Player player, ProtectedRegion region) {
		UUID playerID = player.getUniqueId();

		do {
			if (region.getMembers().contains(playerID)) {
				return true;
			}

			if (region.getOwners().contains(playerID)) {
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
		boolean first = !regions.contains(world.getName() + ":" + region.getId());

		regions.add(world.getName() + ":" + region.getId()); // Only update greeting once per server restart.

		if (isPrivateRegion(region)) {
			// Convert names to UUIDs.
			if (first) {
				DefaultDomain owners = region.getOwners();

				for (String name : owners.getPlayers()) {
					OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(name);

					owners.removePlayer(name);
					owners.addPlayer(offlinePlayer.getUniqueId());
				}

				region.setOwners(owners);

				DefaultDomain members = region.getMembers();

				for (String name : members.getPlayers()) {
					OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(name);

					members.removePlayer(name);
					members.addPlayer(offlinePlayer.getUniqueId());
				}

				region.setMembers(members);
			}

			String message = region.getFlag(DefaultFlag.GREET_MESSAGE);

			if (message == null || (first && message.startsWith("&r"))) {
				List<String> names = new ArrayList<>();

				for (UUID playerID : region.getOwners().getUniqueIds()) {
					OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(playerID);

					if (offlinePlayer.getFirstPlayed() != 0) {
						names.add(offlinePlayer.getName());
					}
				}

				Collections.sort(names, String.CASE_INSENSITIVE_ORDER);

				message = "&r&8[&7";

				int count = names.size();
				int current = 0;

				for (String name : names) {
					if (current++ == 0) {
						message += " " + name;
					} else if (count == current) {
						message += " & " + name;
					} else {
						message += ", " + name;
					}
				}

				message += "'s ";

				if (world.getName().equalsIgnoreCase("world_nether")) {
					message += "Portal";
				} else {
					message += "Home";
				}

				message += " &8]";

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
					setActiveFlagsOnRegion(region, world);
				}

				players.put(world, region, player);

				memberEnteredRegion(player, region, world);
			} else {
				UUID playerID = player.getUniqueId();

				PlayerData playerData = players.playerRegions.get(playerID);

				if (playerData == null) {
					playerData = new PlayerData(plugin, player);

					players.playerRegions.put(playerID, playerData);
				}

				playerData.add(world.getUID(), region.getId());

				playerEnteredRegion(player, region, world);
			}
		} else {
			String message = region.getFlag(DefaultFlag.GREET_MESSAGE);
			String id = region.getId();

		check:
			if (first && id.startsWith("__")) {
				CommandSender sender = plugin.getServer().getConsoleSender();
				WorldGuardPlugin wg = WGBukkit.getPlugin();

				/*
				 * Check type of public region.
				 */

				final Matcher matcher = pattern.matcher(id);

				if (matcher.matches()) {

				/*
				 * Update greeting.
				 */

				message:
					if (message == null || message.startsWith("&r")) {
						message = "&r&8[&b ";

						final String cardinals = matcher.group(1);

						message += direction(cardinals.charAt(0));

						final boolean path = matcher.group(3).equals("p");

						if (!path && !matcher.group(3).equals("j")) {
							break message;
						}

						if (cardinals.length() == 2) {
							if (path) {
								message += "ern " + direction(cardinals.charAt(1));
							} else {
								message += " " + direction(cardinals.charAt(1));
							}

							message += " at " + matcher.group(2);
						} else {
							if (path) {
								message += " to " + matcher.group(2);
							} else {
								message += " at " + matcher.group(2);
							}
						}

						if (path) {
							message += " Path";
						} else {
							message += " Junction";
						}

						message += " &8]";

						try {
							region.setFlag(DefaultFlag.GREET_MESSAGE, DefaultFlag.GREET_MESSAGE.parseInput(wg, sender, message));
						} catch (InvalidFlagFormat invalidFlagFormat) {
							invalidFlagFormat.printStackTrace();
						}
					}
				} else {
					break check;
				}

				// Tweak priority.
				region.setPriority(100);

				// Make sure administrators and moderators are members.
				DefaultDomain members = region.getMembers();

				members.addGroup("su");
				members.addGroup("moderator");

				region.setMembers(members);

				/*
				 * Make sure people don't starve together.
				 */

				try {
					final Integer flag = region.getFlag(DefaultFlag.FEED_AMOUNT);

					if (flag == null || flag != 1) {
						region.setFlag(DefaultFlag.FEED_AMOUNT, DefaultFlag.FEED_AMOUNT.parseInput(wg, sender, "1"));
					}
				} catch (InvalidFlagFormat invalidFlagFormat) {
					invalidFlagFormat.printStackTrace();
				}

				try {
					final Integer flag = region.getFlag(DefaultFlag.FEED_DELAY);

					if (flag == null || flag != 1) {
						region.setFlag(DefaultFlag.FEED_DELAY, DefaultFlag.FEED_DELAY.parseInput(wg, sender, "1"));
					}
				} catch (InvalidFlagFormat invalidFlagFormat) {
					invalidFlagFormat.printStackTrace();
				}

				try {
					final Integer flag = region.getFlag(DefaultFlag.MAX_FOOD);

					if (flag == null || flag != 8) {
						region.setFlag(DefaultFlag.MAX_FOOD, DefaultFlag.MAX_FOOD.parseInput(wg, sender, "8"));
					}
				} catch (InvalidFlagFormat invalidFlagFormat) {
					invalidFlagFormat.printStackTrace();
				}

				/*
				 * Protect the path from everything.
				 */

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
			}

			UUID playerID = player.getUniqueId();

			PlayerData playerData = players.playerRegions.get(playerID);

			if (playerData == null) {
				playerData = new PlayerData(plugin, player);

				players.playerRegions.put(playerID, playerData);
			}

			playerData.add(world.getUID(), region.getId());

			playerEnteredRegion(player, region, world);
		}
	}

	protected void unflagPlayerForRegion(final Player player, final ProtectedRegion region) {
		unflagPlayerForRegion(player, region, player.getWorld());
	}

	protected void unflagPlayerForRegion(final Player player, final ProtectedRegion region, final World world) {
		if (players.remove(world, region, player)) {
			memberLeftRegion(player, region, world);

			if (players.isEmpty(world, region)) {
				setInactiveFlagsOnRegion(region, world);
			}
		} else {
			playerLeftRegion(player, region, world);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void RegionEnteredEventMonitor(final RegionEnteredEvent event) {
		Player          player = event.getPlayer();
		ProtectedRegion region = event.getRegion();

		flagPlayerForRegion(player, region);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void RegionLeaveEventMonitor(final RegionLeftEvent event) {
		Player          player = event.getPlayer();
		ProtectedRegion region = event.getRegion();

		unflagPlayerForRegion(player, region);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void PlayerChangedWorldEventMonitor(final PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();

		final UUID playerID = player.getUniqueId();
		final UUID worldID  = player.getWorld().getUID();

		final PlayerData playerData = players.playerRegions.get(playerID);

		if (playerData == null || playerData.currentWorldID == worldID) {
			return;
		}

		playerData.clear();
		playerData.currentWorldID = worldID;
	}
}
