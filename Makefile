
all:
	javac -cp ./src:lib/tools.jar -source 8 -target 8 -d ./classes -Xlint:unchecked src/beautifuljava/Beautiful.java

run:
	java -cp ./classes beautifuljava.Beautiful IsoPlayer.java

clean:
	rm -f classes/beautifuljava/*.class
