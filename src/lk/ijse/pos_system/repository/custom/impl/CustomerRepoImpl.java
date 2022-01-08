package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.util.CrudUtil;
import lk.ijse.pos_system.repository.custom.CustomerRepo;
import lk.ijse.pos_system.dto.CustomerDTO;
import lk.ijse.pos_system.entity.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepoImpl implements CustomerRepo {

    @Override
    public boolean add(Customer newCust) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate("INSERT INTO Customer VALUES (?,?,?,?,?,?,?)",
                newCust.getCustID(),
                newCust.getCustTitle(),
                newCust.getCustName(),
                newCust.getCustAddress(),
                newCust.getCity(),
                newCust.getProvince(),
                newCust.getPostalCode()
        );
    }

    @Override
    public boolean delete(Customer customer) throws SQLException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not Supported Yet");
    }

    @Override
    public boolean update(Customer customer) throws SQLException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not Supported Yet");
    }

    @Override
    public Customer getCustomer(String id) throws SQLException, ClassNotFoundException {

        ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM Customer WHERE custID = ?",id);

        if (resultSet.next()){
            return new Customer(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getString(6),
                    resultSet.getString(7)
            );
        }else {
            return null;
        }
    }

    @Override
    public List<String> getCustomerIds() throws SQLException, ClassNotFoundException {

        ResultSet rst = CrudUtil.executeQuery("SELECT * FROM Customer");

        List<String> custIDList = new ArrayList<>();
        while (rst.next()){
            custIDList.add(
                    rst.getString(1)
            );
        }
        return custIDList;
    }

    @Override
    public String generateCustomerID() throws SQLException, ClassNotFoundException {

        ResultSet rst = CrudUtil.executeQuery("SELECT * FROM Customer ORDER BY custID DESC LIMIT 1");

        if (rst.next()){
            int tempId = Integer.parseInt(rst.getString(1).split("-")[1]);
            tempId = tempId+1;

            if (tempId <= 9){
                return "C-00" + tempId;
            }else if (tempId <= 99){
                return "C-0" + tempId;
            }else {
                return "C-" + tempId;
            }

        }else {
            return "C-001";
        }
    }

    @Override
    public CustomerDTO getCustomerOfOrder(String orderId) throws SQLException, ClassNotFoundException {

        ResultSet rst = CrudUtil.executeQuery("SELECT c.custID, c.custTitle, c.custName\n" +
                "FROM Customer c INNER JOIN Orders o\n" +
                "ON c.custID = o.custID\n" +
                "WHERE o.orderId = ?", orderId);

        if (rst.next()) {
            return new CustomerDTO(rst.getString(1),rst.getString(2),rst.getString(3));
        }
        return null;
    }

    @Override
    public boolean isCustomerExists(String custID) throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtil.executeQuery("SELECT * FROM Customer WHERE custID = ?", custID);

        if (rst.next()) {
            return true;
        }
        return false;
    }
}
