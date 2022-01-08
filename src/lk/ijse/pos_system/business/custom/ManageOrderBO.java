package lk.ijse.pos_system.business.custom;

import lk.ijse.pos_system.business.SuperBO;
import lk.ijse.pos_system.dto.CustomDTO;
import lk.ijse.pos_system.dto.OrderDTO;
import lk.ijse.pos_system.dto.OrderDetailDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ManageOrderBO extends SuperBO {

    List<String> searchOrdersByCustID(String custID) throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> getOrderedItems(String orderSelected, CustomDTO itemSelected) throws SQLException, ClassNotFoundException;

    ArrayList<Double> getOldPaymentInfo(String orderSelected) throws SQLException, ClassNotFoundException;

    int splitPackSize(String itemCode, String packSize) throws SQLException, ClassNotFoundException;

    boolean deleteItemFromOrder(OrderDetailDTO itemToRemove) throws SQLException, ClassNotFoundException;

    boolean editQtyOnHand(String itemCode, int qtyToRestock) throws SQLException, ClassNotFoundException;

    void deleteOrder(OrderDTO orderToDelete) throws SQLException, ClassNotFoundException;

    boolean updateOrder(OrderDTO orderToBeUpdated, OrderDetailDTO orderDetailToBeUpdated, int newQtyOnHand) throws SQLException, ClassNotFoundException;

    boolean updateOrderAndOrderDetail(OrderDTO orderToBeUpdated, OrderDetailDTO orderDetailToBeUpdated) throws SQLException, ClassNotFoundException;

    String getDiscount(String itemCode) throws SQLException, ClassNotFoundException;

    boolean isCustomerExists(String custID) throws SQLException, ClassNotFoundException;
}
