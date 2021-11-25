
all:
	javac -cp .:tools.jar -Xlint:unchecked FileParser.java

clean:
	rm -f *.class

run:
	java -cp .:tools.jar FileParser.java IsoPlayer.java

runclean:
	java -cp .:tools.jar FileParser.java IsoPlayer.java | grep -v '^$$' > blah; mv blah IsoPlayer.2.java
