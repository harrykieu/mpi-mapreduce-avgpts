# Variables
CLASSPATH=/usr/local/lib/mpi.jar
JAVA_FILE=MPICommMapReduce.java
OUT_FILE=MPICommMapReduce

# Default target
all: compile run

# Compile the Java file
compile:
	mpijavac -cp $(CLASSPATH) $(JAVA_FILE)

# Run the program
run:
	mpirun -n 4 java -cp $(CLASSPATH) $(OUT_FILE)

# Clean up
clean:
	rm -f *.class
