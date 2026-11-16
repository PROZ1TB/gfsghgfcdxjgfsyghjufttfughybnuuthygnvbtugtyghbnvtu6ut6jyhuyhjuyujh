QUICK WAY: double-click BUILD_BLOOM_WITH_TRUE_OPSEC.bat (Mac/Linux: sh build_bloom_with_true_opsec.sh). Your Bloom jar is already included as bloom-client.jar.

TRUE OPSEC -> merged into Bloom Client (ONE jar)   |  Minecraft 1.21.11, client-side only

1. Install JDK 21 and Python 3.
2. In this folder run:   gradlew.bat build      (Windows)   or   ./gradlew build   (Mac/Linux)
   -> creates build/libs/trueopsec-1.0.0.jar   (NOT the -sources jar)
3. Merge it into Bloom:
   python merge_into_bloom.py bloom-client-1_0_33_1.jar build/libs/trueopsec-1.0.0.jar bloom-client-1_0_33_true-opsec.jar
4. Put ONLY bloom-client-1_0_33_true-opsec.jar in .minecraft/mods (plus Fabric API).
   Don't also keep trueopsec-1.0.0.jar or the original Bloom jar in mods.

USE: Multiplayer screen -> small logo button left of "Edit" -> click "True opsec" in the middle.
Settings save to config/trueopsec.properties.
If the Donut board doesn't change, edit the KEY_ words at the top of src/client/java/com/trueopsec/Spoof.java.
