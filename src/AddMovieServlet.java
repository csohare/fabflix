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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.Callable;


@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/api/addmovie")
public class AddMovieServlet extends HttpServlet {

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();
        JsonObject returnObject = new JsonObject();

        String movieTitle = request.getParameter("inputTitle");
        String movieYear = request.getParameter("inputRelease");
        String movieDirector = request.getParameter("inputDirector");
        String starName = request.getParameter("inputName");
        String starBirth = request.getParameter("inputBirth");
        String genreName = request.getParameter("inputGenre");
        int birthYear;
        int release;

        try (Connection conn = dataSource.getConnection()) {
            CallableStatement statement = conn.prepareCall("CALL InsertMovie(?, ?, ?, ?, ?, ?)");
            if(starBirth.equals(""))   {birthYear = -1;}
            else    {birthYear = Integer.parseInt(starBirth);}
            release = Integer.parseInt(movieYear);

            statement.setString(1, movieTitle);
            statement.setInt(2, release);
            statement.setString(3, movieDirector);
            statement.setString(4, starName);
            statement.setInt(5, birthYear);
            statement.setString(6, genreName);
            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                returnObject.addProperty("movieId", rs.getString("movieId"));
                if(rs.getString("movieId").equals("-1")) {
                    break;
                }
                returnObject.addProperty("starId", rs.getString("starId"));
                returnObject.addProperty("genreId", rs.getString("genreId"));
            }
            out.write(returnObject.toString());
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
