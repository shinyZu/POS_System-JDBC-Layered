package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.util.CrudUtil;
import lk.ijse.pos_system.repository.custom.UserRepo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepoImpl implements UserRepo {

    @Override
    public boolean verifyUser(String userRole, String userName, String tPassword, String fPassword) throws SQLException, ClassNotFoundException {

        ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM UserDetail WHERE userName = ?", userName);

        if (userName.equals("") || fPassword.equals("")) {
            //new Alert(Alert.AlertType.WARNING,"Please fill the required fields...", ButtonType.OK).show();
            return false;
        }

        if (resultSet.next()) {
            if (userRole.equals("Admin")) {
                if (resultSet.getObject("userType").equals("ADMIN")) {
                    if (resultSet.getString("userName").equals(userName)
                            && (resultSet.getString("userPassword").equals(fPassword)) | resultSet.getString("userPassword").equals(tPassword)) {
                        return true;

                    } else {
                        //new Alert(Alert.AlertType.WARNING, "Invalid User...").show();
                        return false;
                    }
                } else {
                    //new Alert(Alert.AlertType.WARNING, "Invalid User...").show();
                    return false;
                }


            } else if (userRole.equals("Cashier")) {
                if (resultSet.getObject("userType").equals("CASHIER")) {
                    if (resultSet.getString("userName").equals(userName)
                            && (resultSet.getString("userPassword").equals(fPassword)) | resultSet.getString("userPassword").equals(tPassword)) {
                        return true;
                    } else {
                       // new Alert(Alert.AlertType.WARNING, "Invalid User...").show();
                        return false;
                    }
                } else {
                    //new Alert(Alert.AlertType.WARNING, "Invalid User...").show();
                    return false;
                }

            } else {
                //new Alert(Alert.AlertType.WARNING, "Invalid User...").show();
                return false;
            }
        }
        return false;
    }
}
