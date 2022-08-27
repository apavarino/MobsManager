package me.crylonz;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SerializableAs("MobsData")
public class MobsData implements ConfigurationSerializable {

    private String type;
    private String worldName;
    private boolean allSpawn;
    private boolean naturalSpawn;
    private boolean customSpawn;
    private boolean spawnerSpawn;
    private boolean eggSpawn;
    private boolean breedingSpawn;
    private boolean ironGolemSpawn;

    public MobsData(String name, String worldName, boolean allSpawn, boolean naturalSpawn, boolean customSpawn, boolean spawnerSpawn,
                    boolean eggSpawn, boolean breedingSpawn, boolean ironGolemSpawn) {
        this.type = name;
        this.worldName = worldName;
        this.allSpawn = allSpawn;
        this.naturalSpawn = naturalSpawn;
        this.customSpawn = customSpawn;
        this.spawnerSpawn = spawnerSpawn;
        this.eggSpawn = eggSpawn;
        this.breedingSpawn = breedingSpawn;
        this.ironGolemSpawn = ironGolemSpawn;
    }

    public static MobsData deserialize(Map<String, Object> map) {
        return new MobsData(
                (String) map.get("Name"),
                (String) map.get("WorldName"),
                (boolean) map.get("AllSpawn"),
                (boolean) map.get("NaturalSpawn"),
                (boolean) map.get("CustomSpawn"),
                (boolean) map.get("SpawnerSpawn"),
                (boolean) map.get("EggSpawn"),
                (boolean) map.get("BreedingSpawn"),
                (boolean) map.get("IronGolemSpawn")
        );
    }

    public String getName() {
        return this.type;
    }

    public void setName(String name) {
        this.type = name;
    }

    public boolean getCustomSpawn() {
        return this.customSpawn;
    }

    public boolean getNaturalSpawn() {
        return this.naturalSpawn;
    }

    public String getWorldName() {
        return worldName;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("Name", this.type);
        map.put("WorldName", this.worldName);
        map.put("AllSpawn", this.allSpawn);
        map.put("CustomSpawn", this.customSpawn);
        map.put("NaturalSpawn", this.naturalSpawn);
        map.put("SpawnerSpawn", this.spawnerSpawn);
        map.put("EggSpawn", this.eggSpawn);
        map.put("BreedingSpawn", this.breedingSpawn);
        map.put("IronGolemSpawn", this.ironGolemSpawn);
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobsData mobsData = (MobsData) o;
        return Objects.equals(type, mobsData.type) && Objects.equals(worldName, mobsData.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, worldName);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAllSpawn() {
        return allSpawn;
    }

    public void setAllSpawn(boolean allSpawn) {
        this.allSpawn = allSpawn;
    }

    public boolean isNaturalSpawn() {
        return naturalSpawn;
    }

    public void setNaturalSpawn(boolean naturalSpawn) {
        this.naturalSpawn = naturalSpawn;
    }

    public boolean isCustomSpawn() {
        return customSpawn;
    }

    public void setCustomSpawn(boolean customSpawn) {
        this.customSpawn = customSpawn;
    }

    public boolean isSpawnerSpawn() {
        return spawnerSpawn;
    }

    public void setSpawnerSpawn(boolean spawnerSpawn) {
        this.spawnerSpawn = spawnerSpawn;
    }

    public boolean isEggSpawn() {
        return eggSpawn;
    }

    public void setEggSpawn(boolean eggSpawn) {
        this.eggSpawn = eggSpawn;
    }

    public boolean isBreedingSpawn() {
        return breedingSpawn;
    }

    public void setBreedingSpawn(boolean breedingSpawn) {
        this.breedingSpawn = breedingSpawn;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public boolean isIronGolemSpawn() {
        return ironGolemSpawn;
    }

    public void setIronGolemSpawn(boolean ironGolemSpawn) {
        this.ironGolemSpawn = ironGolemSpawn;
    }
}
