# Mobs Can Crouch And Crawl

Fabric mod for Minecraft 1.21.11.

A player hiding behind a low gap is no longer safe: zombies chasing a target crouch to get under
1.5-block gaps and crawl through 1-block gaps, using the player's own crouching and crawling poses.

## Behaviour

- Affected mobs: zombie, husk, zombie villager (entity type tag `mobscrouchandcrawl:can_crouch_and_crawl`).
  Baby zombies are already small enough for 1-block gaps and keep the vanilla behaviour.
- Crouching: hitbox height x0.75 (1.95 -> 1.4625), fits under 1.5 blocks.
- Crawling: hitbox height x0.5 (1.95 -> 0.975), fits under 1 block.
- Only a mob chasing a target (player, villager, iron golem...) plans paths through low gaps. Wandering
  mobs keep the vanilla pathfinding. A mob already crouching or crawling also plans low paths, to find
  its way out.
- Low paths cost more than standing ones, in proportion to the slower speed, so a mob only goes low
  when it is worth it.
- The mob gets down just before a low gap and stands up again once there is room.
- Crouching and crawling mobs move at 30% speed by default, like a sneaking or crawling player.
- Crawling mobs can still attack.

## Installation

- **Server: required.** All the behaviour runs on the server.
- **Client: recommended.** Without the mod on the client, crouching looks right but a crawling zombie
  is drawn standing, its head through the ceiling, and its hitbox looks taller than it is. With the
  mod, crawling zombies lie down and use the player's swimming arm stroke.

Vanilla clients can join a server running the mod: it only uses vanilla poses and adds no blocks,
items or entity data.

## Configuration

`config/mobscrouchandcrawl.json`, also editable in game with Mod Menu + Cloth Config (both optional).
The settings are used by the server: on a dedicated server, edit the file on the server.

| Setting | Default | Meaning |
|---|---|---|
| `enableCrouching` | `true` | Mobs can crouch under 1.5-block gaps. |
| `enableCrawling` | `true` | Mobs can crawl under 1-block gaps. |
| `crouchSpeedMultiplier` | `0.3` | Speed while crouching (0.05 to 1). |
| `crawlSpeedMultiplier` | `0.3` | Speed while crawling (0.05 to 1). |

Other mobs can be added to the entity type tag with a datapack. They need a humanoid model and
renderer (like zombies) for the crouching and crawling animations to show.

## Known limitations

- Zombies still forget a target they have not seen for 3 seconds (vanilla), so a long detour through
  a tunnel can be abandoned halfway.
- A zombie crawling in 1-block-deep water has its eyes under water and starts turning into a drowned.
- Mods replacing zombie models or animations (Entity Model Features / Fresh Animations...) may not
  show the crawling animation.

## Building

Copy `gradlew`, `gradlew.bat` and the `gradle/wrapper/` folder from another Fabric 1.21.11 project,
then run `./gradlew build`. The jar is in `build/libs/`.
