package me.crylonz;

import me.crylonz.commands.MMCommandExecutor;
import me.crylonz.commands.MMTabCompletion;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

public class MobsManager extends JavaPlugin implements Listener {

    public final static Logger log = Logger.getLogger("Minecraft");
    public static ArrayList<MobsData> enableList = new ArrayList<>();

    static {
        ConfigurationSerialization.registerClass(MobsData.class, "MobsData");
    }

    public static boolean asMyEnum(String str) {
        for (EntityType me : EntityType.values()) {
            if (me.name().equalsIgnoreCase(str))
                return true;
        }
        return false;
    }

    public static boolean isUsefullEntity(EntityType e) {

        return e != EntityType.DROPPED_ITEM &&
                e != EntityType.EXPERIENCE_ORB &&
                e != EntityType.AREA_EFFECT_CLOUD &&
                e != EntityType.EGG &&
                e != EntityType.LEASH_HITCH &&
                e != EntityType.PAINTING &&
                e != EntityType.ARROW &&
                e != EntityType.SNOWBALL &&
                e != EntityType.FIREBALL &&
                e != EntityType.SMALL_FIREBALL &&
                e != EntityType.ENDER_PEARL &&
                e != EntityType.ENDER_SIGNAL &&
                e != EntityType.SPLASH_POTION &&
                e != EntityType.THROWN_EXP_BOTTLE &&
                e != EntityType.ITEM_FRAME &&
                e != EntityType.WITHER_SKULL &&
                e != EntityType.PRIMED_TNT &&
                e != EntityType.FALLING_BLOCK &&
                e != EntityType.FIREWORK &&
                e != EntityType.SPECTRAL_ARROW &&
                e != EntityType.SHULKER_BULLET &&
                e != EntityType.DRAGON_FIREBALL &&
                e != EntityType.ARMOR_STAND &&
                e != EntityType.EVOKER_FANGS &&
                e != EntityType.MINECART_COMMAND &&
                e != EntityType.ILLUSIONER &&
                e != EntityType.BOAT &&
                e != EntityType.MINECART &&
                e != EntityType.MINECART_CHEST &&
                e != EntityType.MINECART_FURNACE &&
                e != EntityType.MINECART_TNT &&
                e != EntityType.MINECART_HOPPER &&
                e != EntityType.MINECART_MOB_SPAWNER &&
                e != EntityType.LLAMA_SPIT &&
                e != EntityType.ENDER_CRYSTAL &&
                asMyEnum("TRIDENT") && e != EntityType.TRIDENT &&
                e != EntityType.FISHING_HOOK &&
                e != EntityType.LIGHTNING &&
                e != EntityType.PLAYER &&
                e != EntityType.UNKNOWN;
    }

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        for (EntityType entity : EntityType.values()) {
            if (isUsefullEntity(entity))
                enableList.add(new MobsData(entity.name(), true, true, true, true, true, true));
        }

        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            getConfig().options().header("PLEASE RELOAD AFTER ANY CHANGE");
            getConfig().set("mobs", enableList);
            saveConfig();

        } else {
            enableList = (ArrayList<MobsData>) getConfig().get("mobs");
        }

        Objects.requireNonNull(this.getCommand("mm"), "Command mm not found")
                .setExecutor(new MMCommandExecutor(this));
        Objects.requireNonNull(getCommand("mm")).setTabCompleter(new MMTabCompletion());
    }

    public void onDisable() {
        log.info("[MobsManager] is disabled !");
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        if (e == null)
            return;

        for (MobsData mobData : enableList) {
            if (e.getEntityType().name().equalsIgnoreCase(mobData.getName())) {

                if (!mobData.isAllSpawn()) {
                    e.setCancelled(true);

                } else {
                    switch (e.getSpawnReason()) {
                        case NATURAL:
                            e.setCancelled(!mobData.isNaturalSpawn());
                            break;
                        case CUSTOM:
                            e.setCancelled(!mobData.isCustomSpawn());
                            break;
                        case SPAWNER:
                            e.setCancelled(!mobData.isSpawnerSpawn());
                            break;
                        case SPAWNER_EGG:
                            e.setCancelled(!mobData.isEggSpawn());
                            break;
                        case BREEDING:
                            e.setCancelled(!mobData.isBreedingSpawn());
                            break;
                    }
                }
                break;
            }
        }
    }

    public enum MMSpawnType {
        ALL,
        CUSTOM,
        NATURAL,
        SPAWNER,
        EGG,
        BREEDING
    }
}
