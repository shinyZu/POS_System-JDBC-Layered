package lk.ijse.pos_system.repository.custom;

import lk.ijse.pos_system.dto.CustomerDTO;
import lk.ijse.pos_system.entity.Customer;
import lk.ijse.pos_system.repository.CrudRepo;

import java.sql.SQLException;
import java.util.List;

public interface CustomerRepo extends CrudRepo<Customer, String> {

    Customer getCustomer(String id) throws SQLException, ClassNotFoundException;

    List<String> getCustomerIds() throws SQLException, ClassNotFoundException;

    String generateCustomerID() throws SQLException, ClassNotFoundException;

    CustomerDTO getCustomerOfOrder(String orderId) throws SQLException, ClassNotFoundException;

    boolean isCustomerExists(String custID) throws SQLException, ClassNotFoundException;

}
