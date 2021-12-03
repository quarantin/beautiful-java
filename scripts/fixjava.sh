#!/bin/bash
set -e

if [ "${0}" = "${BASH_SOURCE}" ]; then
	. "$(dirname "${BASH_SOURCE}")/config.sh"
fi

if ! [ -z "${1}" ]; then
	SOURCEDIR="${1}"
fi

sed -i ${BSDSED} \
	-e 's|\((float)var[0-9]* \* (1.0F - (float)var[0-9]*\.getPerkLevel(PerkFactory\.Perks\.Aiming) / 15\.0F);\)$|// \1|' \
	"${SOURCEDIR}/zombie/ai/states/SwipeStatePlayer.java"

sed -i ${BSDSED} \
	-e 's|<invokedynamic>|/* <invokedynamic> */|' \
	"${SOURCEDIR}/zombie/characters/IsoPlayer.java"

sed -i ${BSDSED} \
	-e 's/(Object\[\])(\(.*\)))\(\[[0-9]\]\)\?;$/new Object[]{ \1 }\2);/' \
	"${SOURCEDIR}/zombie/Lua/LuaManager.java" \
	"${SOURCEDIR}/zombie/network/GameServer.java"

# <= 40.43
FILE="${SOURCEDIR}/zombie/scripting/commands/Lua/LuaCall.java"
if  [ -f "${FILE}" ]; then
	sed -i ${BSDSED} \
		-e 's/(Object\[\])(\(.*\)))\(\[[0-9]\]\)\?;$/new Object[]{ \1 }\2);/' \
		"${SOURCEDIR}/zombie/scripting/commands/Lua/LuaCall.java"
fi

# <= ?.?
FILE="${SOURCEDIR}/se/krka/kahlua/stdlib/CoroutineLib.java"
if [ -f "${FILE}" ]; then
	sed -i ${BSDSED} \
		-e 's/return yield(/return this.yield(/' \
		"${SOURCEDIR}/se/krka/kahlua/stdlib/CoroutineLib.java"
fi
