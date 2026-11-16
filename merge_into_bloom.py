#!/usr/bin/env python3
"""Merge the True Opsec feature classes into Bloom Client's jar -> ONE jar.

Usage:
  python merge_into_bloom.py <bloom-client.jar> <build/libs/trueopsec-1.0.0.jar> <output.jar>
"""
import json, sys, zipfile

ENTRY = "com.trueopsec.TrueOpsecClient"
MIXIN_CFG = "trueopsec.client.mixins.json"

def main():
    if len(sys.argv) != 4:
        sys.exit(__doc__)
    bloom_path, mod_path, out_path = sys.argv[1:]

    with zipfile.ZipFile(bloom_path) as bloom, zipfile.ZipFile(mod_path) as mod:
        meta = json.loads(bloom.read("fabric.mod.json"))
        clients = meta.setdefault("entrypoints", {}).setdefault("client", [])
        if ENTRY not in clients:
            clients.append(ENTRY)
        mixins = meta.setdefault("mixins", [])
        if not any((m == MIXIN_CFG) or (isinstance(m, dict) and m.get("config") == MIXIN_CFG) for m in mixins):
            mixins.append({"config": MIXIN_CFG, "environment": "client"})
        meta["description"] = meta.get("description", "") + " Includes True Opsec."

        names = set(bloom.namelist())
        added = 0
        with zipfile.ZipFile(out_path, "w", zipfile.ZIP_DEFLATED) as out:
            for item in bloom.infolist():
                if item.filename == "fabric.mod.json":
                    out.writestr("fabric.mod.json", json.dumps(meta, indent=2))
                else:
                    out.writestr(item, bloom.read(item.filename))
            for item in mod.infolist():
                n = item.filename
                if item.is_dir() or n.startswith("META-INF/") or n == "fabric.mod.json" or n in names:
                    continue
                out.writestr(n, mod.read(n))
                added += 1
    print(f"Done: {out_path} (added {added} files, ONE jar with Bloom + True Opsec)")

if __name__ == "__main__":
    main()
