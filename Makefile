# Variables
CLASSPATH=/usr/local/lib/mpi.jar:.
JAVA_FILE=MPICommMapReduce.java
MAP_REDUCE_FILE=./MapReduceImpl/MapReduce.java
OUT_FILE=MPICommMapReduce

all: compile run

compile:
	javac $(MAP_REDUCE_FILE)
	javac -cp $(CLASSPATH) $(JAVA_FILE)

# Run the program
run:
	mpirun -n 4 java $(OUT_FILE)

# Clean up
clean:
	rm -f *.class
