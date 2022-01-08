package lk.ijse.pos_system.repository.custom;

import lk.ijse.pos_system.repository.SuperRepo;

import java.sql.SQLException;

public interface UserRepo extends SuperRepo {

    boolean verifyUser(String userRole, String userName, String tPassword, String fPassword) throws SQLException, ClassNotFoundException;
}
