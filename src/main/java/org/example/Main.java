package org.example;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException, InvocationTargetException, NoSuchMethodException {
        GameManger gameManger = new GameManger();
        String[] countries = {"","United States", "Canada", "Mexico", "Brazil", "Argentina", "Peru", "United Kingdom",
                "Germany", "France", "Spain", "Italy", "Russia", "China", "Japan", "South Korea",
                "India", "Australia", "South Africa", "Egypt", "Nigeria"};

     for (int i = 1; i < countries.length ; i++){
         new OlympicRunner(String.format("t%d",i),countries[i],gameManger).start();
     }


        Person p = new Person(1, "danny", "Holon", 1.80f);

        Customer customer1 = new Customer(1, "tal", "abutbul", 74.2f, "Holon", "tal6203@gmail.com");
        Customer customer2 = new Customer(1, "Eden", "Alon", 55f, "Tel-Aviv", "aaa@gmail.com");

        GenericTableAccess<Customer> genericTableAccess = null;
        try {
            genericTableAccess = new GenericTableAccess<>(
                    "jdbc:sqlite:C:\\project_java\\hw43\\hw43\\db2.db",
                    Customer.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
                genericTableAccess.createTable();
        genericTableAccess.insert(customer1);
        var result = genericTableAccess.readFromTable();
        System.out.println(result);
        genericTableAccess.getBYId(1);
        genericTableAccess.update(customer2,1);
        genericTableAccess.deleteById(1);
        genericTableAccess.deleteTable();

    }
}