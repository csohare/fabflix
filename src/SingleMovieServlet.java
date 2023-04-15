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
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        try (Connection conn = dataSource.getConnection()) {

            Statement statement = conn.createStatement();
            String singleMovieQuery ="SELECT * FROM (SELECT * FROM movies WHERE movies.id = 'tt0498362') as movieInfo, (SELECT  group_concat(starId) as starIds , group_concat(starName) as starNames, genreIds, genreNames FROM (SELECT starId, name as starName FROM stars_in_movies as sim, stars WHERE sim.movieId = 'tt0498362' and stars.id = sim.starId) as movieStars, (SELECT group_concat(genreId) as genreIds, group_concat(name) as genreNames FROM genres_in_movies as gim, genres WHERE gim.movieId = 'tt0498362' and genres.id = gim.genreId) as genreStars GROUP BY genreIds, genreNames) as mstarGenre";

            ResultSet movieInfo = statement.executeQuery(singleMovieQuery);
            JsonArray jsonArray = new JsonArray();

            while (movieInfo.next()) {
                
                String movie_id = movieInfo.getString("id");
                String movie_title = movieInfo.getString("title");
                String movie_year = movieInfo.getString("year");
                String movie_director = movieInfo.getString("director");
                String movie_rating = movieInfo.getString("starIds");
                String movie_starNames = movieInfo.getString("starNames");
                String movie_genreNames = 

               

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
