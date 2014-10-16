package com.moltendorf.bukkit.quickclaims;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by moltendorf on 14/10/16.
 */
public class PlayerData {
	final protected Plugin plugin;
	final public Player player;

	public UUID currentWorldID = null;
	public Set<String> regionIDs = new LinkedHashSet<>();

	public PlayerData(final Plugin instance, final Player newPlayer) {
		plugin = instance;
		player = newPlayer;
	}

	public boolean add(final UUID worldID, final String regionID) {
		if (currentWorldID != worldID) {
			clear();

			currentWorldID = worldID;
		}

		return regionIDs.add(regionID);
	}

	public boolean remove(final UUID worldID, final String regionID) {
		if (currentWorldID == worldID) {
			return regionIDs.remove(regionID);
		}

		return false;
	}

	public void clear() {
		if (currentWorldID != null) {
			final World world = plugin.getServer().getWorld(currentWorldID);

			if (world != null) {
				final RegionManager manager = WGBukkit.getRegionManager(world);

				for (final String regionID : regionIDs) {
					final ProtectedRegion region = manager.getRegion(regionID);

					plugin.listeners.unflagPlayerForRegion(player, region, world);
				}
			}

			currentWorldID = null;
			regionIDs.clear();
		}
	}
}
