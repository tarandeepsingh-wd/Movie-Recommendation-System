public class Similarity {

    // Cosine similarity between two vectors
    public static double cosine(double[] a, double[] b) {
        double dot = 0, magA = 0, magB = 0;

        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            magA += a[i] * a[i];
            magB += b[i] * b[i];
        }

        if (magA == 0 || magB == 0){
            return 0;
        }
        return dot / (Math.sqrt(magA) * Math.sqrt(magB));
    }
}

