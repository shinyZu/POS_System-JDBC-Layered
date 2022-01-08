package lk.ijse.pos_system.business.custom;

import lk.ijse.pos_system.business.SuperBO;
import lk.ijse.pos_system.dto.CustomerDTO;
import lk.ijse.pos_system.dto.ItemDTO;
import lk.ijse.pos_system.dto.OrderDTO;
import lk.ijse.pos_system.dto.OrderDetailDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface PurchaseOrderBO extends SuperBO {

    boolean purchaseOrder(OrderDTO newOrder, ArrayList<OrderDetailDTO> items) throws SQLException, ClassNotFoundException;

    String getOrderId() throws SQLException, ClassNotFoundException;

    List<String> getCustomerIds() throws SQLException, ClassNotFoundException;

    List<String> getItemCodes() throws SQLException, ClassNotFoundException;

    List<String> getItemDescriptions() throws SQLException, ClassNotFoundException;

    ItemDTO getItem(String itemCode) throws SQLException, ClassNotFoundException;

    String getDiscount(String itemCode) throws SQLException, ClassNotFoundException;

    String getItemCode(String description) throws SQLException, ClassNotFoundException;

    CustomerDTO getCustomer(String custIdForSearch) throws SQLException, ClassNotFoundException;

    boolean addCustomer(CustomerDTO newCust) throws SQLException, ClassNotFoundException;

    int splitPackSize(String value, String text) throws SQLException, ClassNotFoundException;

    String generateCustomerID() throws SQLException, ClassNotFoundException;

    String generateInvoiceId(String invoiceNo) throws SQLException, ClassNotFoundException;

    CustomerDTO getCustomerOfOrder(String orderId) throws SQLException, ClassNotFoundException;
}
