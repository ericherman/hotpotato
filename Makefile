JAVA = java
JAVAC = javac
LIBDIR = /home/eric/libs
JUNIT_JAR = $(LIBDIR)/junit.jar
BCEL_JAR = $(LIBDIR)/bcel-5.2/bcel-5.2.jar
CLASSPATH = $(BCEL_JAR):$(JUNIT_JAR)
SOURCE_DIR = src
BUILD_DIR = bin

all: compile

clean:
	find $(BUILD_DIR) -name "*.class" -exec rm -v \{} \;

compile: clean
	JAVAC -cp $(CLASSPATH) -sourcepath $(SOURCE_DIR) -d $(BUILD_DIR) $(SOURCE_DIR)/hotpotato/AllTestSuites.java

test:
	JAVA -cp $(BUILD_DIR):$(CLASSPATH) hotpotato.AllTestSuites
