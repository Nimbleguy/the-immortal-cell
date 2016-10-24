BINDIR := bin/
SRCDIR := src/
LIBDIR := libs/
OUT := Immortal.class
MAIN := Immortal
SRC := $(wildcard $(SRCDIR)*.java)
OBJ := $(patsubst $(SRCDIR)%.java,$(BINDIR)%.class,$(SRC))

CC := javac
JAR := jar
EXE := java

OPT := 2~2 cell.fs

ARTCIO := commons-io commons-io 2.5

all: dep build run


dep : libs
	

libs : ivysettings.xml ivy.jar
	-mkdir $(LIBDIR)
	java -jar ivy.jar -retrieve "$(LIBDIR)[artifact](-[classifier]).[ext]" -dependency $(ARTCIO) -settings ivysettings.xml

ivy.jar :
	wget http://archive.apache.org/dist/ant/ivy/2.4.0/apache-ivy-2.4.0-bin.zip
	unzip apache-ivy-2.4.0-bin.zip
	mv apache-ivy-2.4.0/ivy-2.4.0.jar ./ivy.jar
	rm -rf apache-ivy-2.4.0 apache-ivy-2.4.0-bin.zip

run:
	while true; do $(EXE) -cp ".:$(LIBDIR)*" $(MAIN) $(OPT); done

clean:
	-rm $(OBJ)

build: $(OBJ)
	#cd $(BINDIR) && $(JAR) cfe $(OUT) $(MAIN) $(patsubst bin/%,%,$(OBJ))
	mv $(BINDIR)$(OUT) $(OUT)

$(BINDIR)%.class: $(SRCDIR)%.java
	$(CC) -cp ".:$(LIBDIR)*" -d $(BINDIR) $<

.PHONY: all build run clean
