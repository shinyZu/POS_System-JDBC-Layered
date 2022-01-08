package lk.ijse.pos_system.business.custom.impl;

import lk.ijse.pos_system.business.custom.VerifyUserBO;
import lk.ijse.pos_system.repository.RepoFactory;
import lk.ijse.pos_system.repository.custom.UserRepo;

import java.sql.SQLException;

public class VerifyUserBOImpl implements VerifyUserBO {

    private final UserRepo userRepo = (UserRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.USER);

    @Override
    public boolean verifyUser(String userRole, String username, String tPassword, String fPassword) throws SQLException, ClassNotFoundException {
        return userRepo.verifyUser(userRole, username, tPassword, fPassword);
    }
}
