# beautiful-java
This is a tool to help reverse-engineering Java code. It's meant to help with reversing the source code from the game Project Zomboid.

# Install
		git clone https://github.com/quarantin/beautiful-java
		cd beautiful-java
		cp config.json.example config.json
		# Open config.json and edit accordingly to your setup.
- intellij must be set to point to IntelliJ startup script where you extracted IntelliJ.
- zomboid must be set to point to Project Zomboid main executable file in your Project Zomboid install folder.

# Examples
## Linux
- intellij: ~/IntelliJ/bin/idea.sh
- zomboid:  ~/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid/pzexe.jar
## Mac
- intellij: TODO
- zomboid: ~/Library/Application Support/Steam/SteamApps/common/ProjectZomboid/pzexe.jar
## Windows
- intellij: TODO
- zomboid: /c/SteamLibrary/steamapps/common/ProjectZomboid/ProjectZomboid64.exe
