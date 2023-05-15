import com.google.gson.JsonObject;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
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
@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/_dashboard/api/employeelogin")
public class EmployeeLoginServlet extends HttpServlet {
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
        JsonObject responseJsonObject = new JsonObject();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("recaptcha Response" + gRecaptchaResponse);

        try{
            VerifyRecaptcha.verify(gRecaptchaResponse);
        }catch (Exception e) {
            responseJsonObject.addProperty("status", "failed");
            responseJsonObject.addProperty("message", "recaptcha verificaton failed");
            out.write(responseJsonObject.toString());
            response.setStatus(200);
            out.close();
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        try(Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM employees WHERE email = ? and password = ?";
            PreparedStatement employeeStatement = conn.prepareStatement(query);
            employeeStatement.setString(1, email);
            employeeStatement.setString(2, password);
            ResultSet rs = employeeStatement.executeQuery();
            responseJsonObject.addProperty("status", "failed");
            responseJsonObject.addProperty("message", "user not found");
            while(rs.next()) {
                responseJsonObject.remove("status");
                responseJsonObject.remove("message");
                responseJsonObject.addProperty("status", "success");
                request.getSession().setAttribute("employee", rs.getString("fullname"));
                request.getSession().setAttribute("user", rs.getString("email"));
            }
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
