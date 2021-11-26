# beautiful-java
This is a tool to help reverse-engineering Java code. It's meant to help with reversing the source code from the game Project Zomboid.

# Install
## Dependencies
- bash: The main script is written in Bash, so you need a working Bash interpreter.
- jq: You have to install jq.
See: https://stedolan.github.io/jq/download/
- java SDK: You need a valid JDK environment version 15. Anything different will probably not work.
See: https://www.oracle.com/java/technologies/javase/jdk15-archive-downloads.html

## Installing beautiful-java
		git clone https://github.com/quarantin/beautiful-java

# Configuration
- Go to the folder where you downloaded beautiful-java:

		cd beautiful-java
- Copy config.json.example to config.json

		cp config.json.example config.json
- Open config.json for editing
## config.json (Linux)
	{
		"intellij": "/path/to/IntelliJ/bin/idea.sh",
		"zomboid": "/path/to/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid/pzexe.jar",
		"java": "/path/to/java"
	}
## config.json (Mac)
	{
		"intellij": "TODO",
		"zomboid": "/path/to/Library/Application Support/Steam/SteamApps/common/ProjectZomboid/pzexe.jar",
		"java": "/path/to/java"
	}
## config.json (Windows)
	{
		"intellij": "TODO",
		"zomboid":  "C:\SteamLibrary\steamapps\common\ProjectZomboid\ProjectZomboid64.exe",
		"java": "/path/to/java"
	}

# Usage
- Run the following command and wait for the script to finish:

		./BeautifulJava
- Once the script has finished, the source files should be available in beautiful-java/sources/
