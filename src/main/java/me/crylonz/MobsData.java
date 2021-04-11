package me.crylonz;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("MobsData")
public class MobsData implements ConfigurationSerializable {

    private String type;
    private boolean allSpawn;
    private boolean naturalSpawn;
    private boolean customSpawn;
    private boolean spawnerSpawn;
    private boolean eggSpawn;
    private boolean breedingSpawn;

    public MobsData(String name,boolean allSpawn, boolean naturalSpawn, boolean customSpawn, boolean spawnerSpawn,
                    boolean eggSpawn, boolean breedingSpawn ) {
        this.type = name;
        this.allSpawn = allSpawn;
        this.naturalSpawn = naturalSpawn;
        this.customSpawn = customSpawn;
        this.spawnerSpawn = spawnerSpawn;
        this.eggSpawn = eggSpawn;
        this.breedingSpawn = breedingSpawn;
    }

    public String getName() {
        return this.type;
    }

    public void setName(String name) {
        this.type = name;
    }

    public void setCustomSpawn(boolean customSpawn) {
        this.customSpawn = customSpawn;
    }

    public boolean getCustomSpawn() {
        return this.customSpawn;
    }

    public void setNaturalSpawn(boolean naturalSpawn) {
        this.naturalSpawn = naturalSpawn;
    }

    public boolean getNaturalSpawn() {
        return this.naturalSpawn;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("Name", this.type);
        map.put("AllSpawn", this.allSpawn);
        map.put("CustomSpawn", this.customSpawn);
        map.put("NaturalSpawn", this.naturalSpawn);
        map.put("SpawnerSpawn", this.spawnerSpawn);
        map.put("EggSpawn", this.eggSpawn);
        map.put("BreedingSpawn", this.eggSpawn);

        return map;
    }

    public static MobsData deserialize(Map<String, Object> map) {
        return new MobsData(
                (String) map.get("Name"),
                (boolean) map.get("AllSpawn"),
                (boolean) map.get("NaturalSpawn"),
                (boolean) map.get("CustomSpawn"),
                (boolean) map.get("SpawnerSpawn"),
                (boolean) map.get("EggSpawn"),
                (boolean) map.get("BreedingSpawn")
        );
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

    public boolean isCustomSpawn() {
        return customSpawn;
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

}
