import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
        JsonArray jsonArray = new JsonArray();
        PrintWriter out = response.getWriter();


        try (Connection conn = dataSource.getConnection()) {
            String starQuery = "SELECT group_concat(starId) as starIds, group_concat(name) as starNames\n" +
                    "FROM\n" +
                    "(SELECT movieStars.starId, name, count(tmp.starId) as item_count\n" +
                    "FROM\n" +
                    "\t(SELECT *\n" +
                    "\t FROM stars_in_movies\n" +
                    "     WHERE movieId = ?) as movieStars\n" +
                    "     Left Join (SELECT starId FROM stars_in_movies) as tmp\n" +
                    "     ON movieStars.starId = tmp.starId, stars\n" +
                    "     WHERE id = movieStars.starId\n" +
                    "     GROUP by movieStars.starId, name\n" +
                    "     ORDER by item_Count desc\n" +
                    "     LIMIT 3\n" +
                    ") as top3Stars";
            ResultSet rs;
            ResultSet starSet;
            PreparedStatement statement;
            PreparedStatement starStatement = conn.prepareStatement(starQuery);
            String query = "";

                query = genQuery(request);
                statement = conn.prepareStatement(query);
                rs = statement.executeQuery();

                while(rs.next()) {
                    String starIds = "";
                    String starNames = "";
                    JsonObject jsonObject = new JsonObject();

                    String movieId = rs.getString("movieId");
                    String title = rs.getString("title");
                    String year = rs.getString("year");
                    String director = rs.getString("director");
                    String rating = rs.getString("rating");
                    String genreIds = rs.getString("genreIds");
                    String genreNames = rs.getString("genreNames");

                    starStatement.setString(1, movieId);
                    starSet = starStatement.executeQuery();
                    while(starSet.next()) {
                        starIds = starSet.getString("starIds");
                        starNames = starSet.getString("starNames");
                    }

                    jsonObject.addProperty("movieId", movieId);
                    jsonObject.addProperty("title", title);
                    jsonObject.addProperty("year", year);
                    jsonObject.addProperty("director", director);
                    jsonObject.addProperty("rating", rating);
                    jsonObject.addProperty("genreIds", genreIds);
                    jsonObject.addProperty("genreNames", genreNames);
                    jsonObject.addProperty("starIds", starIds);
                    jsonObject.addProperty("starNames", starNames);

                    jsonArray.add(jsonObject);
            }

            out.write(jsonArray.toString());
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

    protected String genQuery (HttpServletRequest request) throws IOException {
        String pageSize = "25";
        String pageOffset = "0";
        String query = "";
        PreparedStatement statement;
        if(request.getParameter("pageSize") != null) {pageSize = request.getParameter("pageSize");}
        if(request.getParameter("pageOffset") != null) {pageOffset = request.getParameter("pageOffset");}

        if(request.getParameter("movieGenre") != null) {
            String genreId = request.getParameter("movieGenre");
            query = "SELECT m.id as movieId, title, year, director, rating, group_concat(mG.id) as genreIds, group_concat(name) as genreNames\n" +
                    "FROM\n" +
                    "(SELECT m.id, title, year, director \n" +
                    "FROM genres_in_movies as gim,\n" +
                    "movies as m \n" +
                    "WHERE genreId = " + genreId + " and m.id = movieId \n";
        }
        if(request.getParameter("movieTitle") != null) {
            String movieTitle = request.getParameter("movieTitle");
            if(movieTitle.equals("*")) {
                query = "SELECT m.id as movieId, title, year, director, rating, group_concat(mG.id) as genreIds, group_concat(name) as genreNames\n" +
                        "FROM\n" +
                        "(SELECT m.id, title, year, director \n" +
                        "FROM movies as m \n" +
                        "WHERE title REGEXP '^[^a-zA-Z0-9]'\n";

            }
            else {
                if(request.getParameter("starName") != null) {
                    String starName = request.getParameter("starName");
                    query = "SELECT m.id as movieId, title, year, director, rating, group_concat(mG.id) as genreIds, group_concat(name) as genreNames\n" +
                            "FROM\n" +
                            "(SELECT m.id, title, year, director \n" +
                            "FROM movies as m, \n" +
                            "(SELECT movieId\n" +
                            "FROM stars, stars_in_movies as sim\n" +
                            "WHERE sim.starId = stars.id and name Like '%" + starName + "%') as starMovies\n"  +
                            "where m.id = starMovies.movieId and ";
                }
                else {
                    query = "SELECT m.id as movieId, title, year, director, rating, group_concat(mG.id) as genreIds, group_concat(name) as genreNames\n" +
                            "FROM\n" +
                            "(SELECT m.id, title, year, director \n" +
                            "FROM movies as m \n" +
                            "WHERE ";
                }

                if(movieTitle.length() > 1) {query += "title LIKE '%" + movieTitle + "%'";}
                else    {query += "title LIKE '" + movieTitle + "%'";}

                if(request.getParameter("director") != null) {
                    String director = request.getParameter("director");
                    query += " and director LIKE '%" + director + "%'";
                    String year = request.getParameter("year");
                    if(!(year.equals(""))) {
                        query += " and year LIKE " + year;
                    }
                }
            }
        }
        query += "\nLIMIT " + pageSize + " OFFSET " + pageOffset + ") as m\n" +
                  "LEFT JOIN (SELECT name, genres.id, movieId FROM genres_in_movies as gim, genres WHERE  gim.genreId = genres.id) as mG\n" +
                  "ON mG.movieId = m.id\n" +
                  "LEFT JOIN (SELECT movieId, rating FROM ratings) as r\n" +
                  "ON r.movieId = m.id\n" +
                  "GROUP BY m.id, title, year, director, rating";

        return query;
    }
}
