package lk.ijse.pos_system.repository.custom;

import lk.ijse.pos_system.dto.CustomDTO;
import lk.ijse.pos_system.repository.SuperRepo;

import java.sql.SQLException;
import java.util.ArrayList;

public interface QueryRepo extends SuperRepo {

    ArrayList<CustomDTO> getCustomerWiseIncome(String date) throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> getOrderedItems(String orderSelected, CustomDTO itemSelected) throws SQLException, ClassNotFoundException;

    ArrayList<Double> getOldPaymentInfo(String orderSelected) throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> getAllItems() throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> getDailyReport(String date) throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> getMonthlyReport(String date) throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> getAnnualReport(String date) throws SQLException, ClassNotFoundException;

    String getMostMovableItem(String reportType, String date) throws SQLException, ClassNotFoundException;

    String getLeastMovableItem(String reportType, String date) throws SQLException, ClassNotFoundException;
}
