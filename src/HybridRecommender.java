import java.util.*;
import java.util.stream.Collectors;

public class HybridRecommender {

    private final ContentBasedRecommender contentRec;
    private final CollaborativeRecommender collabRec;
    private final double alpha;

    public HybridRecommender(ContentBasedRecommender contentRec,
                             CollaborativeRecommender collabRec) {
        this(contentRec, collabRec, 0.6);
    }

    public HybridRecommender(ContentBasedRecommender contentRec,
                             CollaborativeRecommender collabRec,
                             double alpha) {
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("alpha must be between 0 and 1");
        }
        this.contentRec = contentRec;
        this.collabRec = collabRec;
        this.alpha = alpha;
    }

    public List<Movie> recommend(Movie likedMovie, int userId, int topK) {
        if (topK <= 0) {
            return Collections.emptyList();
        }

        int candidateLimit = Math.max(topK * 10, topK);
        Map<Integer, Double> contentScores = contentRec.recommendWithScores(likedMovie, candidateLimit);
        Map<Integer, Double> collabScores = collabRec.recommendWithScores(userId, candidateLimit);

        Map<Integer, Double> combinedScores = new HashMap<>();
        Set<Integer> candidateIds = new HashSet<>();
        candidateIds.addAll(contentScores.keySet());
        candidateIds.addAll(collabScores.keySet());

        for (int movieId : candidateIds) {
            double contentScore = contentScores.getOrDefault(movieId, 0.0);
            double collabScore = normalizeRating(collabScores.getOrDefault(movieId, 0.0));
            combinedScores.put(movieId, alpha * contentScore + (1 - alpha) * collabScore);
        }

        return combinedScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue(Comparator.reverseOrder()))
                .limit(topK)
                .map(entry -> contentRec.getMovie(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private double normalizeRating(double rating) {
        if (rating <= 0) {
            return 0;
        }
        return Math.max(0, Math.min(1, (rating - 1.0) / 4.0));
    }
}
