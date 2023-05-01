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
import java.util.HashMap;
import java.util.Date;

@WebServlet(name = "CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
    private DataSource dataSource;
    private boolean UserFound = false;

    public void init(ServletConfig config) {
        try{
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch(NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        JsonObject jsonObject = new JsonObject();
        HashMap<String, Integer> map = (HashMap<String, Integer>) request.getSession().getAttribute("cart");
        int total = 0;
        for (String key : map.keySet()) {
            int quantity = map.get(key);
            quantity *= 8;
            total += quantity;
        }
        String value = Integer.toString(total);
        jsonObject.addProperty("total", value);
        out.write(jsonObject.toString());
        response.setStatus(200);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String cNumber = request.getParameter("cNum");
        String message = "";
        boolean cardFound = false;

        JsonObject responseJsonObject = new JsonObject();


        try(Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM creditcards WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, cNumber);
            ResultSet rs = statement.executeQuery();
            if(rs.next()) {
                cardFound = true;
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String expiration = rs.getString("expiration");
                if(!(firstName.equals(request.getParameter("fName")))) {
                    responseJsonObject.addProperty("status", "failed");
                    message = "First name incorrect";
                }
                else if(!(lastName.equals(request.getParameter("lName")))) {
                    responseJsonObject.addProperty("status", "failed");
                    message = "Last name incorrect";
                }
                else if(!(expiration.equals(request.getParameter("eDate")))) {
                    responseJsonObject.addProperty("status", "failed");
                    message = "expiration incorrect";
                }
                else {
                    String customerQ = "SELECT id FROM customers WHERE ccId = ?";
                    String insertQ = "INSERT INTO sales(customerId, movieId, saleDate) VALUES(?, ?, ?)";
                    PreparedStatement saleStatement = conn.prepareStatement(insertQ);
                    PreparedStatement CustomerStatement = conn.prepareStatement(customerQ);
                    CustomerStatement.setString(1, cNumber);
                    ResultSet Id = CustomerStatement.executeQuery();
                    String CustomerId = "";
                    while(Id.next()) {
                        CustomerId = Id.getString("id");
                    }
                    saleStatement.setString(1, CustomerId);
                    java.util.Date date = new java.util.Date();
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                    responseJsonObject.addProperty("status", "success");
                    HashMap<String, Integer> map = (HashMap<String, Integer>) request.getSession().getAttribute("cart");
                    for(String key : map.keySet()) {
                        int value = map.get(key);
                        saleStatement.setString(2, key);
                        saleStatement.setDate(3, sqlDate);
                        for(int i = 0; i < value; ++i) {
                            saleStatement.executeUpdate();
                        }
                        map.remove(key);
                    }
                }
            }
            if(!message.equals("")) {responseJsonObject.addProperty("message", message);}

            if(!cardFound) {
                responseJsonObject.addProperty("status", "failed");
                responseJsonObject.addProperty("message", "No card with that number exists");
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
