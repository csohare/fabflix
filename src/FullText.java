import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.google.gson.JsonArray;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
@WebServlet(name = "FullText", urlPatterns = "/api/fulltext")
public class FullText extends HttpServlet {
    private DataSource dataSource;
    private boolean UserFound = false;

    public void init(ServletConfig config) {
        try{
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch(NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();
        String fullTextSearch = request.getParameter("query");
        fullTextSearch = genString(fullTextSearch);
        System.out.println(fullTextSearch);

        try(Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM movies WHERE MATCH(title) AGAINST(? IN BOOLEAN MODE) LIMIT 10";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, fullTextSearch);
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                JsonObject outer = new JsonObject();
                JsonObject inner = new JsonObject();
                outer.addProperty("value", rs.getString("title") + " (" + rs.getString("year") + ")");
                inner.addProperty("id", rs.getString("id"));
                inner.addProperty("year", rs.getString("year"));
                inner.addProperty("director", rs.getString("director"));
                outer.add("data", inner);
                jsonArray.add(outer);
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


    }
    private String genString(String query) {
        String[] tokens = query.split(" ");
        for(int i = 0; i < tokens.length; i++) {
            tokens[i] = "+" + tokens[i] + "*";
            System.out.println(tokens[i]);
        }
        return String.join(" ", tokens);
    }
}
