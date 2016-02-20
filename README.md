# MapRestore
Restore game map. It restore map on server startup or runtime.

## Requirement
- *nix operation system

## Usage
Put your world folder into plugin's data folder and edit configure.
```YAML
restore-on-startup:
- survival-1
```
Plugin will restore the world named "survival-1" on server startup.

An API here allown runtime map restore.
> MapRestore.restoreMap(survival-1);

It will unload, restore and reload the world named "survival".
