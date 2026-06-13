import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Movie {
    private final int id;
    private final String title;
    private final List<String> genres;

    public Movie(int id, String title, List<String> genres) {
        this.id = id;
        this.title = title;
        this.genres = Collections.unmodifiableList(new ArrayList<>(genres));
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getGenres() {
        return genres;
    }

    @Override
    public String toString() {
        return String.format("%d: %s [%s]", id, title, String.join(", ", genres));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return id == movie.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

