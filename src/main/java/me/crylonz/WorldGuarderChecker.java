package me.crylonz;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class WorldGuarderChecker {

    public static boolean check(CreatureSpawnEvent e) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(e.getEntity().getLocation().getWorld()));

        if (regions != null) {
            BlockVector3 position = BlockVector3.at(e.getEntity().getLocation().getX(),
                    e.getEntity().getLocation().getY(), e.getEntity().getLocation().getZ());
            ApplicableRegionSet set = regions.getApplicableRegions(position);

            if (set.size() != 0) {

                if (!set.testState(null, Flags.MOB_SPAWNING) ||
                        !set.queryAllValues(null, Flags.DENY_SPAWN).isEmpty()
                ) {
                    return true;
                }
            }
        }
        return false;
    }

}