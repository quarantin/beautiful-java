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
	git clone https://github.com/quarantin/beautiful-java
	cd beautiful-java

## Configuring BeautifulJava
Before you can configure BeautifulJava, you have to find the path to Zomboid install folder. Open Steam, go to your **Library** section, right-click the game **Project Zomboid**, select **Properties...**, choose **LOCAL FILES**, then click **Browse...**.

### Configuration for Linux
	cp config.txt.example config.txt
	vim config.txt
Edit **config.txt** according to your setup:

### Configuration for Mac OSX
	cp config.txt.example config.txt
	open -a TextEdit config.txt
Edit **config.txt** according to your setup:

### Configuration for Windows
	cp config.txt.example config.txt
	start notepad++ config.txt
Edit **config.txt** according to your setup:

## Using BeautifulJava
Run the following commands and wait for the script to finish:

	cd "${HOME}/beautiful-java/"
	./BeautifulJava
Once the script has finished, the Java source files should be available in this folder:

	${HOME}/beautiful-java/sources/

## Updating BeautifulJava
Updating BeautifulJava is done as follow:

	cd "${HOME}/beautiful-java/"
	git pull
In case you get an error with git, try the following commands:

	git reset --hard origin/main
	git pull

## Uninstalling BeautifulJava
To uninstall BeautifulJava, simply delete beautiful-java folder:

	cd "${HOME}"
	rm -rf beautiful-java

## Bugs
Please report bugs [here](https://github.com/quarantin/beautiful-java/issues).
