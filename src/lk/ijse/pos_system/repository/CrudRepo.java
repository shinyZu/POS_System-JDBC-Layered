package lk.ijse.pos_system.repository;

import java.sql.SQLException;

public interface CrudRepo<T,ID> extends SuperRepo {

    boolean add(T t) throws SQLException, ClassNotFoundException;

    boolean delete(T t) throws SQLException, ClassNotFoundException;

    boolean update(T t) throws SQLException, ClassNotFoundException;

}
