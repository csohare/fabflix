import java.util.ArrayList;

public class Movie {
    private String title;
    private String director;

    private ArrayList<String> genres;
    private int release;



    public Movie(String title, String director, int release, ArrayList<String> genres) {
        this.title = title;
        this.director = director;
        this.release = release;
        this.genres = genres;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDirector() {
        return this.director;
    }

    public int getRelease() {
        return this.release;
    }

    public ArrayList<String> getGenres(){
        return this.genres;
    }
}
