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

    /**
     * @param input
     * @return float[]
     */
    public static float[] reducePhase1(Map<Integer, List<Float>> input) {
        List<Float> outputList = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.##"); // Format to two decimal places

        for (int subjectId = 1; subjectId <= 4; subjectId++) {
            List<Float> pointsList = input.getOrDefault(subjectId, new ArrayList<>());

            float totalPoints = 0;
            for (float points : pointsList) {
                totalPoints += points;
            }

            int totalCount = pointsList.size();

            // Format the average with subjectId and add to outputList
            String pts = String.valueOf(subjectId) + String.valueOf(totalCount) + df.format(totalPoints);
            outputList.add(Float.parseFloat(pts));
        }

        // Convert List<Float> to float[]
        float[] output = new float[outputList.size()];
        for (int i = 0; i < outputList.size(); i++) {
            output[i] = outputList.get(i);
        }

        return output;
    }

    /**
     * @param MpiInput
     * @return Map<Integer, List<Map<Integer, Float>>>
     */
    public static Map<Integer, List<Map<Integer, Float>>> mapPhase2(float[] MpiInput) {
        Map<Integer, List<Map<Integer, Float>>> InputList = new HashMap<>();
        for (float i : MpiInput) {
            if (i == 0.0) {
                continue;
            }
            String medium = String.valueOf(i);
            // Extract the id and points from tokens
            int subjectId = Integer.parseInt(medium.substring(0, 1));
            int count = Integer.parseInt(medium.substring(1, 2));
            float pts = Float.parseFloat(medium.substring(2));

            // Check if the subject ID already exists in the output map
            if (!InputList.containsKey(subjectId)) {
                // If it doesn't exist, create a new list and put it into the map
                InputList.put(subjectId, new ArrayList<>());
            }

            // Add the points to the list associated with the subject ID
            InputList.get(subjectId).add(Map.of(count, pts));
        }
        return InputList; // Added return statement

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
