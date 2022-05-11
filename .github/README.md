# BrickScheduler

A Minecraft library for scheduling things.

## Platforms

* [x] Minestom
* [x] Spigot / Paper

## Usage
### Gradle

```
repositories {
    maven { url "https://repo.jorisg.com/snapshots" }
}
```

```
dependencies {
    // minestom
    implementation 'com.guflimc.brick.scheduler:minestom-api:1.0-SNAPSHOT'
    
    // spigot
    implementation 'com.guflimc.brick.scheduler:spigot-api:1.0-SNAPSHOT'
}
```

### Javadoc

You can find the javadocs for all platforms [here](https://guflimc.github.io/BrickScheduler)


### Examples
```java
Scheduler scheduler = new SpigotScheduler("name", plugin);

// async
scheduler.asyncLater(() -> {
    System.out.println("poggers");
}, 1, ChronoUnit.SECONDS);

// sync
scheduler.syncLater(() -> {
    System.out.println("poggers");
}, 1, ChronoUnit.SECONDS);
```
