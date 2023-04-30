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



@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVemovieInfoionUID = 1L;

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

        // Retrieve parameter id from url request.
        String id = "'" + request.getParameter("id") + "'";

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        try (Connection conn = dataSource.getConnection()) {

            String movieQuery = "SELECT m.id as movieId, title, year, director, rating, group_concat(mG.id) as genreIds, group_concat(name) as genreNames\n" +
                    "FROM\n" +
                    "(SELECT m.id, title, year, director \n" +
                    "FROM movies as m\n" +
                    "WHERE id = " + id + ") as m\n" +
                    "LEFT JOIN (SELECT name, genres.id, movieId FROM genres_in_movies as gim, genres WHERE  gim.genreId = genres.id) as mG\n" +
                    "ON mG.movieId = m.id\n" +
                    "LEFT JOIN (SELECT movieId, rating FROM ratings) as r\n" +
                    "ON r.movieId = m.id";
            String starQuery = "SELECT group_concat(starId) as starIds, group_concat(name) as names\n" +
                    "FROM\n" +
                    "(SELECT movieStars.starId, name, count(tmp.starId) as item_count\n" +
                    "FROM\n" +
                    "(SELECT *\n" +
                    "FROM stars_in_movies\n" +
                    "     WHERE movieId = " + id + ")as movieStars\n" +
                    "     Left Join (SELECT starId FROM stars_in_movies) as tmp\n" +
                    "     ON movieStars.starId = tmp.starId, stars\n" +
                    "     WHERE id = movieStars.starId\n" +
                    "     GROUP by movieStars.starId, name\n" +
                    "     ORDER by item_Count desc\n" +
                    ") as stars\n";
            Statement statement = conn.createStatement();
            ResultSet movieInfo = statement.executeQuery(movieQuery);


            Statement starStatement = conn.createStatement();
            ResultSet starInfo = starStatement.executeQuery(starQuery);
            // id, title, year, starIds, starNames, genreIds, genreNames, rating

            JsonArray jsonArray = new JsonArray();

            while (movieInfo.next()) {
                String movie_id = movieInfo.getString("movieId");
                String movie_title = movieInfo.getString("title");
                String movie_year = movieInfo.getString("year");
                String movie_director = movieInfo.getString("director");
                String genre_ids = movieInfo.getString("genreIds");
                String movie_genre = movieInfo.getString("genreNames");
                String movie_rating = movieInfo.getString("rating");
                String starIds = "";
                String starNames = "";
                while(starInfo.next()) {
                    starIds = starInfo.getString("starIds");
                    starNames = starInfo.getString("names");
                }



                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movieId", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genreIds", genre_ids);
                jsonObject.addProperty("movie_genre", movie_genre);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_starNames", starNames);
                jsonObject.addProperty("movie_starIds", starIds);


                jsonArray.add(jsonObject);
            }
            movieInfo.close();
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