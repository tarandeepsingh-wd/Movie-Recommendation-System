import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Map<Integer, Movie> movies = DataLoader.loadMovies();
        Map<Integer, Map<Integer, Integer>> ratings = DataLoader.loadRatings();

        System.out.printf("Loaded %d movies and %d users from data/%n", movies.size(), ratings.size());

        ContentBasedRecommender contentRec = new ContentBasedRecommender(movies);
        CollaborativeRecommender collabRec = new CollaborativeRecommender(ratings);
        HybridRecommender hybridRec = new HybridRecommender(contentRec, collabRec, 0.55);

        Movie sampleMovie = movies.getOrDefault(1, movies.values().iterator().next());
        int sampleUser = 1;

        System.out.println("\nContent-based recommendations for " + sampleMovie.getTitle() + ":");
        for (Movie movie : contentRec.recommend(sampleMovie, 10)) {
            System.out.println("  " + movie);
        }

        System.out.println("\nCollaborative recommendations for User " + sampleUser + ":");
        for (int movieId : collabRec.recommend(sampleUser, 10)) {
            System.out.println("  " + movies.get(movieId));
        }

        System.out.println("\nHybrid recommendations for User " + sampleUser + " based on " + sampleMovie.getTitle() + ":");
        for (Movie movie : hybridRec.recommend(sampleMovie, sampleUser, 10)) {
            System.out.println("  " + movie);
        }
    }
}
