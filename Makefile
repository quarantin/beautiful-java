all: clean build

build:
	javac -cp ./src:lib/* -source 8 -target 8 -d ./classes -Xlint:unchecked src/beautifuljava/BeautifulJava.java

run: clean build
	java -cp ./classes:lib/* beautifuljava.BeautifulJava ./zombie
	#java -cp ./classes:lib/* beautifuljava.BeautifulJava IsoPlayer.java

dump: clean build
	java -cp ./classes:lib/* beautifuljava.BeautifulJava --dump ./zombie
	#java -cp ./classes:lib/* beautifuljava.BeautifulJava --dump IsoPlayer.java

missing: clean build
	java -cp ./classes:lib/* beautifuljava.BeautifulJava --dump-missing ./zombie
	#java -cp ./classes:lib/* beautifuljava.BeautifulJava --dump-missing IsoPlayer.java

clean:
	rm -f classes/beautifuljava/*.class
