#!/bin/bash
set -e

if [ -z "${1}" ]; then
	echo "Usage: ${0} <source dir>"
	exit
fi

if [ "${0}" = "${BASH_SOURCE}" ]; then
	. "$(dirname "${BASH_SOURCE}")/config.sh"
else
	cp -a "${DECOMPDIR}/"* "${1}"
fi

. "${REPO}/scripts/fixjava.sh"

MAIN=beautifuljava.BeautifulJava 
CLASSPATH="${REPO}/classes${PATHSEP}${REPO}/lib/*"
"${JAVA}" -cp "${CLASSPATH}" -Dfile.encoding=UTF-8 "${MAIN}" ${LINEFEED} ${@}
