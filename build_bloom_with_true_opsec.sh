#!/bin/sh
# Put your jar here as bloom-client.jar, then run: sh build_bloom_with_true_opsec.sh
set -e
./gradlew build
python3 merge_into_bloom.py bloom-client.jar build/libs/trueopsec-1.0.0.jar bloom-client-true-opsec.jar
echo "DONE: bloom-client-true-opsec.jar -> put it in .minecraft/mods (remove the old Bloom jar)"
