import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import java.io.IOException;
import java.util.*;
import java.sql.*;
import java.io.FileWriter;


public class DomParser{

    Document dom;


    Connection conn;

    Hashtable<String, String> table;

    int DuplicateEntries = 0;
    int Inconsistencies = 0;

    FileWriter fw;



    public DomParser(Connection conn) throws Exception{
        this.conn = conn;
    }
    public int[] run(String filename) throws Exception{
        int[] numRows = null;
        int genreId = 0;
        // parse the xml file and get the dom object
        parseXmlFile(filename);
        if(filename.equals("casts124.xml")) {
            DuplicateEntries = 0;
            Inconsistencies = 0;
            int MoviesNotFound = 0;
            int StarsNotFound = 0;
            this.fw = new FileWriter("castInserts.txt");
            ArrayList<String[]> arr = new ArrayList<>();
            this.table = new Hashtable<>();
            Hashtable<String, String> stars = new Hashtable<>();
            Set<String> simRelations = new HashSet<>();
            PreparedStatement movieQuery = conn.prepareStatement("SELECT id, title, director FROM movies");
            PreparedStatement starQuery = conn.prepareStatement("SELECT id, name FROM stars");
            PreparedStatement simQuery = conn.prepareStatement("SELECT * FROM  stars_in_movies");
            ResultSet movieSet = movieQuery.executeQuery();
            ResultSet starSet = starQuery.executeQuery();
            ResultSet simSet = simQuery.executeQuery();
            while(simSet.next()) {
                simRelations.add(simSet.getString("starId") + simSet.getString("movieId"));
            }
            while(movieSet.next()) {
                this.table.put(movieSet.getString("director") + movieSet.getString("title"), movieSet.getString("id"));
            }
            while(starSet.next()) {
                stars.put(starSet.getString("name"), starSet.getString("id"));
            }
            PreparedStatement simInsert = conn.prepareStatement("INSERT INTO stars_in_movies VALUES (?, ?)");
            parseCasts(arr, MoviesNotFound, StarsNotFound, stars);
            for(int i = 0; i < arr.size(); ++i) {
                String[] ids = arr.get(i);
                if(simRelations.contains(ids[0] + ids[1]))  {fw.append("DUPLICATE INSERTION " + ids[0] + "-" + ids[1] + "\n"); continue;}
                simInsert.setString(1, ids[0]);
                simInsert.setString(2, ids[1]);
                simRelations.add(ids[0] + ids[1]);
                simInsert.addBatch();
            }
            numRows = simInsert.executeBatch();
            conn.commit();
            System.out.println("INSERTING " + numRows.length + " SIM relations");



        }
        if(filename.equals("mains243.xml")) {
            String id = "";
            DuplicateEntries = 0;
            Inconsistencies = 0;
            this.fw = new FileWriter("movieInserts.txt");
            ArrayList<Movie> arr = new ArrayList<>();

            this.table = new Hashtable<>();
            Hashtable<String, Integer> genres = new Hashtable<>();

            PreparedStatement statement = conn.prepareStatement("SELECT title , id FROM movies");
            PreparedStatement genreStatement = conn.prepareStatement("SELECT name, id from genres");
            PreparedStatement movieIdStatement = conn.prepareStatement("SELECT max(id) from movies");
            ResultSet movieId = movieIdStatement.executeQuery();
            ResultSet genre = genreStatement.executeQuery();
            ResultSet movies = statement.executeQuery();
            while(movies.next()) {

                this.table.put(movies.getString("title"), movies.getString("id"));
            }
            while(genre.next()) {
                genres.put(genre.getString("name"), genre.getInt("id"));
            }
            while(movieId.next()) {
                id = movieId.getString("max(id)");
            }
            genreId = genres.size() + 1;
            parseMovies(arr);
            PreparedStatement movieInsert = conn.prepareStatement("INSERT INTO movies VALUES (?, ?, ?, ?)");
            PreparedStatement genreInsert = conn.prepareStatement("INSERT INTO genres VALUES(?, ?)");
            PreparedStatement gimInsert = conn.prepareStatement("INSERT INTO genres_in_movies VALUES(?, ?)");
            for(int i = 0; i < arr.size(); i++) {
                id = nextMovieId(id);
                Movie movie = arr.get(i);
                movieInsert.setString(1, id);
                movieInsert.setString(2, movie.getTitle());
                movieInsert.setInt(3, movie.getRelease());
                movieInsert.setString(4, movie.getDirector());
                movieInsert.addBatch();
                ArrayList<String> genreList = movie.getGenres();
                for(int j = 0; j < genreList.size(); j++) {
                    String gName = genreList.get(j);
                    int tmpId = 0;
                    if(genres.containsKey(gName))    {tmpId = genres.get(gName);}
                    else {
                        genres.put(gName, genreId);
                        genreInsert.setInt(1,genreId);
                        genreInsert.setString(2, gName);
                        genreInsert.addBatch();
                        tmpId = genreId;
                        genreId++;
                    }
                    gimInsert.setInt(1, tmpId);
                    gimInsert.setString(2, id);
                    gimInsert.addBatch();
                }
            }
            numRows = movieInsert.executeBatch();
            conn.commit();
            System.out.println("INSERTING " + numRows.length + " Movies");
            numRows = genreInsert.executeBatch();
            conn.commit();
            System.out.println("INSERTING " + numRows.length + " Genres");
            numRows = gimInsert.executeBatch();
            conn.commit();
            System.out.println("INSERTING " + numRows.length + " GIM relations");
            System.out.println("DUPLICATE MOVIES " + DuplicateEntries);
            System.out.println("INCONSISTENT MOVIE DATA " + Inconsistencies);

        }
        if(filename.equals("actors63.xml")) {
            DuplicateEntries = 0;
            this.fw = new FileWriter("StarInserts.txt");
            ArrayList<Actor> arr = new ArrayList<>();
            this.table = new Hashtable<>();
            PreparedStatement statement = conn.prepareStatement("SELECT id, name FROM stars");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                this.table.put(rs.getString("name"), rs.getString("id"));
            }
            PreparedStatement idStatement = conn.prepareStatement("SELECT max(id) FROM stars");
            ResultSet maxId = idStatement.executeQuery();
            String id = "";
            while (maxId.next()) {
                id = maxId.getString("max(id)");
            }
            parseActors(arr);
            PreparedStatement actorInsert = conn.prepareStatement("INSERT INTO  stars (id, name, birthYear) VALUES (?, ?, ?)");
            for (int i = 0; i < arr.size(); ++i) {
                id = nextStarId(id);
                Actor actor = arr.get(i);
                actorInsert.setString(1, id);
                actorInsert.setString(2, actor.getName());
                if (actor.getDob() == -1) {
                    actorInsert.setNull(3, java.sql.Types.INTEGER);
                } else {
                    actorInsert.setInt(3, actor.getDob());
                }


                actorInsert.addBatch();
            }
            numRows = actorInsert.executeBatch();
            conn.commit();
            System.out.println("INSERTING " + numRows.length + " STARS");
            System.out.println("DUPLICATE STARS " + DuplicateEntries);
            maxId.close();
            rs.close();
            actorInsert.close();
        }
        fw.flush();
        return numRows;

    }

    private String nextStarId(String currentId) {
        String numbers = currentId.substring(2);
        int incremented = Integer.parseInt(numbers) + 1;
        return "nm" + incremented;
    }


    private String nextMovieId(String currentId) {
        String numbers = currentId.substring(2);
        int incremented = Integer.parseInt(numbers) + 1;
        numbers = Integer.toString(incremented);
        while(numbers.length() < 7 ) {
            numbers = "0" + numbers;
        }
        return "tt" + numbers;

    }
    private void parseXmlFile(String filename) {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse(filename);

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseCasts(ArrayList<String[]> cast, int MoviesNotFound, int StarsNotFound, Hashtable<String, String> stars) throws Exception{
        Element documentElement = dom.getDocumentElement();
        NodeList nodeList = documentElement.getElementsByTagName("dirfilms");
        for(int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            String directorName = getTextValue(element, "is");
            if(directorName == null)    {fw.append("NULL DIRECTOR " + element.getNodeName()); Inconsistencies++; continue;}
            NodeList movies = element.getElementsByTagName("m");
            for(int j = 0; j < movies.getLength(); j++) {
                Element filmElement = (Element) movies.item(j);
                String movieName = getTextValue(filmElement, "t");
                String actorName = getTextValue(filmElement, "a");
                if(!stars.containsKey(actorName))   {fw.append("STAR NOT FOUND " + actorName + "\n"); StarsNotFound++; continue;}
                else if(!this.table.containsKey(directorName + movieName))    {fw.append("MOVIE NOT FOUND " + movieName + " " + directorName + "\n"); MoviesNotFound++; continue;}
                else {
                    String[] ids = new String[2];
                    ids[0] = stars.get(actorName);
                    ids[1] = table.get(directorName + movieName);
                    cast.add(ids);
                }

            }
        }
        System.out.println("MOVIES NOT FOUND " + MoviesNotFound);
        System.out.println("STARS NOT FOUND " + StarsNotFound);
    }
    private void parseMovies(ArrayList<Movie> movies) throws Exception{
        Element documentElement = dom.getDocumentElement();
        NodeList nodeList = documentElement.getElementsByTagName("directorfilms");
        for(int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            String directorName = getTextValue(element, "dirname");
            if(directorName == null)    {fw.append("null director" + element.getNodeName() + "\n"); Inconsistencies++; continue;}
            NodeList filmList = element.getElementsByTagName("film");
            for(int j = 0; j < filmList.getLength(); j++) {
                Element film = (Element) filmList.item(j);
                Movie movie = parseMovie(film, directorName);
                if(movie != null)   {movies.add(movie);}
            }
        }
    }

    private Movie parseMovie(Element element, String director) throws Exception{
        ArrayList<String> genres = new ArrayList<>();
        String title = getTextValue(element, "t");
        if(title == null)   {fw.append("null title " + element.getNodeName() + "\n"); Inconsistencies++; return null;}
        int year = getIntValue(element, "year");
        if(year == -1)  {fw.append("null release" + element.getNodeName() + "\n"); Inconsistencies++; return null;}
        if(this.table.containsKey(title))    {fw.append("duplicate movie " + element.getNodeName() + " " + this.table.get(title) + "\n"); DuplicateEntries++; return null;}
        NodeList catList = element.getElementsByTagName("cats");
        for(int j = 0; j < catList.getLength(); j++) {
            Element genreElement = (Element) catList.item(j);
            String cat = getTextValue(genreElement, "cat");
            if(cat != null) {genres.add(cat);}
        }
        Movie movie = new Movie(title, director, year, genres);
        return movie;
    }
    private void parseActors(ArrayList<Actor> arr) throws Exception{
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("actor");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            Actor actor = parseActor(element);
            if(actor == null)   {continue;}
            arr.add(actor);

        }
    }

    /**
     * It takes an employee Element, reads the values in, creates
     * an Employee object for return
     */
    private Actor parseActor(Element element) throws Exception{
        String name = getTextValue(element, "stagename");
        if(this.table.containsKey(name))    {
            DuplicateEntries++;
            fw.append("DUPLICATE " + element.getNodeName() + " " + name + "\n");
            return null;
        }
        int dob = getIntValue(element, "dob");
        return new Actor(name, dob);

    }

    /**
     * It takes an XML element and the tag name, look for the tag and get
     * the text content
     * i.e for <Employee><Name>John</Name></Employee> xml snippet if
     * the Element points to employee node and tagName is name it will return John
     */
    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            // here we expect only one <Name> would present in the <Employee>
            try {
                textVal = nodeList.item(0).getFirstChild().getNodeValue();
            }
            catch (Exception e) {
                textVal = "-1";
                return textVal;
            }
        }
        return textVal;
    }

    /**
     * Calls getTextValue and returns a int value
     */
    private int getIntValue(Element ele, String tagName) throws Exception{
        // in production application you would catch the exception
        try {
            return Integer.parseInt(getTextValue(ele, tagName));
        }
        catch (Exception e) {
            return -1;
        }
    }



    public static void main(String[] args) throws Exception{
        // create an instance
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false",
                "mytestuser", "My6$Password");
        if(conn != null) {
            System.out.println("CONNECTION ESTABLISHED");
        }
        conn.setAutoCommit(false);
        DomParser Parser= new DomParser(conn);

        // call run example
        Parser.run("actors63.xml");
        Parser.run("mains243.xml");
        Parser.run("casts124.xml");

    }

}
