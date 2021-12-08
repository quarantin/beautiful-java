#!/bin/bash
set -e

if [ "${0}" = "${BASH_SOURCE}" ]; then
	. "$(dirname "${BASH_SOURCE}")/config.sh"
fi

if ! [ -z "${1}" ]; then
	SOURCEDIR="${1}"
fi

FILE="${SOURCEDIR}/zombie/ai/states/SwipeStatePlayer.java"
if [ -f "${FILE}" ]; then
	sed -i ${BSDSED} \
		-e 's|\((float)var[0-9]* \* (1.0F - (float)var[0-9]*\.getPerkLevel(PerkFactory\.Perks\.Aiming) / 15\.0F);\)$|// \1|' \
		"${FILE}"
else
	echo "File not found: ${FILE}"
fi

FILE="${SOURCEDIR}/zombie/characters/IsoPlayer.java"
if [ -f "${FILE}" ]; then
	sed -i ${BSDSED} \
		-e 's|<invokedynamic>||' \
		"${FILE}"
else
	echo "File not found: ${FILE}"
fi

# BSD sed BRE implementation is broken, so we have to use ERE here.
# For a testcase see:
# https://github.com/quarantin/beautiful-java/tree/main/BSDBUG
#sed -i ${BSDSED} \
#	-e 's/(Object\[\])(\(.*\)))\(\[[0-9]\]\)\?;$/new Object[]{ \1 }\2);/' \
#	"${SOURCEDIR}/zombie/Lua/LuaManager.java" \
#	"${SOURCEDIR}/zombie/network/GameServer.java"

FILES=("${SOURCEDIR}/zombie/Lua/LuaManager.java"
"${SOURCEDIR}/zombie/network/GameServer.java")

for FILE in "${FILES[@]}"; do

	if [ -f "${FILE}" ]; then
		sed -i ${BSDSED} \
			-E -e 's/\(Object\[\]\)\((.*)\)\)(\[[0-9]\])?;$/new Object[]{ \1 }\2);/' \
			"${FILE}"
	else
		echo "File not found: ${FILE}"
	fi
done

# <= 40.43
FILE="${SOURCEDIR}/zombie/scripting/commands/Lua/LuaCall.java"
if  [ -f "${FILE}" ]; then
	sed -i ${BSDSED} \
		-E -e 's/\(Object\[\]\)\((.*)\)\)(\[[0-9]\])?;$/new Object[]{ \1 }\2);/' \
		"${SOURCEDIR}/zombie/scripting/commands/Lua/LuaCall.java"
fi

# <= ?.?
FILE="${SOURCEDIR}/se/krka/kahlua/stdlib/CoroutineLib.java"
if [ -f "${FILE}" ]; then
	sed -i ${BSDSED} \
		-e 's/return yield(/return this.yield(/' \
		"${SOURCEDIR}/se/krka/kahlua/stdlib/CoroutineLib.java"
fi
