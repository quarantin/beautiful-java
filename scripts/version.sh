#!/bin/bash
set -e

if ! [ -z "${VERSION}" ]; then
	return
fi

if [ "${0}" = "${BASH_SOURCE}" ]; then
	. "$(dirname "${BASH_SOURCE}")/config.sh" --standalone
fi

TMPDIR="$(mktemp -d)"

cp "${ZOMBOID}/zombie/core/Core.class" "${TMPDIR}"

. "${REPO}/scripts/decompile.sh" "${TMPDIR}" "${TMPDIR}" > /dev/null

VERSION="$(grep -o 'new GameVersion(.*' "${TMPDIR}/Core.java" | head -n 1 | cut -f2 -d'(' | cut -f1,2 -d',' | sed 's/, /./')"
if [ -z "${VERSION}" ]; then
	VERSION="$(grep 'versionNumber =' "${TMPDIR}/Core.java" | cut -f2 -d= | cut -f2 -d'"')"
fi

rm -rf "${TMPDIR}"

if [ -z "${VERSION}" ]; then
	echo
	echo "ERROR: Zomboid version not found!"
	exit
fi

VERSION_MAJOR="$(echo "${VERSION}" | cut -f1 -d.)"
VERSION_MINOR="$(echo "${VERSION}" | cut -f2 -d.)"

if [ "${0}" = "${BASH_SOURCE}" ]; then
	echo $VERSION
fi
