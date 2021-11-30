all: clean build

build:
	javac -cp ./src:lib/tools.jar -source 8 -target 8 -d ./classes -Xlint:unchecked src/beautifuljava/BeautifulJava.java

run: clean build
	java -cp ./classes:lib/tools.jar beautifuljava.BeautifulJava IsoPlayer.java

clean:
	rm -f classes/beautifuljava/*.class
