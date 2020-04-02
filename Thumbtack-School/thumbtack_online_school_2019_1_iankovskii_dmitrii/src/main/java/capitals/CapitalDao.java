package capitals;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CapitalDao {

    private static Connection connection;
    private static final String USER = "test";
    private static final String PASSWORD = "test";
    private static final String URL = "jdbc:mysql://localhost:3306/capitals?useUnicode=yes&characterEncoding=UTF8&useSSL=false&serverTimezone=Asia/Omsk";

    public CapitalDao(){
        createConnection();
    }

    private boolean createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            return false;
        }
    }

    private Connection getConnection() {
        return connection;
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            // can only log
        }
    }

    public List<String> getCapitals(){
        String query = "Select city_name from city";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(query, Statement.NO_GENERATED_KEYS);
            ResultSet set = stmt.executeQuery();
            List<String > capitals = new ArrayList<>();
            while (set.next()){
                capitals.add(set.getString("city_name"));
            }
            return capitals;
        }catch (SQLException ex){
            return null;
        }finally {
            closeConnection();
        }
    }
}
