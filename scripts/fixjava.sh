#!/bin/bash
set -e

if [ "${0}" = "${BASH_SOURCE}" ] && ! [ -f "${1}" ]; then
	. "$(dirname "${BASH_SOURCE}")/config.sh"
fi

if ! [ -z "${1}" ]; then
	SOURCEDIR="${1}"
fi

if [ -z "${1}" ]; then
	echo "Usage: ${0} <file|directory>"
	exit
fi

fix_non_statement() {

	FILE="${1}"
	if [ -f "${FILE}" ]; then
		sed -i ${BSDSED} \
			-e 's|\((float)var[0-9]* \* (1.0F - (float)var[0-9]*\.getPerkLevel(PerkFactory\.Perks\.Aiming) / 15\.0F);\)$|// \1|' \
			"${FILE}"
	else
		echo "File not found: ${FILE}"
	fi
}

fix_invoke_dynamic() {

	FILE="${1}"
	if [ -f "${FILE}" ]; then
		sed -i ${BSDSED} \
			-e 's|[ ]*<[ ]*invokedynamic[ ]*>[ ]*||' \
			"${FILE}"
	else
		echo "File not found: ${FILE}"
	fi
}

fix_invalid_syntax() {

	# BSD sed BRE implementation is broken, so we have to use ERE here.
	# For a testcase see:
	# https://github.com/quarantin/beautiful-java/tree/main/BSDBUG
	#sed -i ${BSDSED} \
	#	-e 's/(Object\[\])(\(.*\)))\(\[[0-9]\]\)\?;$/new Object[]{ \1 }\2);/' \
	#	"${SOURCEDIR}/zombie/Lua/LuaManager.java" \
	#	"${SOURCEDIR}/zombie/network/GameServer.java"

	FILE="${1}"
	if [ -f "${FILE}" ]; then
		sed -i ${BSDSED} \
			-E -e 's/\(Object\[\]\)\((.*)\)\)(\[[0-9]\])?;$/new Object[]{ \1 }\2);/' \
			"${FILE}"
	else
		echo "File not found: ${FILE}"
	fi
}

fix_return_yield() {

	FILE="${1}"
	if [ -f "${FILE}" ]; then
		sed -i ${BSDSED} \
			-e 's/return yield(/return this.yield(/' \
			"${FILE}"
	else
		echo "File not found ${FILE}"
	fi
}

if [ -f "${SOURCEDIR}" ]; then

	fix_non_statement  "${SOURCEDIR}"
	fix_invoke_dynamic "${SOURCEDIR}"
	fix_invalid_syntax "${SOURCEDIR}"
	fix_return_yield   "${SOURCEDIR}"

else
	fix_non_statement  "${SOURCEDIR}/zombie/ai/states/SwipeStatePlayer.java"
	fix_invoke_dynamic "${SOURCEDIR}/zombie/characters/IsoPlayer.java"
	fix_invalid_syntax "${SOURCEDIR}/zombie/Lua/LuaManager.java"
	fix_invalid_syntax "${SOURCEDIR}/zombie/network/GameServer.java"

	if [ "${VERSION_MAJOR}" -le "40" ]; then
		fix_invalid_syntax "${SOURCEDIR}/zombie/scripting/commands/Lua/LuaCall.java"
		fix_return_yield "${SOURCEDIR}/se/krka/kahlua/stdlib/CoroutineLib.java"
	fi
fi
