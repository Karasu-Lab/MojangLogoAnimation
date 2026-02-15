# MojangLogoAnimation

A Fabric mod that replaces the static Mojang Studios logo on the splash screen with a smooth frame-by-frame animation.

## Dependencies

| Dependency | Required | Link |
|---|---|---|
| **Fabric API** | ✅ | [Modrinth](https://modrinth.com/mod/fabric-api) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api) |
| **Cloth Config** | ✅ | [Modrinth](https://modrinth.com/mod/cloth-config) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/cloth-config) |
| **Mod Menu** | ❌ (optional) | [Modrinth](https://modrinth.com/mod/modmenu) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/modmenu) |

## Architecture

This mod uses a **data-driven animation system** built around a clean separation of concerns:

- **`ISplashScreenAnimationData`** — Interface defining animation metadata (frame count, timing, sound, textures). New animation types can be added by implementing this interface.
- **`FolderAnimationData`** — A `record` implementation that resolves textures from `textures/gui/title/{id}/` and sound events via the `gui.title.{id}` naming convention.
- **`AnimationPlayer`** — Encapsulates all animation state (frame progression, sound playback, lifecycle). Runs animation on a daemon thread with thread-safe access via `AtomicInteger`/`AtomicBoolean`.
- **`SplashLoader`** — Registry for animation data. Animations are pre-registered during client initialization since the splash screen renders *during* resource loading.
- **`SplashOverlayMixin`** — A thin Mixin layer that delegates all logic to `AnimationPlayer`. The Mixin itself holds no animation knowledge.

Adding a new animation requires only:
1. Place frame textures in `textures/gui/title/{id}/`
2. Place sound in `sounds/gui/title/{id}.ogg`
3. Register with `splashLoader.register(new FolderAnimationData("id", frameCount))`

## Preview

![MojangStudioMode](https://raw.githubusercontent.com/Hashibutogarasu/MojangLogoAnimation-Assets/main/mojang_studios.gif)
![3dSharewareMode](https://raw.githubusercontent.com/Hashibutogarasu/MojangLogoAnimation-Assets/main/april_fool.gif)
