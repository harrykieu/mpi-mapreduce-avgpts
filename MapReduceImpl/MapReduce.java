package MapReduceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.DecimalFormat;

public class MapReduce {

    /**
     * @param MpiInput
     * @return Map<Integer, List<Float>>
     */
    public static Map<Integer, List<Float>> mapPhase1(float[] MpiInput) {
        Map<Integer, List<Float>> InputList = new HashMap<>();
        for (float i : MpiInput) {
            if (i == 0.0) {
                continue;
            }
            String medium = String.valueOf(i);
            // Extract the id and points from tokens
            int subjectId = Integer.parseInt(medium.substring(0, 1));
            float pts = Float.parseFloat(medium.substring(1));

            // Check if the subject ID already exists in the output map
            if (!InputList.containsKey(subjectId)) {
                // If it doesn't exist, create a new list and put it into the map
                InputList.put(subjectId, new ArrayList<>());
            }

            // Add the points to the list associated with the subject ID
            InputList.get(subjectId).add(pts);
        }
        return InputList; // Added return statement

    }

    public static float[] reducePhase1(Map<Integer, List<Float>> input) {
        List<Float> outputList = new ArrayList<>();

        for (int subjectId = 1; subjectId <= 4; subjectId++) {
            List<Float> pointsList = input.getOrDefault(subjectId, new ArrayList<>());

            float totalPoints = 0;
            for (float points : pointsList) {
                totalPoints += points;
            }

            int totalCount = pointsList.size();

            // Add subjectId, totalCount, and totalPoints to the outputList
            outputList.add((float) subjectId);
            outputList.add((float) totalCount);
            outputList.add(totalPoints);
        }

        // Convert List<Float> to float[]
        float[] output = new float[outputList.size()];
        for (int i = 0; i < outputList.size(); i++) {
            output[i] = outputList.get(i);
        }

        return output;
    }

    public static Map<Integer, List<Map<Integer, Float>>> mapPhase2(float[] MpiInput) {
        Map<Integer, List<Map<Integer, Float>>> InputList = new HashMap<>();

        // Iterate through the input array by increments of 3
        for (int i = 0; i < MpiInput.length - 2; i += 3) {
            int subjectId = (int) MpiInput[i];
            int totalCount = (int) MpiInput[i + 1];
            float totalPoints = MpiInput[i + 2];

            // Check if the subject ID already exists in the output map
            if (!InputList.containsKey(subjectId)) {
                // If it doesn't exist, create a new list and put it into the map
                InputList.put(subjectId, new ArrayList<>());
            }

            // Add the points to the list associated with the subject ID
            InputList.get(subjectId).add(Map.of(totalCount, totalPoints));
        }
        return InputList;
    }

    public static float[] reducePhase2(Map<Integer, List<Map<Integer, Float>>> input) {
        List<Float> outputList = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.##");

        for (int subjectId = 1; subjectId <= 4; subjectId++) {
            List<Map<Integer, Float>> pointsList = input.get(subjectId);

            float totalPts = 0;
            int totalCount = 0;
            for (Map<Integer, Float> countAndPts : pointsList) {
                for (Map.Entry<Integer, Float> entry : countAndPts.entrySet()) {
                    totalCount += entry.getKey();
                    totalPts += entry.getValue();
                }
                // Use total count and total points here
            }
            String res = String.valueOf(subjectId) + String.valueOf(df.format(totalPts / totalCount));
            outputList.add(Float.parseFloat(res));
        }

        // Convert List<Float> to float[]
        float[] output = new float[outputList.size()];
        for (int i = 0; i < outputList.size(); i++) {
            output[i] = outputList.get(i);
        }

        return output;
    }

}
