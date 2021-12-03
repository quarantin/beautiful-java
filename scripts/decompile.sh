#!/bin/bash
set -e

if [ "${0}" = "${BASH_SOURCE}" ]; then
	. "$(dirname "${BASH_SOURCE}")/config.sh" --standalone
fi

if ! [ -z "${1}" ]; then
	CLASSDIR="${1}"
fi

if ! [ -z "${2}" ]; then
	DECOMPDIR="${2}"
fi

"${JAVA}" -cp "${DECOMPILER}" "${DECOMPILER_MAIN}" "${CLASSDIR}" "${DECOMPDIR}" | sed -e 's/^INFO:  //' -e '/^\.\.\. done/d'
