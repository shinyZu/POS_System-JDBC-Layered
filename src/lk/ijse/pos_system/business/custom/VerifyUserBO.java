package lk.ijse.pos_system.business.custom;

import lk.ijse.pos_system.business.SuperBO;

import java.sql.SQLException;

public interface VerifyUserBO extends SuperBO {

    boolean verifyUser(String userRole, String username, String tPassword, String fPassword) throws SQLException, ClassNotFoundException;
}
