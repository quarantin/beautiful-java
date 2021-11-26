# beautiful-java
This is a tool to help reverse-engineering Java code. It's meant to help with reversing the source code from the game Project Zomboid.

# Dependencies
## bash
The main script is written in Bash, so you need a working Bash interpreter.

## jq
You need the jq command. In case you don't have it already, it can be downloaded from here: https://stedolan.github.io/jq/download/

# Install
## Linux
	cd "${HOME}"
	mkdir -p repos
	cd repos
	git clone https://github.com/quarantin/beautiful-java
	cd beautiful-java
	cp config.json.linux.example config.json
	vim config.json
Update the content of config.json with following content:

		{
			"intellij": "~/IntelliJ/bin/idea.sh",
			"zomboid": "~/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid/pzexe.jar"
		}

## Mac OSX
	cd "${HOME}"
	mkdir -p repos
	cd repos
	git clone https://github.com/quarantin/beautiful-java
	cd beautiful-java
	cp config.json.macosx.example config.json
	open -a TextEdit config.json
Update the content of config.json with following content:

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
Update the config of config.json with following content:

		{
			"intellij": "C:/Program Files/JetBrains/IntelliJ IDEA Community Edition 2021.2.3/bin/idea64.exe",
			"zomboid": "C:/SteamLibrary/steamapps/common/ProjectZomboid/ProjectZomboid64.exe"
		}

# Usage
- Run the following command and wait for the script to finish:

		./BeautifulJava
- Once the script has finished, the source files should be available in sources/ folder.
