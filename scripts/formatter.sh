#!/bin/bash
set -e

if [ "${0}" = "${BASH_SOURCE}" ]; then
	. "$(dirname "${BASH_SOURCE}")/config.sh"
fi

if ! [ -z "${1}" ]; then
	SOURCEDIR="${1}"
fi

FORMATTER_XML=formatter.xml
FORMATTER_CONFIG="${REPO}/${FORMATTER_XML}"
FORMATTER_SAMPLE="${REPO}/resources/${FORMATTER_XML}.example"
if ! [ -f "${FORMATTER_CONFIG}" ]; then
	cp "${FORMATTER_SAMPLE}" "${FORMATTER_CONFIG}"
fi

"${FORMATTER}" ${FORMATTER_ARG} -r -m '*.java' -s "${FORMATTER_CONFIG}" "${SOURCEDIR}" 2>&1 | sed -e '/^IntelliJ/d' -e '/^20/d' -e '/^SLF4J:/d' -e 's/\.\.\.OK$//'
