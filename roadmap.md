# CMD Roadmap

This file is updated whenever project-archive progress changes in chat. It summarizes current goals, compares them against implemented code paths, and moves finished work into the implemented checklist in chronological order.

## Current goals

1. Complete merged pack asset handling  
   Short description: Copy and reconcile model, texture, equipment, and related asset files across scanned resource packs so the built server `resources.zip` works as a complete combined pack.  
   Current comparison: item-definition normalization and `SOURCES.txt` generation exist, but full asset copying and path rewriting are not yet implemented.

2. Broaden definition translation coverage  
   Short description: Support more item-definition formats, including older predicate-based formats and later compatibility work for CIT/CIM-style packs where feasible.  
   Current comparison: resolved translation exists for a narrow set of modern item-definition selector types only.

3. Deepen equipment and equippable asset handling  
   Short description: Confirm and support real equipment asset paths such as humanoid and humanoid_leggings where required by actual armor and equipment packs.  
   Current comparison: resolved asset namespace application exists, but full equipment path and asset build handling is still incomplete.

4. Shift rebuild truth toward final merged output where appropriate  
   Short description: Keep the long-term path pointed at using final merged definitions as the authoritative runtime truth while leaving room for later client-side definition-only experiments.  
   Current comparison: registries are still built from scan results, while merged item definitions are built as a separate output step.

5. Expand config-guided pack handling  
   Short description: Continue growing `config/CMD/config.json` so server owners can control scan sources, world resources, datapack paths, extra definition targets, and later merge behavior examples from one place.  
   Current comparison: initial packhandling fields and examples now exist, but later custom-source and merge-behavior options remain to be added.

6. Reintroduce legacy config migration when release packaging begins  
   Short description: Add automatic migration from older mistaken config names only when the mod is preparing for public release and real upgrade paths matter.  
   Current comparison: active legacy migration has now been removed from development builds to keep current behavior simple.

7. Stabilize command, hover, and permission compatibility for Yarn 1.21.11  
   Short description: Replace outdated text-event and permission assumptions with calls that actually match the current mappings and vanilla runtime behavior.  
   Current comparison: command-side compatibility has improved, but more compile/runtime verification is still needed.

8. Evolve the resolved layer from flat cases toward definition-tree understanding  
   Short description: Move the project language and internal structure away from overly flat case terminology and toward resolved definition entries/rule trees that can represent several condition styles together.  
   Current comparison: current code still largely thinks in flat case structures, while the design direction now explicitly allows nested select/range_dispatch/condition logic.

9. Standardize the primary trigger without destroying secondary vanilla logic  
   Short description: Use `custom_model_data.strings` as the preferred canonical trigger identity where appropriate, while preserving dimension/context/name/condition/range logic in generated item-definition trees.  
   Current comparison: current normalization mainly targets string identity, but preserved secondary condition structure is not yet deep enough.

10. Formalize translated lore generation from source analysis  
    Short description: Build lore from analyzed metadata, using translated source names, merged model identifiers, and cleaned source display text from `pack.mcmeta`.  
    Current comparison: design direction is clear, but the code does not yet generate the final translated lore system described in the documentation.

11. Formalize generated `pack.mcmeta` support for the CMD top pack  
    Short description: Generate a modern CMD pack metadata file that correctly describes the intended compatibility span and human-facing pack identity.  
    Current comparison: current builder writes a documented min/max-based pack metadata block, but later refinement may still be needed.

12. Deepen rebuild preview reporting  
    Short description: Expand `/cmd rebuild` analysis so it reports more exact pack structure and model/equipment path information before the actual build is confirmed.  
    Current comparison: current rebuild preview already reports paths read, archive count, and broad model-json counts, but deeper pack-aware analysis is still planned.

## Implemented

- [x] Unified main config path corrected to `config/CMD/config.json`  
  Function: The project now points to the correctly named main config file instead of the earlier misspelled file name.

- [x] Active development config now uses only `config.json`  
  Function: Development builds no longer try to migrate `Condig.json` or `config.jsonc`; the project reads and writes only the intended config filename until release preparation needs migration support.

- [x] Packhandling config block added to the unified config  
  Function: Server owners can already control world `resources.zip`, datapack zip scanning, mod jar scanning, additional scan roots, and extra definition targets from one central config.

- [x] Vanilla item target coverage expanded and categorized for chat and preview  
  Function: Weapons, tools, equipment, carved pumpkin, and totem targets are grouped into chat and browser categories with representative preview items.

- [x] Extra definition targets supported through config  
  Function: Additional item-definition targets such as `minecraft:stick` can be added without hardcoding new scan targets into the code.

- [x] World `resources.zip` prioritized as a scan source when enabled  
  Function: CMD can begin from the world pack that a server already distributes, then layer additional scanned sources into its analysis flow.

- [x] Duplicate canonical CMD values made unique per item during normalization  
  Function: Later collisions such as two `katana` entries on the same base item are renamed deterministically to keep merged string selectors unique.

- [x] `SOURCES.txt` build output added  
  Function: The build output includes a readable source summary for loaded archives and merged item definitions.

- [x] Documented the updated three-layer truth model  
  Function: The project direction now explicitly distinguishes input truth, resolved truth, and runtime truth, helping future refactors stay aligned with Minecraft's own runtime behavior.

- [x] Documented nested condition-tree direction for item definitions  
  Function: The design now explicitly allows multiple requirements through nested vanilla item-definition nodes instead of assuming everything must be represented as a flat case list.

- [x] Documented translated lore direction for merged model identities  
  Function: The project now records that model display text should come from analyzed source data and translation keys instead of raw trigger values alone.

- [x] Documented `pack.mcmeta` source-display extraction rule  
  Function: Lore/browser source display text should use only the earliest description text string for cleanliness, with zip-name fallback when needed.

- [x] Permissions split by individual CMD command paths in config design  
  Function: The command system now has separate configurable permission-node entries for root/help/list/filter/apply/reset/reload/rebuild/confirm/deny.

- [x] Rebuild converted to a two-step command flow  
  Function: `/cmd rebuild` now acts as an analysis/preview step, while `/cmd rebuild confirm` performs the actual rebuild and `/cmd rebuild deny` cancels the pending action.
