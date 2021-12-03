#!/bin/bash
set -e

if [ "${0}" = "${BASH_SOURCE}" ]; then
	exit
fi

IFS="
"
config_error() {
	echo "${1}"
	echo
	echo "Please follow configuration steps here:"
	echo "https://github.com/quarantin/beautiful-java/#configuring-beautifuljava"
	exit
}

find_path() {

	PATTERN="${1}"
	TARGET="${2}"
	CHECK="${3}"

	DIRPATH="$(grep -i "^${PATTERN}" "${CONFIG}" | cut -f2 -d= | xargs | sed "s|^~|${HOME}|")"

	WINVAR="$(echo "${DIRPATH}" | grep -o '%\([^%]\+\)%' | tr -d '%')"
	if ! [ -z "${WINVAR}" ]; then
		WINVAR="${WINVAR^^}"
		WINVAR="$(echo "${!WINVAR}" | sed 's/\\/\//')"
		DIRPATH="$(echo "${DIRPATH}" | sed "s|%[^%]\+%|${WINVAR}|")"
	fi

	if [ -z "${DIRPATH}" ]; then
		echo "ERROR: ${TARGET} path missing from config.txt"
		return

	elif ! [ -d "${DIRPATH}" ]; then
		echo "ERROR: ${TARGET} path is invalid: ${DIRPATH}"
		return
	fi

	if [ "${PATTERN}" = intellij ]; then
		EXTRAS=("${INTELLIJ_EXTRA[@]}")

	elif [ "${PATTERN}" = zomboid ]; then
		EXTRAS=("${ZOMBOID_EXTRA[@]}")

	else
		echo "ERROR: This should never happen!"
		return
	fi

	for EXTRA in "${EXTRAS[@]}"; do
		if [ -f "${DIRPATH}/${EXTRA}/${CHECK}" ]; then
			DIRPATH="${DIRPATH}/${EXTRA}"
			break
		fi
	done

	if ! [ -f "${DIRPATH}/${CHECK}" ]; then
		echo "ERROR: ${TARGET} path is invalid: ${DIRPATH}"
		return
	fi

	echo "${DIRPATH}"
}


#
# OS specific
#
PATHSEP=':'
OS="$(uname -s)"
if [ "${OS}" = 'Darwin' ]; then
	BSDSED="''"
	FORMATTER=format.sh
	INTELLIJ_EXTRA=(''
		'Contents'
	)
	ZOMBOID_EXTRA=(''
		'Project Zomboid.app'
		'Project Zomboid.app/Contents'
		'Project Zomboid.app/Contents/Java'
	)
elif [ "${OS}" = 'Linux' ]; then
	FORMATTER=idea.sh
	FORMATTER_ARG=format
	INTELLIJ_EXTRA=()
	ZOMBOID_EXTRA=(''
		'projectzomboid'
	)
elif [ "$(uname -o)" = 'Msys' ]; then
	FORMATTER=idea64.exe
	FORMATTER_ARG=format
	INTELLIJ_EXTRA=()
	ZOMBOID_EXTRA=()
	PATHSEP=';'
	PWDOPT=-W
	LINEFEED='--crlf'
	OS="$(uname -o)"
else
	echo "Unsupported operating system: ${OS}"
	exit
fi


#
# Check for config.txt
#

REPO="$(pwd ${PWDOPT})"
CONFIG="${REPO}/config.txt"
if ! [ -f "${CONFIG}" ]; then
	config_error "ERROR: ${CONFIG} not found"
fi

if [ "${OS}" = 'Msys' ]; then
	HASBACKSLASH="$(grep -v '^;\|^$' "${CONFIG}" | grep '\\' || true)"
	if ! [ -z "${HASBACKSLASH}" ]; then
		sed -i 's/\\/\//g' "${CONFIG}"
	fi
fi


#
# Check for IntelliJ install folder
#
INTELLIJ=$(find_path intellij IntelliJ bin/idea.svg)
if ! [ -d "${INTELLIJ}" ]; then
	config_error "${INTELLIJ}"
	exit
fi


#
# Check for Zomboid install folder
#
ZOMBOID=$(find_path zomboid Zomboid zombie/core/Core.class)
if ! [ -d "${ZOMBOID}" ]; then
	config_error "${INTELLIJ}"
	exit
fi


#
# Check for Java
#
JAVA="$(grep -i ^java "${CONFIG}" | cut -f2 -d= | xargs | sed "s|^~|${HOME}|")"
if ! [ -f "${JAVA}" ]; then
	JAVA="${INTELLIJ}/jbr/bin/java"
	if ! [ -f "${JAVA}" ]; then
		JAVA="${INTELLIJ}/jbr/Contents/Home/bin/java"
		if ! [ -f "${JAVA}" ]; then
			config_error "ERROR: java interpreter not found."
		fi
	fi
fi

FORMATTER="${INTELLIJ}/bin/${FORMATTER}"
DECOMPILER="${INTELLIJ}/plugins/java-decompiler/lib/java-decompiler.jar"
DECOMPILER_MAIN=org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler

if [ "${1}" != "--standalone" ]; then
	. "$(dirname "${BASH_SOURCE}")/version.sh"
fi

CLASSDIR="${REPO}/pz/classes/${VERSION}"
DECOMPDIR="${REPO}/pz/decompiled/${VERSION}"
LIBDIR="${REPO}/pz/libs/${VERSION}"
LUADIR="${REPO}/pz/lua/${VERSION}"
SOURCEDIR="${REPO}/pz/sources/${VERSION}"

if [ "${1}" = "--clean" ]; then
	mkdir -p "${LIBDIR}"   "${LUADIR}"   "${CLASSDIR}"   "${SOURCEDIR}"   "${DECOMPDIR}"
	rm   -rf "${LIBDIR}/"* "${LUADIR}/"* "${CLASSDIR}/"* "${SOURCEDIR}/"* "${DECOMPDIR}/"*
fi
