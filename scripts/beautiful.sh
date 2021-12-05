#!/bin/bash
set -e

if [ -z "${1}" ]; then
	echo "Usage: ${0} <source dir>"
	exit
fi

if [ "${0}" = "${BASH_SOURCE}" ]; then
	. "$(dirname "${BASH_SOURCE}")/config.sh"
fi

cp -a "${DECOMPDIR}/"* "${1}"

. "${REPO}/scripts/fixjava.sh"

MAIN=beautifuljava.BeautifulJava 
CLASSPATH="${REPO}/classes${PATHSEP}${REPO}/lib/*"
"${JAVA}" -cp "${CLASSPATH}" "${MAIN}" ${LINEFEED} ${@}
