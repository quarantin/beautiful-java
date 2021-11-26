
all:
	javac -cp ./src:lib/tools.jar -source 8 -target 8 -d ./classes -Xlint:unchecked src/beautifuljava/BeautifulJava.java

run:
	java -cp ./classes beautifuljava.BeautifulJava IsoPlayer.java

clean:
	rm -f classes/beautifuljava/*.class
