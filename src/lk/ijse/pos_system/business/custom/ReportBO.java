package lk.ijse.pos_system.business.custom;

import lk.ijse.pos_system.business.SuperBO;
import lk.ijse.pos_system.dto.CustomDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ReportBO extends SuperBO {

    ArrayList<CustomDTO> getDailyReport(String date) throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> getCustomerWiseIncome(String date) throws SQLException, ClassNotFoundException;

    String getMostMovableItem(String reportType, String date) throws SQLException, ClassNotFoundException;

    String getLeastMovableItem(String reportType, String date) throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> getMonthlyReport(String date) throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> getAnnualReport(String date) throws SQLException, ClassNotFoundException;
}
