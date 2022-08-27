## MobsManager - How it works ?

This section describes the behavior of the plugin.

### Default behaviour

The plugin prevent the spawn of entities that you have disabled. It's works per world.

> Import note : Existing entities are not deleted.

### Spawn Type

When you want to disable the spawning of a mob, you have several way.

+ **ALL** : Every kind of spawning is disabled
+ **NATURAL** : When something spawns from natural means
+ **CUSTOM** When an entity is missing a SpawnReason
+ **SPAWNER**  When a creature spawns from a spawner
+ **SPAWNER_EGG** When a creature spawns from an egg
+ **BREEDING** When an animal breeds to create a child
+ **BUILD_IRONGOLEM** When an iron golem is spawned by being built

> BUILD_IRONGOLEM is a spawn reason that have sense only for the Iron Golem entity but this is possible for any plugin to spawn another entity when a IRON GOLEM is build. This is why this spawn reason can also be disabled for other entities.

### Next step
See [configuration part](https://apavarino.github.io/MobsManager/configuration) or go to [home page](https://apavarino.github.io/MobsManager)
