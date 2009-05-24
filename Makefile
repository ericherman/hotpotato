JAVA = java
JAVAC = javac
LIBDIR = /home/eric/libs
JUNIT_JAR = $(LIBDIR)/junit.jar
BCEL_JAR = $(LIBDIR)/bcel-5.2/bcel-5.2.jar
CLASSPATH = $(BCEL_JAR):$(JUNIT_JAR)
SOURCE_DIR = src
BUILD_DIR = bin
# Absolutely all classes should be referenced by the top level test suite
JAVAC_TARGET = $(SOURCE_DIR)/hotpotato/AllTestSuites.java

all: clean compile

clean:
	find $(BUILD_DIR) -name "*.class" -exec rm -v \{} \;

compile:
	$(JAVAC) -cp $(CLASSPATH) -sourcepath $(SOURCE_DIR) -d $(BUILD_DIR) $(JAVAC_TARGET)

test: clean compile check

check:
	$(JAVA) -cp $(BUILD_DIR):$(CLASSPATH) hotpotato.AllTestSuites
