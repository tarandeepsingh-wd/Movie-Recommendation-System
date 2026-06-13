import java.io.IOException;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DataLoader {

    private static final Path DATA_DIR = Paths.get("data");

    public static Map<Integer, Movie> loadMovies() {
        try {
            Map<Integer, String> genres = loadGenres();
            Path itemFile = DATA_DIR.resolve("u.item");
            Map<Integer, Movie> movies = new LinkedHashMap<>();

            try (BufferedReader reader = Files.newBufferedReader(itemFile, StandardCharsets.ISO_8859_1)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }

                    String[] parts = line.split("\\|", -1);
                    if (parts.length < 6) {
                        continue;
                    }

                    int movieId = Integer.parseInt(parts[0].trim());
                    String title = parts[1].trim();
                    List<String> movieGenres = new ArrayList<>();

                    for (int i = 5; i < parts.length; i++) {
                        if ("1".equals(parts[i]) && genres.containsKey(i - 5)) {
                            movieGenres.add(genres.get(i - 5));
                        }
                    }

                    movies.put(movieId, new Movie(movieId, title, movieGenres));
                }
            }

            return movies;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load movies from data/u.item", e);
        }
    }

    public static Map<Integer, Map<Integer, Integer>> loadRatings() {
        try {
            Path dataFile = DATA_DIR.resolve("u.data");
            Map<Integer, Map<Integer, Integer>> ratings = new LinkedHashMap<>();

            try (BufferedReader reader = Files.newBufferedReader(dataFile, StandardCharsets.ISO_8859_1)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }

                    String[] parts = line.split("\\t");
                    if (parts.length < 3) {
                        continue;
                    }

                    int userId = Integer.parseInt(parts[0].trim());
                    int movieId = Integer.parseInt(parts[1].trim());
                    int rating = Integer.parseInt(parts[2].trim());

                    ratings.computeIfAbsent(userId, k -> new HashMap<>()).put(movieId, rating);
                }
            }

            return ratings;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load ratings from data/u.data", e);
        }
    }

    private static Map<Integer, String> loadGenres() throws IOException {
        Path genreFile = DATA_DIR.resolve("u.genre");
        Map<Integer, String> genres = new TreeMap<>();

        for (String line : Files.readAllLines(genreFile, StandardCharsets.ISO_8859_1)) {
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split("\\|");
            if (parts.length < 2) {
                continue;
            }
            try {
                int index = Integer.parseInt(parts[1].trim());
                genres.put(index, parts[0].trim());
            } catch (NumberFormatException ignored) {
            }
        }

        return genres;
    }
}
