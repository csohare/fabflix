import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.google.gson.JsonArray;

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

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private DataSource dataSource;
    private boolean UserFound = false;

    public void init(ServletConfig config) {
        try{
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch(NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        JsonObject responseJsonObject = new JsonObject();
        try(Connection conn = dataSource.getConnection()) {
            String userQuery = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement statement = conn.prepareStatement(userQuery);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                UserFound = true;
                String queryPassword = rs.getString("password");
                if (!queryPassword.equals(password)) {

                    responseJsonObject.addProperty("message", "incorrect password");
                    responseJsonObject.addProperty("status", "failed");
                } else {
                    responseJsonObject.addProperty("status", "success");
                    request.getSession().setAttribute("user", email);
                }
            }
            if(!UserFound) {
                responseJsonObject.addProperty("status", "failed");
                responseJsonObject.addProperty("message", "account with that email does not exist");
            }

            rs.close();
            out.write(responseJsonObject.toString());
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