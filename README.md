# BeautifulJava
BeautifulJava is a tool meant to help with reversing the source code from the game **Project Zomboid**.

Once setup properly, when you run the script, it will do the following for you:
- Grab the Java class files from Project Zomboid folder
- Decompile them using the Java decompiler from IntelliJ
- Rename missing symbols (such as var1, var2, var3, etc), based on their type.

# Dependencies
## jq
You'll have to install the jq command. In case you don't have it already, it can be downloaded from here: https://stedolan.github.io/jq/download/

## IntelliJ
You'll also have to install IntelliJ. BeautifulJava is using it for disassembling and formatting the code. It can be downloaded from here: https://www.jetbrains.com/idea/download/

## Windows Users Only
If you're running Windows, you'll have to install the following programs:
- Notepad++. It can be downloaded from here: https://notepad-plus-plus.org/downloads/
- Git Bash. It can be downloaded from here: https://gitforwindows.org/

# Install
Start a Bash interpreter or Git Bash, and run the following commands:

	cd "${HOME}"
	mkdir -p repos
	cd repos
	git clone https://github.com/quarantin/beautiful-java
	cd beautiful-java

## Linux
	cp examples/config.json.linux.example config.json
	vim config.json

Update the content of config.json with this (update according to your setup):

	{
		"intellij": "~/IntelliJ/bin/idea.sh",
		"zomboid": "~/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid/pzexe.jar"
	}

## Mac OSX
	cp BeautifulJava.macosx BeautifulJava
	cp examples/config.json.macosx.example config.json
	open -a TextEdit config.json
Update the content of config.json with this (update according to your setup):

	{
		"intellij": "/Applications/IntelliJ IDEA CE.app/Contents/bin/format.sh",
		"zomboid": "~/Library/Application Support/Steam/steamapps/common/ProjectZomboid/Project Zomboid.app/Contents/Java/pzexe.jar"
	}

## Windows
	cp examples/config.json.windows.example config.json
	start notepad++ config.json
Update the config of config.json with this (update according to your setup):

	{
		"intellij": "C:/Program Files/JetBrains/IntelliJ IDEA Community Edition 2021.2.3/bin/idea64.exe",
		"zomboid": "C:/SteamLibrary/steamapps/common/ProjectZomboid/ProjectZomboid64.exe"
	}

# Updates
Updating BeautifulJava is done as follow:

	cd "${HOME}/repos/beautiful-java/"
	git pull

# Usage
Run the following command and wait for the script to finish:

	./BeautifulJava
Once the script has finished, the source files should be available in the folder:

	${HOME}/repos/beautiful-java/sources/
