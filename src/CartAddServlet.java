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
import java.util.HashMap;



@WebServlet(name = "CartAddServlet", urlPatterns = "/api/CartAdd")
public class CartAddServlet extends HttpServlet {

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
        JsonObject myjsonObject = new JsonObject();

        if(request.getParameter("movieId") == null) {
            myjsonObject.addProperty("status", "failed");
            myjsonObject.addProperty("message", "no movieId listed");
        }
        else {
            HashMap<String, Integer> map = (HashMap<String, Integer>) request.getSession().getAttribute("cart");
            String movieId = request.getParameter("movieId");
            if (map.containsKey(movieId)) {
                int prev = map.get(movieId);
                int increment = prev + 1;
                map.put(movieId, increment);
                myjsonObject.addProperty("status", "success movie already exists");
            }
            else {map.put(movieId, 1); myjsonObject.addProperty("status", "success first add");}
        }
        String queryString = request.getQueryString();
        myjsonObject.addProperty("queryString",queryString);
            out.write(myjsonObject.toString());
            response.setStatus(200);
    }
}
