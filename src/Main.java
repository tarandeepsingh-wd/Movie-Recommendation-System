import java.util.List;
import java.util.Map;

public class Main {

    private static final int DEFAULT_USER_ID = 1;
    private static final int DEFAULT_MOVIE_ID = 1;
    private static final int DEFAULT_TOP_K = 10;
    private static final double DEFAULT_ALPHA = 0.85;

    public static void main(String[] args) {
        Map<Integer, Movie> movies = DataLoader.loadMovies();
        Map<Integer, Map<Integer, Integer>> ratings = DataLoader.loadRatings();

        System.out.printf("Loaded %d movies and %d users from data/%n", movies.size(), ratings.size());

        ContentBasedRecommender contentRec = new ContentBasedRecommender(movies);
        CollaborativeRecommender collabRec = new CollaborativeRecommender(ratings);

        int sampleUser = parseIntArg(args, 0, DEFAULT_USER_ID, "userId");
        int sampleMovieId = parseIntArg(args, 1, DEFAULT_MOVIE_ID, "movieId");
        int topK = parseIntArg(args, 2, DEFAULT_TOP_K, "topK");
        double alpha = parseDoubleArg(args, 3, DEFAULT_ALPHA, "alpha");

        Movie sampleMovie = requireMovie(movies, sampleMovieId);
        requireUser(ratings, sampleUser);
        HybridRecommender hybridRec = new HybridRecommender(contentRec, collabRec, alpha);

        System.out.printf("Demo parameters: userId=%d, movieId=%d, topK=%d, hybrid alpha=%.2f%n",
                sampleUser, sampleMovieId, topK, alpha);

        System.out.println("\nContent-based recommendations for " + sampleMovie.getTitle() + ":");
        for (Movie movie : contentRec.recommend(sampleMovie, topK)) {
            System.out.println("  " + movie);
        }

        System.out.println("\nCollaborative recommendations for User " + sampleUser + ":");
        for (int movieId : collabRec.recommend(sampleUser, topK)) {
            System.out.println("  " + movies.get(movieId));
        }

        System.out.println("\nHybrid recommendations for User " + sampleUser + " based on " + sampleMovie.getTitle() + ":");
        for (Movie movie : hybridRec.recommend(sampleMovie, sampleUser, topK)) {
            System.out.println("  " + movie);
        }
    }

    private static int parseIntArg(String[] args, int index, int defaultValue, String name) {
        if (args.length <= index) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(args[index]);
            if (value <= 0) {
                throw new IllegalArgumentException(name + " must be positive.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + name + ": " + args[index], e);
        }
    }

    private static double parseDoubleArg(String[] args, int index, double defaultValue, String name) {
        if (args.length <= index) {
            return defaultValue;
        }
        try {
            double value = Double.parseDouble(args[index]);
            if (value < 0 || value > 1) {
                throw new IllegalArgumentException(name + " must be between 0 and 1.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + name + ": " + args[index], e);
        }
    }

    private static Movie requireMovie(Map<Integer, Movie> movies, int movieId) {
        Movie movie = movies.get(movieId);
        if (movie == null) {
            throw new IllegalArgumentException("Movie id " + movieId + " was not found in data/u.item.");
        }
        return movie;
    }

    private static void requireUser(Map<Integer, Map<Integer, Integer>> ratings, int userId) {
        if (!ratings.containsKey(userId)) {
            throw new IllegalArgumentException("User id " + userId + " was not found in data/u.data.");
        }
    }
}
