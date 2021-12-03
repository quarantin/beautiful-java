#!/bin/bash
set -e

if [ "${0}" = "${BASH_SOURCE}" ]; then
	. "$(dirname "${BASH_SOURCE}")/config.sh"
fi

if ! [ -z "${1}" ]; then
	SOURCEDIR="${1}"
fi

cp -a "${DECOMPDIR}/"* "${SOURCEDIR}"

. "$(dirname ${BASH_SOURCE})/fixjava.sh"

MAIN=beautifuljava.BeautifulJava 
CLASSPATH="classes${PATHSEP}lib/*"
"${JAVA}" -cp "${CLASSPATH}" "${MAIN}" ${LINEFEED} "${SOURCEDIR}" ${@}
