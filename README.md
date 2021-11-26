# beautiful-java
This is a tool to help reverse-engineering Java code. It's meant to help with reversing the source code from the game Project Zomboid.

# Install
## Dependencies
- bash: The main script is written in Bash, so you need a working Bash interpreter.
- jq: You need the jq command. It can be downloaded from here: https://stedolan.github.io/jq/download/
- java SDK: You need a valid JDK environment, any version between 11 and 15 included. Any different version will probably not work.
See: https://www.oracle.com/java/technologies/javase/jdk15-archive-downloads.html


# Install
## Linux
	cd "${HOME}"
	mkdir -p repos
	cd repos
	git clone https://github.com/quarantin/beautiful-java
	cd beautiful-java
	cp config.json.linux.example config.json
	vim config.json
	{
		"intellij": "/path/to/IntelliJ/bin/idea.sh",
		"zomboid": "/path/to/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid/pzexe.jar"
	}

## Mac OSX
	cd "${HOME}"
	mkdir -p repos
	cd repos
	git clone https://github.com/quarantin/beautiful-java
	cd beautiful-java
	cp config.json.macosx.example config.json
	open -a TextEdit config.json
	{
		"intellij": "TODO",
		"zomboid": "/path/to/Library/Application Support/Steam/SteamApps/common/ProjectZomboid/pzexe.jar"
	}

## Windows
	cd "${HOME}"
	mkdir -p repos
	cd repos
	git clone https://github.com/quarantin/beautiful-java
	cd beautiful-java
	cp config.json.windows.example config.json
	Notepad config.json
	{
		"intellij": "C:/Program Files/JetBrains/IntelliJ IDEA Community Edition 2021.2.3/bin/idea64.exe",
		"zomboid": "C:/SteamLibrary/steamapps/common/ProjectZomboid/ProjectZomboid64.exe"
	}

# Usage
- Run the following command and wait for the script to finish:

		./BeautifulJava
- Once the script has finished, the source files should be available in sources/ folder.
