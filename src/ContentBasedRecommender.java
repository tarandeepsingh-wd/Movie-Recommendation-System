import java.util.*;
import java.util.stream.Collectors;

public class ContentBasedRecommender {

    private final Map<Integer, Movie> movies;
    private final Map<String, Integer> genreIndex;
    private final Map<Integer, double[]> movieVectors;

    public ContentBasedRecommender(Map<Integer, Movie> movies) {
        this.movies = new HashMap<>(movies);
        this.genreIndex = buildGenreIndex(movies.values());
        this.movieVectors = buildMovieVectors(movies);
    }

    private Map<String, Integer> buildGenreIndex(Collection<Movie> movies) {
        Map<String, Integer> index = new HashMap<>();
        for (Movie movie : movies) {
            for (String genre : movie.getGenres()) {
                if (!index.containsKey(genre)) {
                    index.put(genre, index.size());
                }
            }
        }
        return index;
    }

    private Map<Integer, double[]> buildMovieVectors(Map<Integer, Movie> movies) {
        Map<Integer, double[]> vectors = new HashMap<>();
        for (Movie movie : movies.values()) {
            vectors.put(movie.getId(), buildVector(movie));
        }
        return vectors;
    }

    private double[] buildVector(Movie movie) {
        double[] vector = new double[genreIndex.size()];
        for (String genre : movie.getGenres()) {
            Integer index = genreIndex.get(genre);
            if (index != null) {
                vector[index] = 1;
            }
        }
        return vector;
    }

    public Map<Integer, Double> recommendWithScores(Movie likedMovie, int limit) {
        if (limit <= 0) {
            return Collections.emptyMap();
        }

        double[] baseVector = movieVectors.getOrDefault(likedMovie.getId(), buildVector(likedMovie));

        return movieVectors.entrySet().stream()
                .filter(entry -> entry.getKey() != likedMovie.getId())
                .map(entry -> Map.entry(entry.getKey(), Similarity.cosine(baseVector, entry.getValue())))
                .filter(entry -> entry.getValue() > 0)
                .sorted(this::compareScoreEntries)
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    public List<Movie> recommend(Movie likedMovie, int topK) {
        return recommendWithScores(likedMovie, topK).keySet().stream()
                .map(movies::get)
                .collect(Collectors.toList());
    }

    public Movie getMovie(int id) {
        return movies.get(id);
    }

    private int compareScoreEntries(Map.Entry<Integer, Double> left,
                                    Map.Entry<Integer, Double> right) {
        int scoreCompare = Double.compare(right.getValue(), left.getValue());
        if (scoreCompare != 0) {
            return scoreCompare;
        }

        Movie leftMovie = movies.get(left.getKey());
        Movie rightMovie = movies.get(right.getKey());
        int titleCompare = leftMovie.getTitle().compareTo(rightMovie.getTitle());
        if (titleCompare != 0) {
            return titleCompare;
        }
        return Integer.compare(left.getKey(), right.getKey());
    }
}
