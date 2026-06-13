import java.util.*;
import java.util.stream.Collectors;

public class CollaborativeRecommender {

    private static final int DEFAULT_NEIGHBOR_LIMIT = 40;
    private static final int MIN_COMMON_RATINGS = 5;
    private static final int MIN_NEIGHBOR_SUPPORT = 2;

    private final Map<Integer, Map<Integer, Integer>> userRatings;
    private final Map<Integer, Double> userAverages;

    public CollaborativeRecommender(Map<Integer, Map<Integer, Integer>> userRatings) {
        this.userRatings = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : userRatings.entrySet()) {
            this.userRatings.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        this.userAverages = computeUserAverages();
    }

    private Map<Integer, Double> computeUserAverages() {
        Map<Integer, Double> averages = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : userRatings.entrySet()) {
            Map<Integer, Integer> ratings = entry.getValue();
            double sum = 0;
            for (int rating : ratings.values()) {
                sum += rating;
            }
            averages.put(entry.getKey(), sum / ratings.size());
        }
        return averages;
    }

    private UserSimilarity similarity(int userId, int otherId) {
        Map<Integer, Integer> targetRatings = userRatings.get(userId);
        Map<Integer, Integer> otherRatings = userRatings.get(otherId);

        Set<Integer> commonMovies = new HashSet<>(targetRatings.keySet());
        commonMovies.retainAll(otherRatings.keySet());

        if (commonMovies.size() < MIN_COMMON_RATINGS) {
            return new UserSimilarity(otherId, 0);
        }

        double meanTarget = userAverages.get(userId);
        double meanOther = userAverages.get(otherId);
        double numerator = 0;
        double denominatorA = 0;
        double denominatorB = 0;

        for (int movieId : commonMovies) {
            double adjustedTarget = targetRatings.get(movieId) - meanTarget;
            double adjustedOther = otherRatings.get(movieId) - meanOther;
            numerator += adjustedTarget * adjustedOther;
            denominatorA += adjustedTarget * adjustedTarget;
            denominatorB += adjustedOther * adjustedOther;
        }

        double denominator = Math.sqrt(denominatorA) * Math.sqrt(denominatorB);
        if (denominator == 0) {
            return new UserSimilarity(otherId, 0);
        }

        double rawSimilarity = numerator / denominator;
        double significance = Math.min(1.0, commonMovies.size() / 50.0);
        return new UserSimilarity(otherId, rawSimilarity * significance);
    }

    public Map<Integer, Double> recommendWithScores(int userId, int topK) {
        Map<Integer, Integer> targetRatings = userRatings.get(userId);
        if (targetRatings == null || topK <= 0) {
            return Collections.emptyMap();
        }

        Map<Integer, Double> weightedSum = new HashMap<>();
        Map<Integer, Double> weightSum = new HashMap<>();
        Map<Integer, Integer> supportCount = new HashMap<>();

        for (UserSimilarity neighbor : topSimilarUsers(userId)) {
            double otherMean = userAverages.get(neighbor.userId);
            for (Map.Entry<Integer, Integer> entry : userRatings.get(neighbor.userId).entrySet()) {
                int movieId = entry.getKey();
                if (targetRatings.containsKey(movieId)) {
                    continue;
                }

                double adjustedRating = entry.getValue() - otherMean;
                weightedSum.merge(movieId, neighbor.score * adjustedRating, Double::sum);
                weightSum.merge(movieId, Math.abs(neighbor.score), Double::sum);
                supportCount.merge(movieId, 1, Integer::sum);
            }
        }

        double userMean = userAverages.getOrDefault(userId, 3.0);
        return weightedSum.entrySet().stream()
                .filter(entry -> supportCount.getOrDefault(entry.getKey(), 0) >= MIN_NEIGHBOR_SUPPORT)
                .map(entry -> Map.entry(entry.getKey(), clampRating(userMean + entry.getValue() / Math.max(1e-8, weightSum.getOrDefault(entry.getKey(), 1.0)))))
                .sorted(Map.Entry.<Integer, Double>comparingByValue(Comparator.reverseOrder()))
                .limit(topK)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    public List<Integer> recommend(int userId, int topK) {
        return new ArrayList<>(recommendWithScores(userId, topK).keySet());
    }

    private List<UserSimilarity> topSimilarUsers(int userId) {
        List<UserSimilarity> similarities = new ArrayList<>();

        for (int otherUserId : userRatings.keySet()) {
            if (otherUserId == userId) {
                continue;
            }

            UserSimilarity similarity = similarity(userId, otherUserId);
            if (similarity.score > 0) {
                similarities.add(similarity);
            }
        }

        similarities.sort(Comparator.comparingDouble((UserSimilarity value) -> value.score).reversed());
        if (similarities.size() > DEFAULT_NEIGHBOR_LIMIT) {
            return similarities.subList(0, DEFAULT_NEIGHBOR_LIMIT);
        }
        return similarities;
    }

    private double clampRating(double rating) {
        return Math.max(1.0, Math.min(5.0, rating));
    }

    private static class UserSimilarity {
        private final int userId;
        private final double score;

        private UserSimilarity(int userId, double score) {
            this.userId = userId;
            this.score = score;
        }
    }
}
