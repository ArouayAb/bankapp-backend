package authentication.bank.client.Helpers;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Helper class abstracting the database communication layer
public abstract class DataProviderHelper<T> {

    final Class<T> typeT;
    // JDBC Communication with DB
    private Connection connection = null;
    public static final String DB_URL = "jdbc:mysql://localhost:3306/restfulApp";

    // Getting the class name using this helper class in order to interact with the according database table directly
    public DataProviderHelper(Class<T> typeT) {
        this.typeT = typeT;
    }

    public boolean isConnectionOpen() {
        return connection != null;
    }

    public Connection openConnection() throws
            SQLException,
            ClassNotFoundException
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(DB_URL, "root", "1988");
        return connection;
    }

    public void closeConnection() throws
            SQLException
    {
        if (isConnectionOpen()){
            connection.close();
        }
    }

    // Transforming Pojos to Lists to simplify data manipulation later
    public List<T> convertPojoToList(ResultSet resultSet) throws
            SQLException,
            IllegalAccessException,
            InstantiationException
    {
        // Using Java reflection in order to iterate over class attributes
        List<T> listObjects = new ArrayList<>();
        Field[] fields = this.typeT.getDeclaredFields();
        while (resultSet.next()) {
            T entity = this.typeT.newInstance();
            int index = 1;
            for (Field field : fields) {
                field.setAccessible(true);
                field.set(entity, resultSet.getObject(index, field.getType()));
                index++;
            }
            listObjects.add(entity);
        }
        return listObjects;
    }

    // Executing queries having a return value
    public List<T> executeQuery(String query) throws
            SQLException,
            ClassNotFoundException,
            IllegalAccessException,
            InstantiationException
    {
        Connection connection = openConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        // Transforming results from Pojo to list for simple manipulation later
        List<T> listRows = convertPojoToList(resultSet);
        closeConnection();
        return listRows;
    }

    // Executing queries not having a return value
    public void executeUpdateQuery(String query) throws
            SQLException,
            ClassNotFoundException
    {
        Connection connection = openConnection();
        Statement statement = connection.createStatement();
        statement.execute(query);

        closeConnection();
    }

}
