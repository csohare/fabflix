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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;



@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shoppingCart")
public class ShoppingCartServlet extends HttpServlet {

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
        JsonArray jsonArray = new JsonArray();

        try (Connection conn = dataSource.getConnection()) {
            HashMap<String, Integer> map = (HashMap<String, Integer>) request.getSession().getAttribute("cart");
            String query = "SELECT title FROM movies WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet title;
            for (String key : map.keySet()) {
                JsonObject jsonObject = new JsonObject();
                statement.setString(1, key);
                title = statement.executeQuery();
                while(title.next()) {
                    jsonObject.addProperty("title", title.getString("title"));
                }
                int quantity = map.get(key);
                jsonObject.addProperty("id", key);
                jsonObject.addProperty("quantity", quantity);
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
    }
}
