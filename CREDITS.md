# Credits

## CIM / Just_S

This project includes CMD-specific adaptations of smithing and recipe implementation work that were intentionally brought closer to the original **CIM** mod structure in order to improve build stability and preserve a proven workflow.

**Original mod referenced:** CIM  
**Original author:** Just_S

### How CIM was used
CIM was used as a direct technical reference for:
- smithing recipe structure
- custom any-item recipe ingredient structure
- serializer organization around the smithing path
- the Name Tag + smithing application workflow

### What CMD changed
CMD does not present CIM's original work as its own. The adapted code path was changed to:
- use CMD package names and mod id
- remove namespace locking so valid `namespace:path` ids can be used
- add a lore line when a model is applied through a Name Tag
- integrate with CMD-specific scanning, merging, preview, and command systems

CIM remains the original source of that implementation direction and should be credited accordingly.
