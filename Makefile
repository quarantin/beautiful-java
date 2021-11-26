
all:
	javac -cp .:lib/tools.jar -d ./classes -Xlint:unchecked beautifuljava/Beautiful.java

run:
	java -cp ./classes beautifuljava.Beautiful IsoPlayer.java

clean:
	rm -f classes/beautifuljava/*.class
