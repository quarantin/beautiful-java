all: clean build

build:
	javac --release 11 -cp ./src:lib/* -d ./classes -Xlint:unchecked src/beautifuljava/BeautifulJava.java

run: clean build
	java -cp ./classes:lib/* beautifuljava.BeautifulJava ./zombie

dump: clean build
	java -cp ./classes:lib/* beautifuljava.BeautifulJava --dump ./zombie

missing: clean build
	java -cp ./classes:lib/* beautifuljava.BeautifulJava --dump-missing ./zombie

clean:
	rm -f classes/beautifuljava/*.class
