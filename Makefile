NAME      := beautiful-java
BUNDLE    := dist
BUNDLEDIR := $(BUNDLE)/$(NAME)

all: clean build

build:
	javac --release 17 -cp ./src:lib/* -d ./classes -Xlint:unchecked src/beautifuljava/BeautifulJava.java

run: clean build
	java -cp ./classes:lib/* beautifuljava.BeautifulJava ./zombie

dump: clean build
	java -cp ./classes:lib/* beautifuljava.BeautifulJava --dump ./zombie

missing: clean build
	java -cp ./classes:lib/* beautifuljava.BeautifulJava --dump-missing ./zombie

bundle:
	rm -rf $(BUNDLE) && mkdir -p $(BUNDLEDIR) && \
	cp -r BeautifulJava classes lib LICENSE README.md resources scripts $(BUNDLEDIR) && \
	cd $(BUNDLE) && zip -r BeautifulJava.zip $(NAME)

clean:
	rm -f classes/beautifuljava/*.class
