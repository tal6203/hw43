package org.example;

import java.sql.*;
import java.util.ArrayList;

public class DBAccess {
    private Connection connection;

    public DBAccess() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:C:\\project_java\\hw43\\hw43\\db2.db");
    }

    public ArrayList<Person> readFromPerson() throws ClassNotFoundException {

        ArrayList<Person> result = new ArrayList<>();

        try
        {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            //statement.executeUpdate("drop table if exists person");
            //statement.executeUpdate("create table person (id integer, name string)");
            //statement.executeUpdate("insert into person values(1, 'leo')");
            //statement.executeUpdate("insert into person values(2, 'yui')");
            ResultSet rs = statement.executeQuery("select * from person");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                Float height = rs.getFloat("height");
                Person p = new Person(id, name,address,height);
                result.add(p);
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }

        return result;
    }
}
