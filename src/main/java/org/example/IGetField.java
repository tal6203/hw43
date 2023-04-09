package org.example;
import java.sql.SQLException;

public interface IGetField<T> {

    T getData(String column_name) throws SQLException;

}