package me.crylonz;

import me.crylonz.commands.MMCommandExecutor;
import me.crylonz.commands.MMTabCompletion;
import me.crylonz.utils.MobsManagerConfig;
import me.crylonz.utils.MobsManagerUpdater;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class MobsManager extends JavaPlugin implements Listener {

    public final static Logger log = Logger.getLogger("Minecraft");
    public static ArrayList<MobsData> mobsData = new ArrayList<>();
    public MobsManagerConfig config = new MobsManagerConfig(this);

    public static boolean worldGuardDetection;
    public static FileManager fileManager;

    static {
        ConfigurationSerialization.registerClass(MobsData.class, "MobsData");
    }


    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        fileManager = new FileManager(this);

        Metrics metrics = new Metrics(this, 15773);

        Bukkit.getWorlds().forEach(world -> {
            for (EntityType entity : EntityType.values()) {
                if (isUsefulEntity(entity))
                    mobsData.add(new MobsData(entity.name(), world.getName(), true, true, true, true, true, true, true));
            }
        });

        //Updater from 4.X to 5.X
        if (!fileManager.getMobsDataFile().exists() && fileManager.getConfigFile().exists()) {
            fileManager.getConfigFile().renameTo(fileManager.getMobsDataFile());
            log.warning("[MobsManager] Old configuration detected ( < 5.0.0 )");
            log.warning("[MobsManager] Applying configuration migration...");
        }

        registerConfig();

        if (!fileManager.getMobsDataFile().exists()) {
            getConfig().options().header("PLEASE DON'T EDIT THIS FILE");
        } else {
            // Merging maybe new world configuration with existing one
            ArrayList<MobsData> tmp = (ArrayList<MobsData>) fileManager.getMobsDataConfig().get("mobs");
            mobsData.removeAll(tmp);
            mobsData.addAll(tmp);
        }

        fileManager.getMobsDataConfig().set("mobs", mobsData);
        fileManager.saveMobsDataConfig();

        if (!fileManager.getConfigFile().exists()) {
            saveDefaultConfig();
        } else {
            config.updateConfig();
        }

        if (config.getBoolean("auto-update")) {
            MobsManagerUpdater updater = new MobsManagerUpdater(this, 322365, this.getFile(), MobsManagerUpdater.UpdateType.DEFAULT, true);
        }

        worldGuardDetection = config.getBoolean("world-guard-detection");

        Objects.requireNonNull(this.getCommand("mobsmanager"), "Command mobsmanager not found")
                .setExecutor(new MMCommandExecutor(this));
        Objects.requireNonNull(getCommand("mobsmanager")).setTabCompleter(new MMTabCompletion());
    }


    public void registerConfig() {
        config.register("auto-update", true);
        config.register("world-guard-detection", false);
    }

    public void onDisable() {
        log.info("[MobsManager] is disabled !");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        if (e == null)
            return;

        // Do nothing if WorldGuard spawn flag exists to prevent spawn issues
        if (worldGuardCheck(e)) return;

        if (mobsData != null) {
            Optional<Boolean> isCancelled = mobsData
                    .stream()
                    .filter(mobsData -> mobsData.getWorldName().equalsIgnoreCase(e.getEntity().getWorld().getName()))
                    .filter(mobsData -> mobsData.getName().equalsIgnoreCase(e.getEntityType().name()))
                    .findFirst()
                    .map(mobData -> {
                        if (!mobData.isAllSpawn()) {
                            return true;
                        } else {
                            switch (e.getSpawnReason()) {
                                case NATURAL:
                                case DEFAULT:
                                    return !mobData.isNaturalSpawn();
                                case CUSTOM:
                                    return !mobData.isCustomSpawn();
                                case SPAWNER:
                                    return !mobData.isSpawnerSpawn();
                                case SPAWNER_EGG:
                                    return !mobData.isEggSpawn();
                                case BREEDING:
                                    return !mobData.isBreedingSpawn();
                                case BUILD_IRONGOLEM:
                                    return !mobData.isIronGolemSpawn();
                                default:
                                    return false;
                            }
                        }
                    });
            e.setCancelled(isCancelled.orElse(false));
        }
    }

    private static boolean worldGuardCheck(CreatureSpawnEvent e) {
        if (worldGuardDetection) {
            try {
                return WorldGuarderChecker.check(e);
            } catch (NoClassDefFoundError exception) {
                return false;
            }
        }
        return false;
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent e) {
        if (e.getChunk().isLoaded()) {
            if (mobsData != null && mobsData.size() > 0) {
                Arrays.stream(e.getChunk().getEntities())
                        .forEach(entity -> {
                            mobsData
                                    .stream()
                                    .filter(mobData -> mobData.getName().equalsIgnoreCase(entity.getName()))
                                    .filter(mobData -> mobData.getWorldName().equalsIgnoreCase(entity.getWorld().getName()))
                                    .filter(mobsData -> mobsData.getWorldName().equalsIgnoreCase(entity.getWorld().getName()))
                                    .forEach(mobData -> {
                                        if (!mobData.isAllSpawn() || !mobData.isNaturalSpawn()) {
                                            entity.remove();
                                        }
                                    });
                        });

                for (Entity entity : e.getChunk().getEntities()) {
                    for (MobsData mobData : mobsData) {
                        if (entity.getName().equalsIgnoreCase(mobData.getName())) {
                            if (!mobData.isAllSpawn() || !mobData.isNaturalSpawn()) {
                                entity.remove();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public enum MMSpawnType {
        ALL,
        CUSTOM,
        NATURAL,
        SPAWNER,
        EGG,
        BREEDING,
        IRON_GOLEM
    }

    public static boolean asMyEnum(String str) {
        for (EntityType me : EntityType.values()) {
            if (me.name().equalsIgnoreCase(str))
                return true;
        }
        return false;
    }

    public static boolean isUsefulEntity(EntityType e) {

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
}
