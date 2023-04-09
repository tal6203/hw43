package org.example;

public class Person {
    @DBFieldTableName(table_name =  "person")
    public static String TABLE_NAME = "person";

    public Person() {
    }

    public Person(int id, String name,String address,Float height) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.height = height;
    }

    @DBField(column_name = "id", isPrimaryKey = true, type = int.class)
    public int id;

    @DBField(column_name = "name", type = String.class)
    public String name;

    @DBField(column_name = "address", type = String.class)
    public String address;

    @DBField(column_name = "height", type = Float.class)
    public Float height;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", adress='" + address + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
}
