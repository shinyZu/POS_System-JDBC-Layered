package lk.ijse.pos_system.business.custom.impl;

import lk.ijse.pos_system.business.custom.ReportBO;
import lk.ijse.pos_system.repository.RepoFactory;
import lk.ijse.pos_system.repository.custom.OrderDetailRepo;
import lk.ijse.pos_system.repository.custom.QueryRepo;
import lk.ijse.pos_system.dto.CustomDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public class ReportBOImpl implements ReportBO {

    private final OrderDetailRepo orderDetailRepo = (OrderDetailRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ORDERDETAIL);
    private final QueryRepo queryRepo = (QueryRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.QUERY);

    @Override
    public ArrayList<CustomDTO> getDailyReport(String date) throws SQLException, ClassNotFoundException {
        return queryRepo.getDailyReport(date);
    }

    @Override
    public ArrayList<CustomDTO> getCustomerWiseIncome(String date) throws SQLException, ClassNotFoundException {
        return queryRepo.getCustomerWiseIncome(date);
    }

    @Override
    public String getMostMovableItem(String reportType, String date) throws SQLException, ClassNotFoundException {
        return queryRepo.getMostMovableItem(reportType, date);
    }

    @Override
    public String getLeastMovableItem(String reportType, String date) throws SQLException, ClassNotFoundException {
        return queryRepo.getLeastMovableItem(reportType, date);
    }

    @Override
    public ArrayList<CustomDTO> getMonthlyReport(String date) throws SQLException, ClassNotFoundException {
        return queryRepo.getMonthlyReport(date);
    }

    @Override
    public ArrayList<CustomDTO> getAnnualReport(String date) throws SQLException, ClassNotFoundException {
        return queryRepo.getAnnualReport(date);
    }
}
