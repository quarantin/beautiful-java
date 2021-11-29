# BeautifulJava
BeautifulJava is a tool meant to help with reversing the source code from the game **Project Zomboid**.

Once setup properly, BeautifulJava will do the following for you:
- Grab the Java class files from Project Zomboid folder
- Decompile them using the Java decompiler from IntelliJ
- Rename missing symbols (such as var1, var2, var3, etc), based on their type.

## Prerequisites

### Zomboid
Obviously you need to have Project Zomboid installed:

https://projectzomboid.com/

### IntelliJ
You also have to install IntelliJ Community Edition. BeautifulJava is using IntelliJ for disassembling and formatting the code. It can be downloaded from here:

https://www.jetbrains.com/idea/download/

Remember the location where you install IntelliJ, you'll need it later.

### Prerequisites for Linux
- Install Git:

		sudo apt install git

### Prerequisites for Mac OSX
- Install XCode: https://developer.apple.com/xcode/

### Prerequisites for Windows
- Install Git Bash: https://gitforwindows.org/
- Install Notepad++: https://notepad-plus-plus.org/downloads/

## Installing BeautifulJava
Once you've got Zomboid, IntelliJ, and the prerequisites for your operating system installed, you can proceed to installing BeautifulJava.

Start a Bash interpreter and run the following commands:

	cd "${HOME}"
	mkdir -p repos
	cd repos
	git clone https://github.com/quarantin/beautiful-java
	cd beautiful-java

Before you can configure BeautifulJava, you have to find the path to Zomboid main executable. Open Steam, go to your **Library** section, right-click the game **Project Zomboid**, select **Properties**, choose **LOCAL FILES**, then click **Browse...**. From there, depending on your operating system, you have to find a file named pzexe.jar (for Linux and MacOSX), or ProjectZomboid64.exe (for Windows). You have to copy the full path to the main executable.

## Configuring BeautifulJava
### Configuration for Linux
	cp examples/config.txt.linux.example config.txt
	vim config.txt

Update the content of config.txt with this (update according to your setup):

	INTELLIJ=~/IntelliJ/bin/idea.sh
	ZOMBOID=~/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid/pzexe.jar

### Configuration for Mac OSX
	cp examples/config.txt.macosx.example config.txt
	open -a TextEdit config.txt
Update the content of config.txt with this (update according to your setup):

	INTELLIJ=/Applications/IntelliJ IDEA CE.app/Contents/bin/format.sh
	ZOMBOID=~/Library/Application Support/Steam/steamapps/common/ProjectZomboid/Project Zomboid.app/Contents/Java/pzexe.jar

### Configuration for Windows
	cp examples/config.txt.windows.example config.txt
	start notepad++ config.txt
Update the config of config.txt with this (update according to your setup):

	INTELLIJ=C:/Program Files/JetBrains/IntelliJ IDEA Community Edition 2021.2.3/bin/idea64.exe
	ZOMBOID=C:/SteamLibrary/steamapps/common/ProjectZomboid/ProjectZomboid64.exe

## Updating BeautifulJava
Updating BeautifulJava is done as follow:

	cd "${HOME}/repos/beautiful-java/"
	git pull

## Using BeautifulJava
Run the following commands and wait for the script to finish:

	cd "${HOME}/repos/beautiful-java/"
	./BeautifulJava
Once the script has finished, the Java source files should be available in the folder:

	${HOME}/repos/beautiful-java/sources/

## Bugs
Please report bugs [here](https://github.com/quarantin/beautiful-java/issues).
