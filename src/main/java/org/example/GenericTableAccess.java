package org.example;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GenericTableAccess<T> {

    private Connection connection;

    private Class theclass;

    private String table_name;

    public GenericTableAccess(String db, Class theclass) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(db);
        this.theclass = theclass;

        for (var field : theclass.getDeclaredFields()) {
//            System.out.println(field.getName());

            DBFieldTableName dbFieldTableName = field.getAnnotation(DBFieldTableName.class);
            if (dbFieldTableName != null) {
                table_name = dbFieldTableName.table_name();
            }
        }
    }

    public ArrayList<T> readFromTable() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {

        ArrayList<T> result = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            ResultSet rs = statement.executeQuery("select * from " + table_name);

            HashMap<Class, IGetField> getFromDB = new HashMap<>();
            getFromDB.put(int.class, (String column) -> rs.getInt(column));
            getFromDB.put(String.class, (String column) -> rs.getString(column));
            getFromDB.put(Float.class,(String column) -> rs.getFloat(column));

            while (rs.next()) {

                Constructor<T> ctors = theclass.getDeclaredConstructor();
                T item = ctors.newInstance();

                for (var field : theclass.getDeclaredFields()) {
                    System.out.println(field.getName());

                    DBField dbField = field.getAnnotation(DBField.class);
                    if (dbField != null) {
                        System.out.println(dbField);
                        field.set(item,
                                getFromDB.get(dbField.type()).
                                        getData(dbField.column_name()));
                    }
                }

                result.add(item);
            }
        } catch (SQLException | InvocationTargetException | NoSuchMethodException e) {
            System.out.println(e);
        }

        return result;
    }

public void createTable() throws SQLException {
    Statement statement = connection.createStatement();
    statement.setQueryTimeout(30);

    DBField primaryKeyDBField = null;
    for(var field: theclass.getDeclaredFields()) {
        DBField dbField = field.getAnnotation(DBField.class);
        if (dbField != null) {
            if (dbField.isPrimaryKey()) {
                System.out.println("found primary key");
                primaryKeyDBField = dbField;
                break;
            }
        }
    }
    HashMap<Class, String> mapJavaFieldToSql = new HashMap<>();
    mapJavaFieldToSql.put(int.class, "integer");
    mapJavaFieldToSql.put(String.class, "string");
    mapJavaFieldToSql.put(Float.class,"float");

    String query = "create table " + table_name + " (";
    if (primaryKeyDBField != null) {
        query += primaryKeyDBField.column_name() +" "+
                mapJavaFieldToSql.get(primaryKeyDBField.type()) + " PRIMARY KEY " + ",";
    }

    for(var field: theclass.getDeclaredFields()) {
        DBField dbField = field.getAnnotation(DBField.class);
        if (dbField != null && dbField != primaryKeyDBField) {
            query += dbField.column_name() + " " + mapJavaFieldToSql.get(dbField.type()) + ",";
        }
    }
    query = query.substring(0,query.lastIndexOf(","));
    query += ")";
    System.out.println(query);
    statement.executeUpdate(query);
}


public void getBYId(int id) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
    Statement statement = connection.createStatement();
    statement.setQueryTimeout(30);
    ResultSet rs = statement.executeQuery("SELECT * FROM " +table_name+" WHERE id = "+String.valueOf(id));
    HashMap<Class, IGetField> getFromDB = new HashMap<>();
    getFromDB.put(int.class, (String column) -> rs.getInt(column));
    getFromDB.put(String.class, (String column) -> rs.getString(column));
    getFromDB.put(Float.class,(String column) -> rs.getFloat(column));
    ArrayList<T> result = new ArrayList<>();
    while (rs.next()) {
        Constructor<T> ctors = theclass.getDeclaredConstructor();
        T item = ctors.newInstance();

        for (var field : theclass.getDeclaredFields()) {
            DBField dbField = field.getAnnotation(DBField.class);
            if (dbField != null) {
                field.set(item,
                        getFromDB.get(dbField.type()).
                                getData(dbField.column_name()));
            }
        }

        result.add(item);
    }
    result.stream().forEach(x -> System.out.println(x));
}

public void deleteById(int id) throws SQLException {
    Statement statement = connection.createStatement();
    statement.setQueryTimeout(30);
    statement.execute("DELETE FROM " +table_name+" WHERE id = "+String.valueOf(id));
    System.out.println("The row has been deleted");
}

public void insert(T obj) throws SQLException, IllegalAccessException {
    Statement statement = connection.createStatement();
    statement.setQueryTimeout(30);
    String query = "INSERT INTO "+table_name+" (";
    for(var field: obj.getClass().getDeclaredFields()) {
        if (!field.getName().equals("TABLE_NAME")){
            query += field.getName() + ",";
        }
    }
    query = query.substring(0,query.lastIndexOf(","));
    query+= ")\n";
    query+="VALUES (";

    for (var values : obj.getClass().getDeclaredFields()){
            if (!values.getName().equals("TABLE_NAME")) {
                if (values.get(obj).getClass().equals(String.class)) {
                    query += "'" + values.get(obj) + "',";
                } else {
                    query += values.get(obj) + ",";
                }
            }
    }
    query = query.substring(0,query.lastIndexOf(","));
    query += ");";
    statement.executeUpdate(query);
    System.out.println("insert success");
}

public void update(T obj,int id) throws SQLException, IllegalAccessException {
    Statement statement = connection.createStatement();
    statement.setQueryTimeout(30);
    String query = "UPDATE " + table_name +"\n"+
            "SET ";

    for(var field: obj.getClass().getDeclaredFields()) {
        DBField dbField = field.getAnnotation(DBField.class);
        if (!field.getName().equals("TABLE_NAME")&& !field.getName().equals("id") && !dbField.isPrimaryKey()){
            if (field.get(obj).getClass().equals(String.class)){
                query += field.getName() + " = '" + field.get(obj) +"',";
            }
            else {
                query += field.getName() + " = " + field.get(obj) +",";
            }
        }
    }
    query = query.substring(0,query.lastIndexOf(","));
    query+="\nWHERE id =" + String.valueOf(id);
    System.out.println(query);
    statement.executeUpdate(query);
    System.out.println("The table has been updated");
}
public void deleteTable() throws SQLException {
    Statement statement = connection.createStatement();
    statement.setQueryTimeout(30);
    statement.execute("drop table "+table_name);
    System.out.println("The table has been deleted");
}
}

