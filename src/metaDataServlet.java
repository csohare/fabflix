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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.PreparedStatement;



@WebServlet(name = "metaDataServlet", urlPatterns = "/_dashboard/api/metadata")
public class metaDataServlet extends HttpServlet {
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

        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[] {"TABLE"});

            while(tables.next()) {
                String tablename = tables.getString("TABLE_NAME");
                if(tablename.equals("sys_config"))  {continue;}

                JsonObject tableName = new JsonObject();
                JsonArray tableData = new JsonArray();

                tableName.addProperty("tableName", tablename);
                ResultSet columns = metaData.getColumns(null, null, tablename, null);
                while(columns.next()) {
                    JsonObject data = new JsonObject();
                    data.addProperty("columnName", columns.getString("COLUMN_NAME"));
                    data.addProperty("dataType", columns.getString("TYPE_NAME"));
                    tableData.add(data);
                }
                tableName.add("variables", tableData);
                jsonArray.add(tableName);
            }

            request.getServletContext().log("getting " + jsonArray.size() + " results");
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
}
