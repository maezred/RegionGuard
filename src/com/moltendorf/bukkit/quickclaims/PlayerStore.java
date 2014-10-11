package com.moltendorf.bukkit.quickclaims;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by moltendorf on 14/10/11.
 */
public class PlayerStore {
	final protected Map<UUID, Map<String, Set<UUID>>> players = new LinkedHashMap<>();

	public boolean put(ProtectedRegion region, Player player) {
		return put(player.getWorld(), region, player);
	}

	public boolean put(World world, ProtectedRegion region, Player player) {
		UUID worldID = world.getUID();

		Map<String, Set<UUID>> worldPlayers = players.get(worldID);

		if (worldPlayers == null) {
			worldPlayers = new LinkedHashMap<>();

			players.put(worldID, worldPlayers);
		}

		String regionID = region.getId();

		Set<UUID> regionPlayers = worldPlayers.get(regionID);

		if (regionPlayers == null) {
			regionPlayers = new LinkedHashSet<>();

			worldPlayers.put(regionID, regionPlayers);
		}

		return regionPlayers.add(player.getUniqueId());
	}

	public boolean remove(ProtectedRegion region, Player player) {
		return remove(player.getWorld(), region, player);
	}

	public boolean remove(World world, ProtectedRegion region, Player player) {
		Map<String, Set<UUID>> worldPlayers = players.get(world.getUID());

		if (worldPlayers == null) {
			return false;
		}

		Set<UUID> regionPlayers = worldPlayers.get(region.getId());

		if (regionPlayers == null) {
			return false;
		}

		return regionPlayers.remove(player.getUniqueId());
	}

	public int size(World world, ProtectedRegion region) {
		Map<String, Set<UUID>> worldPlayers = players.get(world.getUID());

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
		Map<String, Set<UUID>> worldPlayers = players.get(world.getUID());

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
		Map<String, Set<UUID>> worldPlayers = players.get(world.getUID());

		if (worldPlayers == null) {
			return;
		}

		worldPlayers.remove(region.getId());
	}

	public void clear(World world) {
		players.remove(world.getUID());
	}

	public void clear() {
		players.clear();
	}
}
