import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called Statop20Servlet, which maps to url "/api/statop20"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/MovieList")
public class MovieListServlet extends HttpServlet {
    private static final long serialVetop20ionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {

            Statement statement = conn.createStatement();
            Statement genreStatement = conn.createStatement();
            Statement starStatement = conn.createStatement();

            String top20Query = "SELECT id, title, year, director, rating " +
                    "FROM movies, ratings WHERE movies.id = ratings.movieId ORDER BY rating DESC LIMIT 20";

            ResultSet top20 = statement.executeQuery(top20Query);
            JsonArray jsonArray = new JsonArray();



            ResultSet genres;
            ResultSet stars;
            while (top20.next()) {
                // GENERATE 3 GENRES FOR EACH MOVIE
                String genreString = "";
                String genreQuery = "SELECT group_concat(name) as names FROM (SELECT genreId FROM genres_in_movies " +
                        "WHERE movieId = \"" + top20.getString("id") +
                        "\" LIMIT 3) as mG, genres WHERE id = genreId";
                genres = genreStatement.executeQuery(genreQuery);
                genres.next();

                //GENERATE 3 STARS FOR EACH MOVIE
                String starString = "";
                String starQuery = "SELECT group_concat(name) as names, group_concat(starId) as ids FROM (SELECT starId FROM stars_in_movies as sim " +
                        "WHERE movieId = \"" + top20.getString("id") + "\" LIMIT 3) as mS, " +
                        "stars WHERE starId = id";
                stars = starStatement.executeQuery(starQuery);
                stars.next();


                String movie_id = top20.getString("id");
                String movie_title = top20.getString("title");
                String movie_year = top20.getString("year");
                String movie_director = top20.getString("director");
                String movie_rating = top20.getString("rating");
                String movie_genre = genres.getString("names");
                String movie_stars = stars.getString("names");
                String star_ids = stars.getString("ids");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genre", movie_genre);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_stars", movie_stars);
                jsonObject.addProperty("star_ids", star_ids);


                jsonArray.add(jsonObject);
            }
            top20.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
