#!/bin/bash
set -e

if [ "${0}" = "${BASH_SOURCE}" ]; then
	. "$(dirname "${BASH_SOURCE}")/config.sh"
fi

if [ -z "${JAVADOC}" ]; then
	echo
	echo 'ERROR: The javadoc command is not installed.'
	echo 'To install the javadoc command you have to install a Java Development Kit environment (JDK) and add it to your PATH environment variable.'
	echo
	exit
fi

SEP=':'
if [ "${OS}" = 'Msys' ]; then
	SEP=';'
fi

if ! [ -z "${1}" ]; then
	SOURCEDIR="${1}"
fi

if ! [ -z "${2}" ]; then
	JAVADOCDIR="${2}"
	mkdir -p "${JAVADOCDIR}"
fi

IFS="${OLDIFS}"

PACKAGES=$(grep --binary-files=text -R ^package "${SOURCEDIR}" | cut -f 2 -d: | sed -e 's/^package //' -e 's/;$//' | sort -u | tr '\n' ' ')
"${JAVADOC}" -source 8 -encoding utf8 -Xdoclint:none -cp "${LIBDIR}${SEP}${LIBDIR}/*" -d "${JAVADOCDIR}" --source-path "${SOURCEDIR}/" ${PACKAGES}

IFS="${NEWIFS}"
