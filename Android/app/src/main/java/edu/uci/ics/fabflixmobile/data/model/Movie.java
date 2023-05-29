package edu.uci.ics.fabflixmobile.data.model;
import java.util.ArrayList;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String id;
    private final String name;
    private final String year;

    private final String rating;

    private final String genres;
    private final String stars;
    private final String director;




    public Movie(String id, String name, String year, String rating, String genres, String stars, String director) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.rating = rating;
        this.genres = genres;
        this.stars = stars;
        this.director = director;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public String getGenres() {
        return genres;
    }

    public String getStars() {
        return stars;
    }

    public String getYear() {
        return year;
    }
    public String getId() {
        return id;
    }
    public String getDirector(){
        return director;
    }
}