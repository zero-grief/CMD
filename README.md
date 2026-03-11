![CMD banner](./assets/cmd/banner.png)

# CMD

**Custom Model Data** is a **server-side Fabric mod for Minecraft Java 1.21.11** built to make modern custom item models easier to use, easier to browse, and easier to apply — without requiring client mods for normal multiplayer use.

CMD is designed to be a **standalone resourcepack utilization mod**. It reads modern item definitions and model files from scanned pack sources, resolves usable definition entries, builds a registry from real pack data, and helps players and admins apply those models through Minecraft's own systems wherever possible.

---

## Highlights

- Vanilla-friendly **Name Tag + smithing table** gameplay path
- Chat browser with **vanilla-style `show_item` previews**
- Pack-driven registry built from scanned definitions and model paths
- Admin tools for apply, reset, reload, and rebuild
- Unified server-owner config at `config/CMD/config.json`
- No client-side Fabric loader or Fabric API required for normal multiplayer use

---

## Main gameplay path

1. Rename a **Name Tag** in an anvil
2. Put the Name Tag and the target item into a **smithing table**
3. CMD applies the needed vanilla-facing item data
4. The generated or merged item-definition tree then determines which model appears

This keeps the public-facing workflow as close to vanilla as possible.

Admin shortcuts still exist:

- `/cmd apply`
- `/cmd reset`
- `/cmd reload`
- `/cmd rebuild`

These are support tools, not replacements for the main player-facing smithing flow.

---

## What CMD is trying to do

CMD's long-term goal is to **read all loaded resource packs**, understand how their item definitions work, and then **merge those definition rules into one unified system**.

That means CMD should:

- scan available resource packs and pack-like archives
- read item definitions from all supported sources
- resolve actual model files and pack metadata
- normalize different trigger styles into one merged internal language
- generate a final top-level resource pack that Minecraft can use directly

The practical target is that CMD can eventually:

- read the requirements for model application from **all available packs in the loaded list**
- combine those requirements into a single merged registry
- expose that merged result through browser/chat/smithing/admin tools
- let vanilla Minecraft render and apply the result through its own systems as far as possible

---

## Command access model

By default:

- all players can use:
  - `/cmd`
  - `/cmd help`
  - `/cmd list`
  - `/cmd list filter ...`
  - `/cmd filters`

- `apply` / `reset` are available to:
  - host/server/admin
  - players granted the configured permission node
  - creative players as a built-in fallback

- `reload` / `rebuild` / `rebuild confirm` / `rebuild deny` are available to:
  - host/server/admin
  - players granted the configured permission node

Creative is **not** treated as admin fallback for reload or rebuild actions.

CMD does not require an external permission mod to function.
If a compatible permission provider is installed, the configured permission nodes can still be used.

---

## Rebuild flow

Rebuild now works in two steps:

1. `/cmd rebuild`
   - scans current configured sources
   - reports:
     - paths read
     - number of archives found
     - number of model `.json` files found under `assets/*/models/**`
     - current grouped counts for:
       - `item`
       - `equipment`
       - `humanoid`

2. `/cmd rebuild confirm`
   - performs the actual rebuild after the analysis step has completed

3. `/cmd rebuild deny`
   - cancels the pending rebuild

This keeps rebuild safer and more transparent.

---

## Three levels of truth

### Input truth
The source pack files describe what exists.

Examples:
- `pack.mcmeta`
- `assets/<namespace>/items/*.json`
- `assets/<namespace>/models/**/*.json`
- textures and equipment assets where relevant

### Resolved truth
CMD builds an internal resolved representation from processed pack data.

This layer should describe:
- base item
- source trigger type
- source requirement structure
- resolved model target
- canonical merged trigger
- asset namespace
- source archive metadata
- fallback behavior
- output-ready merged rule structure

### Runtime truth
When the item is actually used in-game, CMD should express the result through Minecraft's own systems where possible.

Examples:
- `minecraft:custom_model_data`
- `minecraft:equippable`
- vanilla item-definition trees
- vanilla item stack components
- vanilla hover text and command behavior

---

## Definitions direction

CMD should not think only in terms of a flat list of "cases".

Modern Minecraft item definitions are trees of model nodes. CMD should therefore be able to understand and generate richer definition structures, including:

- `select`
- `range_dispatch`
- `condition`
- nested branches
- fallback behavior
- later support for broader or older source styles

The preferred long-term direction is:

- standardize the **primary trigger** toward `custom_model_data.strings` where appropriate
- preserve **secondary vanilla conditions** when Minecraft already supports them in its own definition trees

That means CMD can use one stable merged trigger language while still keeping context-sensitive logic such as:

- `custom_name`
- `main_hand`
- `context_dimension`
- range-based branches
- boolean conditions

---

## Resourcepack merge direction

CMD should eventually be able to generate a merged top-level definitions pack.

That generated pack should:
- sit high in pack priority
- contain unified item-definition files
- express normalized primary trigger identities
- preserve nested conditions where useful
- point to valid model paths and assets

For server use, the safer default remains a complete combined pack when clients must receive one working result directly.

For later experimental workflows, a definitions-first pack may be able to sit above already loaded lower packs and mainly control item-definition logic.

---

## `pack.mcmeta` direction

CMD's generated pack should treat `pack.mcmeta` as an active part of the build process.

Current compatibility target for the generated base pack:
- **min = 55**
- **max = 75.0**

CMD should also read source pack `pack.mcmeta` files for human-facing metadata such as source display names.
For lore cleanliness, only the **first text string** from `pack.description` should be used for display-source extraction.

---

## Current state

The project already contains:

- early resolved-registry architecture
- scanner and translator work toward a pack-driven registry
- shared application logic
- unified config direction
- browser and preview work aligned with vanilla hover behavior
- initial build output for merged item-definition files

The project still needs careful work in areas such as:

- broader definition translation
- full merged pack asset handling
- deeper equipment/equippable path support
- stronger end-to-end resolved-definition usage
- final merged top-pack strategy

---

## Documentation notes

The project now treats the following files as active design references:

- `DefinitionsInfo.md`
- `caseinfo.md`
- `PackMcmetaInfo.md`
- `roadmap.md`

These should be kept aligned with the current merge, registry, lore, rebuild, and resourcepack directions.
