import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.IntBuffer;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

//import MapReduceAverage.*;

import mpi.*;

class MPITest {

    public static void main(String args[]) throws MPIException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int proc = MPI.COMM_WORLD.getSize();
        
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Math", 1);
        map.put("French", 2);       
        map.put("English", 3);
        map.put("Literature", 4);       
        
        List<Float> datasrc = new ArrayList<>();
        IntBuffer datasrcLengthBuffer = MPI.newIntBuffer(1);
        float[] flatArray = null;

        if (rank == 0) {
            String line;
            try (BufferedReader br = new BufferedReader(new FileReader("input.csv"))){
                while ((line = br.readLine()) != null) {
                    String[] tok = line.split(",");
                    if (Float.parseFloat(tok[1]) != 10.0) {
                        datasrc.add((map.get(tok[0]))*10+Float.parseFloat(tok[1]));
                    } else {
                        datasrc.add((map.get(tok[0]))*100+Float.parseFloat(tok[1]));
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                System.exit(1);
            }
            int no_padding = datasrc.size() % proc-1;
            if (no_padding != 0) {
                // Fill the remaining space with 0
                for (int i = 0; i < (proc-no_padding); i ++) {
                    datasrc.add((float)0);
                }
            }
            System.out.println(datasrc.size());
            flatArray = new float[datasrc.size()];
            for (int i = 0; i < datasrc.size(); i++) {
                flatArray[i] = datasrc.get(i);
            }
            datasrcLengthBuffer.put(0, flatArray.length);
        }
        
        
        MPI.COMM_WORLD.bcast(datasrcLengthBuffer, 1, MPI.INT, 0);

        int totalDataSize = datasrcLengthBuffer.get(0); 
        int dataSizePerProcess = Math.round(totalDataSize / proc); // Size of data each process receives
        System.out.println(dataSizePerProcess);
        float[] recv = new float[dataSizePerProcess];

        MPI.COMM_WORLD.scatter(flatArray, dataSizePerProcess, MPI.FLOAT, recv, dataSizePerProcess, MPI.FLOAT, 0);
        MPI.COMM_WORLD.barrier();
        for (int i = 0; i < recv.length; i++){
            System.out.println(rank+" : " + recv[i]);
        }
    

        float[] gather = new float[totalDataSize];
        MPI.COMM_WORLD.gather(recv, dataSizePerProcess, MPI.FLOAT, gather, dataSizePerProcess, MPI.FLOAT, 0);
        // if (rank == 0) {
        //     for (int i = 0; i<gather.length; i++) {
        //         System.out.println(gather[i]);
        //     }
        // }
        MPI.Finalize();
        }
}