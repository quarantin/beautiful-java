
all:
	javac -cp .:tools.jar -Xlint:unchecked JavaSourceVisitor.java FileParser.java

clean:
	rm -f *.class

run:
	java -cp .:tools.jar FileParser.java IsoPlayer.java | grep -v '^$$' > blah; mv blah IsoPlayer.2.java
