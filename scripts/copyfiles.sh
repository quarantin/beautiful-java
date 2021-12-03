#!/bin/bash
set -e

if [ "${0}" = "${BASH_SOURCE}" ]; then
	. "$(dirname "${BASH_SOURCE}")/config.sh"
fi

# Copy Lua files
cp -r "${ZOMBOID}/media/lua/"* "${LUADIR}"

# Copy class files
PACKAGES=(astar
	com
	de
	fmod
	javax
	org
	rcon
	se
	sun
	zombie
)
for PACKAGE in "${PACKAGES[@]}"; do
	if [ -d "${ZOMBOID}/${PACKAGE}" ]; then
		cp -r "${ZOMBOID}/${PACKAGE}" "${CLASSDIR}"
	fi
done

# Copy jar files
find "${ZOMBOID}" -type f -name '*.jar' -exec cp {} "${LIBDIR}" \;

# Add Junit and Hamcrest to lib directory
cp "${REPO}/resources/"{junit-4.12.jar,hamcrest-2.2.jar} "${LIBDIR}"

# Move class files from astar and rcon to lib directory
for PACKAGE in astar rcon; do
	if [ -d "${CLASSDIR}/${PACKAGE}" ]; then
		mv "${CLASSDIR}/${PACKAGE}" "${LIBDIR}"
	fi
done
