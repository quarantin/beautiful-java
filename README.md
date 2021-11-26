# BeautifulJava
This tool is meant to help with reversing the source code from the game **Project Zomboid**.

Once setup properly, when you run the script, it will do the following for you:
- Grab the Java class files from Project Zomboid folder
- Decompile them using the Java decompiler from IntelliJ
- Rename missing symbols (such as var1, var2, var3, etc), based on their type.

# Dependencies
## bash
The main script is written in Bash, so you need a working Bash interpreter.

## jq
You also need the jq command. In case you don't have it already, it can be downloaded from here: https://stedolan.github.io/jq/download/

# Install
Start a Bash interpreter, and run the following commands:

	cd "${HOME}"
	mkdir -p repos
	cd repos
	git clone https://github.com/quarantin/beautiful-java
	cd beautiful-java

# Configuration
## Linux
	cp config.json.linux.example config.json
	vim config.json
Update the content of config.json with this (update according to your setup):

	{
		"intellij": "~/IntelliJ/bin/idea.sh",
		"zomboid": "~/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid/pzexe.jar"
	}

## Mac OSX
	cp config.json.macosx.example config.json
	open -a TextEdit config.json
Update the content of config.json with this (update according to your setup):

	{
		"intellij": "TODO",
		"zomboid": "/path/to/Library/Application Support/Steam/SteamApps/common/ProjectZomboid/pzexe.jar"
	}

## Windows
	cp config.json.windows.example config.json
	Notepad config.json
Update the config of config.json with this (update according to your setup):

	{
		"intellij": "C:/Program Files/JetBrains/IntelliJ IDEA Community Edition 2021.2.3/bin/idea64.exe",
		"zomboid": "C:/SteamLibrary/steamapps/common/ProjectZomboid/ProjectZomboid64.exe"
	}

# Updates
Updating BeautifulJava is done as follow:

	cd "${HOME}/repos/beautfiul-java/"
	git pull

# Usage
Run the following command and wait for the script to finish:

	./BeautifulJava
Once the script has finished, the source files should be available in the folder:

	${HOME}/repos/beautiful-java/sources/
