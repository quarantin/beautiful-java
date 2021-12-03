# BeautifulJava
BeautifulJava is a tool meant to help with reversing the source code from the game **Project Zomboid**.

Once setup properly, BeautifulJava will do the following for you:
- Grab the Java class files from Project Zomboid folder
- Decompile them using the Java decompiler from IntelliJ
- Rename missing symbols (such as var1, var2, var3, etc), based on their type.

## Prerequisites

### Zomboid
Obviously you need Project Zomboid installed:

https://projectzomboid.com/

To configure BeautifulJava, you'll have to find the path to Zomboid install folder. Open Steam, go to your **Library** section, right-click the game **Project Zomboid**, select **Properties...**, choose **LOCAL FILES**, then click the **Browse...** button. Open the properties of any file available in the window that just opened and copy its location.

This is the path you'll have to set for **ZOMBOID** when configuring BeautifulJava.

### IntelliJ
You also have to install IntelliJ Community Edition. BeautifulJava is using IntelliJ for disassembling and formatting the code. It can be downloaded from here:

https://www.jetbrains.com/idea/download/

Remember the location where you install IntelliJ. This is the path you'll have to set for **INTELLIJ** when configuring BeautifulJava.

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

Start a Bash interpreter (or Git Bash) and run the following commands:

	cd "${HOME}"
	git clone https://github.com/quarantin/beautiful-java

## Configuring BeautifulJava
Type the following commands:

	cd "${HOME}/beautiful-java"
	cp resources/config.txt.example config.txt

Edit the file **config.txt** according to your setup. You have to identify the section for your operating system, and remove the ';' character in front of the lines starting with INTELLIJ and ZOMBOID. Replace the paths with the values from previous steps. When done, just save the file and quit the editor.

### Configuration for Linux
	vim config.txt

### Configuration for Mac OSX
	open -a TextEdit config.txt

### Configuration for Windows
	start notepad++ config.txt

## Using BeautifulJava
To run BeautifulJava, type the following commands and wait for the script to finish. It should take at minimum a few minutes:

	cd "${HOME}/beautiful-java/"
	./BeautifulJava

Once the script has finished, the Java source files should be available in this folder:

	${HOME}/beautiful-java/pz/sources/

## Updating BeautifulJava
Updating BeautifulJava is done as follow:

	cd "${HOME}/beautiful-java/"
	./bin/update

## Uninstalling BeautifulJava
To uninstall BeautifulJava, simply delete beautiful-java folder:

	cd "${HOME}"
	rm -rf beautiful-java

## Bugs
Please report bugs [here](https://github.com/quarantin/beautiful-java/issues).
