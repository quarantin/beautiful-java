
all:
	javac -cp .:lib/tools.jar -d ./classes -Xlint:unchecked beautifuljava/FileParser.java

run:
	java -cp ./classes beautifuljava.FileParser IsoPlayer.java

clean:
	rm -f classes/beautifuljava/*.class
