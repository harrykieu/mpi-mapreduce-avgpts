import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import MapReduceImpl.*;

import java.util.ArrayList;
import java.util.HashMap;

import mpi.*;

class MPICommMapReduce {
    public static final HashMap<String, Integer> map = new HashMap<>(Map.ofEntries(
            Map.entry("Math", 1),
            Map.entry("French", 2),
            Map.entry("English", 3),
            Map.entry("Literature", 4)));

    public static void main(String args[]) throws MPIException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int proc = MPI.COMM_WORLD.getSize();

        List<Float> datasrc = new ArrayList<>();
        IntBuffer datasrcLengthBuffer = MPI.newIntBuffer(1);
        float[] flatArray = null;

        if (rank == 0) {
            String line;
            try (BufferedReader br = new BufferedReader(new FileReader("input.csv"))) {
                while ((line = br.readLine()) != null) {
                    String[] tok = line.split(",");
                    if (Float.parseFloat(tok[1]) != 10.0) {
                        datasrc.add((map.get(tok[0])) * 10 + Float.parseFloat(tok[1]));
                    } else {
                        datasrc.add((map.get(tok[0])) * 100 + Float.parseFloat(tok[1]));
                    }
                }
                System.out.println("[!] Process rank 0 read from input successfully!");
            } catch (Exception e) {
                System.out.println(e.toString());
                System.exit(1);
            }
            int no_padding = datasrc.size() % proc;
            if (no_padding != 0) {
                // Fill the remaining space with 0
                for (int i = 0; i < (proc - no_padding); i++) {
                    datasrc.add((float) 0);
                }
            }
            flatArray = new float[datasrc.size()];
            for (int i = 0; i < datasrc.size(); i++) {
                flatArray[i] = datasrc.get(i);
            }
            datasrcLengthBuffer.put(0, flatArray.length);
        }

        MPI.COMM_WORLD.bcast(datasrcLengthBuffer, 1, MPI.INT, 0);

        int totalDataSize = datasrcLengthBuffer.get(0);
        int dataSizePerProcess = Math.round(totalDataSize / proc); // Size of data each process receives
        float[] recv = new float[dataSizePerProcess];
        MPI.COMM_WORLD.scatter(flatArray, dataSizePerProcess, MPI.FLOAT, recv, dataSizePerProcess, MPI.FLOAT, 0);
        MPI.COMM_WORLD.barrier();

        // MR phase 1
        System.out.println("[!] Process rank " + rank + " enter MapReduce phase 1...");
        Map<Integer, List<Float>> listMap = MapReduce.mapPhase1(recv);
        float[] result = MapReduce.reducePhase1(listMap);

        float[] gather = new float[(int) (map.size() * 3 * proc)];
        MPI.COMM_WORLD.gather(result, (int) (map.size() * 3), MPI.FLOAT, gather,
                (int) (map.size() * 3), MPI.FLOAT, 0);

        if (rank == 0) {
            // MR phase 2
            System.out.println("[!] Process rank 0 enter MapReduce phase 2...");
            Map<Integer, List<Map<Integer, Float>>> listMap2 = MapReduce.mapPhase2(gather);
            // debug
            for (Map.Entry<Integer, List<Map<Integer, Float>>> entry : listMap2.entrySet()) {
                System.out.println("Subject ID: " + entry.getKey());
                List<Map<Integer, Float>> innerList = entry.getValue();
                for (Map<Integer, Float> innerMap : innerList) {
                    for (Map.Entry<Integer, Float> innerEntry : innerMap.entrySet()) {
                        System.out.println(" Count: " + innerEntry.getKey() + ", Pts: " +
                                innerEntry.getValue());
                    }
                }
            }

            float[] result2 = MapReduce.reducePhase2(listMap2);

            try {
                System.out.println("[!] Done! Average of subjects:");
                for (float subjectPts : result2) {
                    int subjectID = Character.getNumericValue(String.valueOf(subjectPts).charAt(0));
                    float pts = Float.parseFloat(String.valueOf(subjectPts).substring(1));
                    String subject = null;
                    // get subject name from subjectId
                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
                        if (entry.getValue().equals(subjectID)) {
                            subject = entry.getKey();
                            break;
                        }
                    }
                    System.out.println("\t" + subject + ":" + pts);
                }
            } catch (Exception e) {
                System.out.println(e);
                System.exit(1);
            }
        }
        MPI.Finalize();
    }
}