# BrickScheduler

An extension for [Minestom](https://github.com/Minestom/Minestom) with more scheduling options.

## API

### Maven setup
```
repositories {
    maven { url "https://repo.jorisg.com/snapshots" }
}
```

```
dependencies {
    implementation 'org.minestombrick.scehduler:api:1.0-SNAPSHOT'
}
```

### Usage

Check the [javadocs](https://minestombrick.github.io/BrickScheduler/)

#### Examples
```java
// async
SchedulerAPI.get().asyncLater(() -> {
    System.out.println("poggers");
}, 1, ChronoUnit.SECONDS);

// sync
SchedulerAPI.get().syncLater(() -> {
    System.out.println("poggers");
}, 1, ChronoUnit.SECONDS);
```
