package com.moltendorf.bukkit.quickclaims;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by moltendorf on 14/10/11.
 */
public class PlayerStore {
	final protected Plugin plugin;

	final protected Map<UUID, Map<String, Set<UUID>>> serverPlayers = new LinkedHashMap<>();
	final protected Map<UUID, PlayerData> playerRegions = new LinkedHashMap<>();

	public PlayerStore(Plugin instance) {
		plugin = instance;
	}

	public boolean put(ProtectedRegion region, Player player) {
		return put(player.getWorld(), region, player);
	}

	public boolean put(World world, ProtectedRegion region, Player player) {
		UUID worldID = world.getUID();

		Map<String, Set<UUID>> worldPlayers = serverPlayers.get(worldID);

		if (worldPlayers == null) {
			worldPlayers = new LinkedHashMap<>();

			serverPlayers.put(worldID, worldPlayers);
		}

		String regionID = region.getId();

		Set<UUID> regionPlayers = worldPlayers.get(regionID);

		if (regionPlayers == null) {
			regionPlayers = new LinkedHashSet<>();

			worldPlayers.put(regionID, regionPlayers);
		}

		UUID playerID = player.getUniqueId();

		PlayerData playerData = playerRegions.get(playerID);

		if (playerData == null) {
			playerData = new PlayerData(plugin, player);

			playerRegions.put(playerID, playerData);
		}

		playerData.add(worldID, regionID);

		return regionPlayers.add(playerID);
	}

	public boolean remove(ProtectedRegion region, Player player) {
		return remove(player.getWorld(), region, player);
	}

	public boolean remove(World world, ProtectedRegion region, Player player) {
		UUID worldID = world.getUID();

		Map<String, Set<UUID>> worldPlayers = serverPlayers.get(worldID);

		if (worldPlayers == null) {
			return false;
		}

		String regionID = region.getId();

		Set<UUID> regionPlayers = worldPlayers.get(regionID);

		if (regionPlayers == null) {
			return false;
		}

		UUID playerID = player.getUniqueId();

		PlayerData playerData = playerRegions.get(playerID);

		if (playerData == null) {
			playerData = new PlayerData(plugin, player);

			playerRegions.put(playerID, playerData);
		}

		playerData.remove(worldID, regionID);

		return regionPlayers.remove(playerID);
	}

	public int size(World world, ProtectedRegion region) {
		Map<String, Set<UUID>> worldPlayers = serverPlayers.get(world.getUID());

		if (worldPlayers == null) {
			return 0;
		}

		Set<UUID> regionPlayers = worldPlayers.get(region.getId());

		if (regionPlayers == null) {
			return 0;
		}

		return regionPlayers.size();
	}

	public boolean isEmpty(World world, ProtectedRegion region) {
		Map<String, Set<UUID>> worldPlayers = serverPlayers.get(world.getUID());

		if (worldPlayers == null) {
			return true;
		}

		Set<UUID> regionPlayers = worldPlayers.get(region.getId());

		if (regionPlayers == null) {
			return true;
		}

		return regionPlayers.isEmpty();
	}

	public void clear(World world, ProtectedRegion region) {
		Map<String, Set<UUID>> worldPlayers = serverPlayers.get(world.getUID());

		if (worldPlayers == null) {
			return;
		}

		worldPlayers.remove(region.getId());
	}

	public void clear(World world) {
		serverPlayers.remove(world.getUID());
	}

	public void clear() {
		serverPlayers.clear();
	}
}
